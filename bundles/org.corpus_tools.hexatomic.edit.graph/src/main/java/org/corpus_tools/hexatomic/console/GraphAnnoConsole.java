/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.console;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewNodeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.StartContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.Token_node_referenceContext;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class GraphAnnoConsole implements Runnable, IDocumentListener, VerifyListener {

	private static final int MAX_HISTORY_LENGTH = 1000;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphAnnoConsole.class);

	private final IDocument document;

	private final UISynchronize sync;

	private final SDocumentGraph graph;

	private final SourceViewer view;

	private String prompt;

	private final LinkedList<String> commandHistory = new LinkedList<>();

	/**
	 * Constructs a new console.
	 * 
	 * @param view  The view widget the console view is using
	 * @param sync  An Eclipse synchronization object.
	 * @param graph The Salt graph to edit.
	 */
	public GraphAnnoConsole(SourceViewer view, UISynchronize sync, SDocumentGraph graph) {
		this.document = view.getDocument();
		this.sync = sync;
		this.graph = graph;
		this.view = view;
		this.prompt = "> ";

		this.document.addDocumentListener(this);

		StyledText styledText = view.getTextWidget();
		styledText.setDoubleClickEnabled(true);
		styledText.setEditable(true);
		styledText.addVerifyListener(this);
		styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

		styledText.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.character == '+' && ((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
					FontData[] fd = styledText.getFont().getFontData();

					fd[0].setHeight(fd[0].getHeight() + 1);
					styledText.setFont(new Font(Display.getCurrent(), fd[0]));

				} else if (e.character == '-' && ((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
					FontData[] fd = styledText.getFont().getFontData();

					if (fd[0].getHeight() > 6) {
						fd[0].setHeight(fd[0].getHeight() - 1);
					}
					styledText.setFont(new Font(Display.getCurrent(), fd[0]));
				}

			}
		});

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		writeLine("To display a list of available commands, type \"help\".\n");
		writePrompt();

	}

	private void writeLine(String str) {
		sync.asyncExec(() -> {
			document.set(document.get() + str + "\n");
			view.getTextWidget().setCaretOffset(document.getLength());
			view.getTextWidget().setTopIndex(view.getTextWidget().getLineCount() - 1);
		});
	}

	private void writePrompt() {
		sync.asyncExec(() -> {
			document.set(document.get() + this.prompt);
			view.getTextWidget().setCaretOffset(document.getLength());
			view.getTextWidget().setTopIndex(view.getTextWidget().getLineCount() - 1);
		});
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		if (event.getOffset() > 0 && event.getText().endsWith("\n")) {
			int nrLines = event.getDocument().getNumberOfLines();
			try {
				if (nrLines >= 2) {
					IRegion lineRegion = document.getLineInformation(nrLines - 2);
					String lastLine = document.get(lineRegion.getOffset(), lineRegion.getLength())
							.substring(prompt.length());

					executeCommand(lastLine);

				}
			} catch (BadLocationException e) {
				log.error("Bad location in console, no last line", e);
			}
			writePrompt();
		}
	}

	private void executeCommand(String cmd) {
		
		// add to history
		commandHistory.add(cmd);
		if(commandHistory.size() > MAX_HISTORY_LENGTH) {
			commandHistory.removeFirst();
		}
		
		// parse the line
		ConsoleCommandLexer lexer = new ConsoleCommandLexer(CharStreams.fromString(cmd));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConsoleCommandParser parser = new ConsoleCommandParser(tokens);

		ErrorListener errors = new ErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errors);

		StartContext startCtx = parser.start();

		if (errors.errors.isEmpty()) {
			// Collect the relevant elements of the AST and execute the command
			ParseTreeWalker walker = new ParseTreeWalker();
			CommandTreeListener listener = new CommandTreeListener();
			walker.walk(listener, startCtx);
		} else {
			// Output the errors
			for (String e : errors.errors) {
				writeLine(e);
			}
		}
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {

	}

	@Override
	public void verifyText(VerifyEvent e) {
		// get the line this event belongs to
		try {
			int lineNr = document.getLineOfOffset(e.start);

			if (lineNr < document.getNumberOfLines() - 1) {
				e.doit = false;
			} else {
				// check if the position is right of the prompt
				if (e.start >= document.getLineOffset(lineNr) + prompt.length()) {
					e.doit = true;
				} else {
					e.doit = false;
				}
			}

		} catch (BadLocationException ex) {
			log.error("Could not get line location for offset", ex);
			e.doit = false;
		}

	}

	private class ErrorListener extends BaseErrorListener {

		final List<String> errors = new LinkedList<>();

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {

			StringBuilder errorMsg = new StringBuilder();

			// add a marker to the above line
			for (int i = 0; i < charPositionInLine + prompt.length(); i++) {
				errorMsg.append(' ');
			}
			errorMsg.append("^");
			errorMsg.append("\n");
			errorMsg.append(msg);

			errors.add(errorMsg.toString());
		}
	}

	private class CommandTreeListener extends ConsoleCommandBaseListener {

		private Set<SToken> tokenReferences = new LinkedHashSet<SToken>();

		@Override
		public void enterToken_node_reference(Token_node_referenceContext ctx) {
			List<SNode> matchedNodes = graph.getNodesByName(ctx.name.getText());
			if (matchedNodes != null) {
				for (SNode n : matchedNodes) {
					if (n instanceof SToken) {
						tokenReferences.add((SToken) n);
					}
				}
			}
		}

		@Override
		public void exitNewNode(NewNodeContext ctx) {

			SStructure n = graph.createStructure(tokenReferences.toArray(new SStructuredNode[tokenReferences.size()]));
			if (n != null) {
				writeLine("Created new structure node " + n.getId());
			}
		}
	}

}
