/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.StartContext;
import org.corpus_tools.hexatomic.console.internal.SyntaxListener;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;

/**
 * Executes commands on an annotation graph.
 * 
 * @author Thomas Krause {@literal krauseto@hu-berlin.de}
 *
 */
public class ConsoleController {

  private static final int MAX_HISTORY_LENGTH = 1000;

  private SDocumentGraph graph;
  private STextualDS selectedText;

  private final LinkedList<String> commandHistory = new LinkedList<>();
  private ListIterator<String> itCommandHistory;

  private String prompt;

  /**
   * Constructor.
   * 
   * @param graph The annotation graph to manipulate.
   */
  public ConsoleController(SDocumentGraph graph) {
    this.graph = graph;
    this.prompt = "> ";
  }

  /**
   * Takes the raw string representation of a command, parses and executes it.
   * 
   * @param cmd The command.
   * @return The output as list of lines.
   */
  public List<String> executeCommand(String cmd) {

    // add to history
    if (!cmd.isEmpty()) {
      commandHistory.push(cmd);
      itCommandHistory = commandHistory.listIterator();
      if (commandHistory.size() > MAX_HISTORY_LENGTH) {
        commandHistory.removeLast();
      }
    }

    // parse the line
    ErrorListener errorListener = new ErrorListener();

    ConsoleLexer lexer = new ConsoleLexer(CharStreams.fromString(cmd));
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ConsoleCommandParser parser = new ConsoleCommandParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    StartContext startCtx = parser.start();

    List<String> output = new LinkedList<String>();

    if (errorListener.errors.isEmpty()) {
      // Collect the relevant elements of the AST and execute the command
      ParseTreeWalker walker = new ParseTreeWalker();
      SyntaxListener listener = new SyntaxListener(graph, getSelectedText());
      walker.walk(listener, startCtx);
      output.addAll(listener.getOutputLines());
    } else {
      // Output the errors
      for (String e : errorListener.errors) {
        output.add(e);
      }
    }

    return output;
  }

  public String getPrompt() {
    return prompt;
  }

  public ListIterator<String> getCommandHistoryIterator() {
    return itCommandHistory;
  }

  public SDocumentGraph getGraph() {
    return graph;
  }

  /**
   * Sets the used document graph. This also resets the selected text to point to the textual data
   * source from the new graph that has the same name.
   * 
   * @param graph The graph to set.
   */
  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
    if (this.selectedText != null) {
      // We need to update the selected text, since the old one might not be the same object
      String selectedTextName = this.selectedText.getName();
      this.selectedText = null;
      if (this.graph != null) {
        for (STextualDS ds : this.graph.getTextualDSs()) {
          if (Objects.equals(ds.getName(), selectedTextName)) {
            this.selectedText = ds;
            break;
          }
        }
      }
    }
  }

  public void setSelectedText(STextualDS selectedText) {
    this.selectedText = selectedText;
  }

  protected STextualDS getSelectedText() {
    if (this.selectedText == null) {
      // Find the first data source of the current graph
      if (this.graph != null) {
        List<STextualDS> texts = this.graph.getTextualDSs();
        if (texts != null && !texts.isEmpty()) {
          return texts.get(0);
        }
      }
    } else {
      return this.selectedText;
    }

    return null;
  }

  private final class ErrorListener extends BaseErrorListener {

    final List<String> errors = new LinkedList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e) {

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

}
