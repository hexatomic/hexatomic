package org.corpus_tools.hexatomic.console;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestConsoleController {

  private ConsoleController console;
  private SDocumentGraph graph;


  @BeforeEach
  void setUp() throws Exception {

    graph = SaltFactory.createSDocumentGraph();
    console = new ConsoleController(graph);
  }

  @Test
  void testExampleNewNode() {
    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();

    // Add some annnotated nodes
    console.executeCommand("n cat:NP #t3 #t4");
    console.executeCommand("n tiger:cat:NP #t1");
    console.executeCommand("n cat:VP #t2 #n1");
    console.executeCommand("n cat:S #n2 #n3");

    // Check the created graph
    graph.getSortedTokenByText();
    assertEquals(4, graph.getStructures().size());
    SNode n1 = graph.getNodesByName("n1").get(0);
    assertEquals("NP", n1.getAnnotation("cat").getValue());
    assertEquals(2, n1.getOutRelations().size());
    assertEquals(graph.getTokens().get(2), n1.getOutRelations().get(0).getTarget());
    assertEquals(graph.getTokens().get(3), n1.getOutRelations().get(1).getTarget());


    SNode n2 = graph.getNodesByName("n2").get(0);
    assertEquals("NP", n2.getAnnotation("tiger::cat").getValue());
    assertEquals(1, n2.getOutRelations().size());
    assertEquals(graph.getTokens().get(0), n2.getOutRelations().get(0).getTarget());

    SNode n3 = graph.getNodesByName("n3").get(0);
    assertEquals("VP", n3.getAnnotation("cat").getValue());
    assertEquals(2, n3.getOutRelations().size());
    assertEquals(graph.getTokens().get(1), n3.getOutRelations().get(0).getTarget());
    assertEquals(n1, n3.getOutRelations().get(1).getTarget());

    SNode n4 = graph.getNodesByName("n4").get(0);
    assertEquals("S", n4.getAnnotation("cat").getValue());
    assertEquals(2, n4.getOutRelations().size());
    assertEquals(n2, n4.getOutRelations().get(0).getTarget());
    assertEquals(n3, n4.getOutRelations().get(1).getTarget());
  }

  @Test
  void testExampleAddEdge() {
    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();

    // Add some annnotated nodes
    console.executeCommand("n cat:NP #t3 #t4");
    console.executeCommand("n tiger:cat:NP #t1");
    console.executeCommand("n cat:VP #t2 #n1");
    console.executeCommand("n cat:S #n2 #n3");

    // Add dependency and dominance edge
    console.executeCommand("e #t2 -> #t1 func:nsubj");
    console.executeCommand("e #n4 > #t5");

    // Check the created graph
    graph.getSortedTokenByText();

    SNode t1 = graph.getNodesByName("t1").get(0);
    SNode t2 = graph.getNodesByName("t2").get(0);

    List<SRelation<?, ?>> pointing = t2.getOutRelations().stream()
        .filter((rel) -> rel instanceof SPointingRelation).collect(Collectors.toList());
    assertEquals(1, pointing.size());
    assertEquals(t1, pointing.get(0).getTarget());
    assertEquals("nsubj", pointing.get(0).getAnnotation("func").getValue());

    SNode n4 = graph.getNodesByName("n4").get(0);
    assertEquals(3, n4.getOutRelations().size());
    assertEquals(graph.getTokens().get(4), n4.getOutRelations().get(2).getTarget());
  }

  @Test
  void testExampleAnnotate() {
    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();
    
    // Add initial pointing relation
    console.executeCommand("e #t2 -> #t1 func:nsubj");
    assertEquals(1, graph.getPointingRelations().size());
    assertEquals(1, graph.getPointingRelations().get(0).getAnnotations().size());

    // Add token annotation
    console.executeCommand("a pos:DT #t1 #t3");

    assertEquals("DT", graph.getTokens().get(0).getAnnotation("pos").getValue());
    assertEquals("DT", graph.getTokens().get(2).getAnnotation("pos").getValue());

    // Delete existing annotation
    console.executeCommand("a pos: #t1");
    assertEquals(null, graph.getTokens().get(0).getAnnotation("pos"));
    assertEquals("DT", graph.getTokens().get(2).getAnnotation("pos").getValue());
    
    // Add annotation to edge
    console.executeCommand("a func_alt:nominal_subject #t2 -> #t1");
    assertEquals(1, graph.getPointingRelations().size());
    assertEquals(2, graph.getPointingRelations().get(0).getAnnotations().size());

  }

  @Test
  void testExampleTokenize() {

    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();
    List<SToken> tokens = graph.getTokens();
    assertEquals(5, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This is an example .", graph.getTextualDSs().get(0).getText());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("is", graph.getText(tokens.get(1)));
    assertEquals("an", graph.getText(tokens.get(2)));
    assertEquals("example", graph.getText(tokens.get(3)));
    assertEquals(".", graph.getText(tokens.get(4)));

    // Append two tokens
    console.executeCommand("t Not .");

    assertEquals("This is an example . Not .", graph.getTextualDSs().get(0).getText());
    graph.sortTokenByText();
    tokens = graph.getTokens();
    assertEquals(7, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("is", graph.getText(tokens.get(1)));
    assertEquals("an", graph.getText(tokens.get(2)));
    assertEquals("example", graph.getText(tokens.get(3)));
    assertEquals(".", graph.getText(tokens.get(4)));
    assertEquals("Not", graph.getText(tokens.get(5)));
    assertEquals(".", graph.getText(tokens.get(6)));

  }


  @Test
  void testExampleTokenizeAfterBefore() {
    // Add initial tokens
    console.executeCommand("t This text");

    // Add tokens before and after
    console.executeCommand("tb #t2 very simple");
    // Check the result
    graph.sortTokenByText();
    assertEquals("This very simple text", graph.getTextualDSs().get(0).getText());
    List<SToken> tokens = graph.getTokens();
    assertEquals(4, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("very", graph.getText(tokens.get(1)));
    assertEquals("simple", graph.getText(tokens.get(2)));
    assertEquals("text", graph.getText(tokens.get(3)));

    console.executeCommand("ta #t1 is a");

    // Check the result
    graph.sortTokenByText();
    assertEquals("This is a very simple text", graph.getTextualDSs().get(0).getText());
    tokens = graph.getTokens();
    assertEquals(6, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("is", graph.getText(tokens.get(1)));
    assertEquals("a", graph.getText(tokens.get(2)));
    assertEquals("very", graph.getText(tokens.get(3)));
    assertEquals("simple", graph.getText(tokens.get(4)));
    assertEquals("text", graph.getText(tokens.get(5)));
  }


  /**
   * Tests if the "ta" and "tb" commands add t work correctly and with no side-effects when having
   * multiple text. This is a regression test for https://github.com/hexatomic/hexatomic/issues/140.
   */
  @Test
  void testTokenizeAfterBeforeMultipleTexts() {

    // Add initial tokens
    console.executeCommand("t This text is the first one.");
    assertEquals(1, graph.getTextualDSs().size());
    STextualDS firstText = graph.getTextualDSs().get(0);
    final String originalText = firstText.getText();
    
    // Add another textual data source manually
    STextualDS anotherText = graph.createTextualDS("Another text");
    final SToken tok1 = graph.createToken(anotherText, 0, 7);
    final SToken tok2 = graph.createToken(anotherText, 8, 12);

    // Append tokens after the first token
    console.executeCommand("ta #" + tok1.getName() + " very fancy");
    // Prepend token before the second token
    console.executeCommand("tb #" + tok2.getName() + " and reliable");

    // Check that the right textual data source has been amended
    assertEquals("Another very fancy and reliable text", anotherText.getText());

    // Check the original text has not been altered and is displayed correctly
    assertEquals(originalText, firstText.getText());
    graph.sortTokenByText();
    DataSourceSequence<Integer> seq = new DataSourceSequence<>();
    seq.setDataSource(firstText);
    seq.setStart(0);
    seq.setEnd(firstText.getText().length());
    List<SToken> firstTextTokens = graph.getTokensBySequence(seq);
    assertEquals(7, firstTextTokens.size());
    assertEquals("This", graph.getText(firstTextTokens.get(0)));
    assertEquals("text", graph.getText(firstTextTokens.get(1)));
    assertEquals("is", graph.getText(firstTextTokens.get(2)));
    assertEquals("the", graph.getText(firstTextTokens.get(3)));
    assertEquals("first", graph.getText(firstTextTokens.get(4)));
    assertEquals("one", graph.getText(firstTextTokens.get(5)));
    assertEquals(".", graph.getText(firstTextTokens.get(6)));
  }

  @Test
  void testExampleDelete() {
    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();

    // Add edges
    console.executeCommand("e #t4 -> #t3");
    console.executeCommand("e #t4 -> #t2");
    console.executeCommand("e #t1 -> #t3");
    console.executeCommand("e #t4 -> #t5");

    assertEquals(5, graph.getTokens().size());
    assertEquals(4, graph.getPointingRelations().size());

    // Delete first two tokens, this should delete some of the edges as well
    console.executeCommand("d #t1 #t2");

    assertEquals(3, graph.getTokens().size());
    assertEquals(2, graph.getPointingRelations().size());

    // Delete one of the remaining edges
    console.executeCommand("d #t4 -> #t3");

    assertEquals(3, graph.getTokens().size());
    assertEquals(1, graph.getPointingRelations().size());

  }

  @Test
  void testTokenizeError() {
    // This command should produce an error message containing of two lines: the position marker
    // and the actual message.
    List<String> output = console.executeCommand("t I'm a test");
    assertEquals(1, output.size());
    // Check the error position marker and message
    assertEquals("     ^\ntoken recognition error at: '''", output.get(0));
  }

  /**
   * Test if inserting the same edge with the annotation is possible. While a single edge can have
   * only one annotation with the same qualified name, the "e" command should always create a new
   * edge which can have the same annotations. This is a regression test for
   * https://github.com/hexatomic/hexatomic/issues/153
   * 
   */
  @Test
  void testInsertEdgeTwice() {
    // Add initial tokens 
    console.executeCommand("t This is an example \".\"");
    
    // Add first pointing relation
    console.executeCommand("e #t1 -> #t2 ns:n:a");
    assertEquals(1, graph.getPointingRelations().size());
    // Add second pointing relation with the same annotation
    console.executeCommand("e #t1 -> #t2 ns:n:a");
    assertEquals(2, graph.getPointingRelations().size());
    // Check that we can't create a pointing relation when one of the nodes does not exist
    console.executeCommand("e #t1 -> #n245 ns:n:a");
    assertEquals(2, graph.getPointingRelations().size());
    
    // Repeat the test sequence with dominance relations: create two syntax nodes first
    console.executeCommand("n cat:X #t1");
    assertEquals(1, graph.getDominanceRelations().size());
    console.executeCommand("e #n1 > #t2 ns:n:a");
    assertEquals(2, graph.getDominanceRelations().size());
    console.executeCommand("e #n1 > #t2 ns:n:a");
    assertEquals(3, graph.getDominanceRelations().size()); 
  }

}
