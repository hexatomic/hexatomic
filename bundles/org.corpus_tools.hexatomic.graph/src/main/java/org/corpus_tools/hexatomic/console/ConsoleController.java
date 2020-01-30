package org.corpus_tools.hexatomic.console;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.StartContext;
import org.corpus_tools.hexatomic.console.internal.SyntaxListener;
import org.corpus_tools.salt.common.SDocumentGraph;

/**
 * Executes commands on an annotation graph.
 * 
 * @author Thomas Krause
 *
 */
public class ConsoleController {

  private static final int MAX_HISTORY_LENGTH = 1000;

  private final SDocumentGraph graph;

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
    ConsoleLexer lexer = new ConsoleLexer(CharStreams.fromString(cmd));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ConsoleCommandParser parser = new ConsoleCommandParser(tokens);

    parser.removeErrorListeners();
    ErrorListener errorListener = new ErrorListener();
    parser.addErrorListener(errorListener);

    StartContext startCtx = parser.start();

    List<String> output = new LinkedList<String>();
    
    if (errorListener.errors.isEmpty()) {
      // Collect the relevant elements of the AST and execute the command
      ParseTreeWalker walker = new ParseTreeWalker();
      SyntaxListener listener = new SyntaxListener(graph);
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
