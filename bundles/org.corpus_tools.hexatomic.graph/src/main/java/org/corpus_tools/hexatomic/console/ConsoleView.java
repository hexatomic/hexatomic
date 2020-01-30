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

import com.google.common.collect.Range;
import java.util.ListIterator;
import java.util.Optional;
import org.corpus_tools.salt.common.SDocumentGraph;
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
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class ConsoleView implements Runnable, IDocumentListener, VerifyListener {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConsoleView.class);

  private final IDocument document;

  private final UISynchronize sync;

  private final ConsoleController controller;

  private final SourceViewer view;

  /**
   * Constructs a new console.
   * 
   * @param view The view widget the console view is using
   * @param sync An Eclipse synchronization object.
   * @param graph The Salt graph to edit.
   */
  public ConsoleView(SourceViewer view, UISynchronize sync, SDocumentGraph graph) {
    this.document = view.getDocument();
    this.sync = sync;
    this.view = view;
    this.controller = new ConsoleController(graph);

    this.document.addDocumentListener(this);

    StyledText styledText = view.getTextWidget();
    styledText.setDoubleClickEnabled(true);
    styledText.setEditable(true);
    styledText.addVerifyListener(this);
    styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

    styledText.addVerifyKeyListener(new ActionKeyListener(styledText));

    Thread t = new Thread(this);
    t.start();
  }

  @Override
  public void run() {
    writeLine(
        "See the Hexatomic User guide (https://hexatomic.github.io/hexatomic/user/) for help.\n");
    writePrompt();

  }

  /**
   * Outputs a string in the console.
   * 
   * @param str The string to output (without trailing new line).
   */
  public void writeLine(String str) {
    sync.asyncExec(() -> {
      document.set(document.get() + str + "\n");
      view.getTextWidget().setCaretOffset(document.getLength());
      view.getTextWidget().setTopIndex(view.getTextWidget().getLineCount() - 1);
    });
  }

  private void writePrompt() {
    sync.asyncExec(() -> {
      document.set(document.get() + controller.getPrompt());
      view.getTextWidget().setCaretOffset(document.getLength());
      view.getTextWidget().setTopIndex(view.getTextWidget().getLineCount() - 1);
    });
  }

  @Override
  public void documentChanged(DocumentEvent event) {
    if (event.getDocument() == document && event.getOffset() > 0
        && event.getText().endsWith("\n")) {
      int nrLines = event.getDocument().getNumberOfLines();
      try {
        if (nrLines >= 2) {
          IRegion lineRegion = document.getLineInformation(nrLines - 2);
          String lastLine = document.get(lineRegion.getOffset(), lineRegion.getLength())
              .substring(controller.getPrompt().length());

          controller.executeCommand(lastLine);

        }
      } catch (BadLocationException e) {
        log.error("Bad location in console, no last line", e);
      }
      writePrompt();
    }
  }

  private Optional<Range<Integer>> getPromptRange() {
    int numberOfLines = document.getNumberOfLines();
    if (numberOfLines > 0) {
      try {
        IRegion lineRegion = document.getLineInformation(numberOfLines - 1);
        Range<Integer> promptRange =
            Range.closed(lineRegion.getOffset() + controller.getPrompt().length(),
                lineRegion.getOffset() + lineRegion.getLength());
        return Optional.of(promptRange);
      } catch (BadLocationException e) {
        log.error("Something went wrong when getting the last line of the document", e);
      }
    }
    return Optional.empty();
  }


  private void setCommand(String cmd) {
    if (cmd == null) {
      return;
    }
    cmd = cmd.trim();

    // get the current command as text ran
    Optional<Range<Integer>> promptRange = getPromptRange();
    if (promptRange.isPresent()) {
      try {
        document.replace(promptRange.get().lowerEndpoint(),
            promptRange.get().upperEndpoint() - promptRange.get().lowerEndpoint(), cmd);
        view.getTextWidget().setCaretOffset(document.getLength());
      } catch (BadLocationException ex) {
        log.error("Something went wrong when setting the history entry", ex);
      }
    }

  }


  @Override
  public void documentAboutToBeChanged(DocumentEvent event) {

  }

  @Override
  public void verifyText(VerifyEvent e) {
    e.doit = isOffsetPartOfCmd(e.start);
  }

  private boolean isOffsetPartOfCmd(int offset) {
    Optional<Range<Integer>> promptOffset = getPromptRange();
    if (promptOffset.isPresent()) {
      boolean result = promptOffset.get().contains(offset);
      return result;
    }
    return false;
  }

  private final class ActionKeyListener implements VerifyKeyListener {
    private final StyledText styledText;

    private ActionKeyListener(StyledText styledText) {
      this.styledText = styledText;
    }

    @Override
    public void verifyKey(VerifyEvent e) {
      boolean ctrlActive = (e.stateMask & SWT.CTRL) == SWT.CTRL;

      ListIterator<String> itCommandHistory = controller.getCommandHistoryIterator();

      if (ctrlActive) {
        if (e.character == '+') {
          e.doit = false;
          FontData[] fd = styledText.getFont().getFontData();

          fd[0].setHeight(fd[0].getHeight() + 1);
          styledText.setFont(new Font(Display.getCurrent(), fd[0]));

        } else if (e.character == '-') {
          e.doit = false;
          FontData[] fd = styledText.getFont().getFontData();

          if (fd[0].getHeight() > 6) {
            fd[0].setHeight(fd[0].getHeight() - 1);
          }
          styledText.setFont(new Font(Display.getCurrent(), fd[0]));
        }
      } else if (e.keyCode == SWT.ARROW_UP) {
        if (isOffsetPartOfCmd(view.getTextWidget().getCaretOffset())) {
          e.doit = false;
          if (itCommandHistory != null && itCommandHistory.hasNext()) {
            String oldCommand = itCommandHistory.next();
            if (oldCommand != null) {
              setCommand(oldCommand);
            }
          }
        }
      } else if (e.keyCode == SWT.ARROW_DOWN) {
        if (isOffsetPartOfCmd(view.getTextWidget().getCaretOffset())) {
          e.doit = false;
          if (itCommandHistory != null && itCommandHistory.hasPrevious()) {
            String oldCommand = itCommandHistory.previous();
            if (oldCommand != null) {
              setCommand(oldCommand);
            }
          }
        }
      } else if (e.keyCode == '\r') {
        // make sure the cursor is set to the end of the line
        styledText.setCaretOffset(document.getLength());
      }

    }
  }


}
