package org.corpus_tools.hexatomic.grid.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GraphDataProvider}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class TestGraphDataProvider {

  private static GraphDataProvider fixture = null;
  private SDocumentGraph exampleGraph;
  private STextualDS exampleText;
  private SDocumentGraph overlappingExampleGraph;
  private STextualDS overlappingExampleText;

  private static final String examplePath =
      "../org.corpus_tools.hexatomic.core.tests/src/main/resources/"
          + "org/corpus_tools/hexatomic/core/example-corpus/";
  private static final String overlappingExamplePath =
      "src/main/resources/org/corpus_tools/hexatomic/grid/overlapping-spans/";

  /**
   * Set up the fixture and models for each test.
   * 
   * @throws java.lang.Exception Any exception that is thrown during the setup phase.
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new GraphDataProvider();
    exampleGraph = retrieveGraph(examplePath);
    overlappingExampleGraph = retrieveGraph(overlappingExamplePath);
    exampleText = getFirstTextFromGraph(exampleGraph);
    overlappingExampleText = getFirstTextFromGraph(overlappingExampleGraph);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueEmptyGraph() {
    assertEquals("Please select data source!", fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(1, 1));
    assertEquals(null, fixture.getDataValue(1, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueSetDsWithoutTokens() {
    STextualDS ds = mock(STextualDS.class);
    SDocumentGraph graph = mock(SDocumentGraph.class);
    fixture.setGraph(graph);
    fixture.setDsAndResolveGraph(ds);
    assertEquals("Data source contains no tokens!", fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
    // Cannot test other cells, as ErrorService cannot be be got via BundleContext, as this is not
    // available in fragments. Instead, this is a circumvent way of testing whether the
    // IndexOutOfBoundsException is thrown in the host, as this will throw an NPE here (as
    // ErrorService is null).
    assertThrows(NullPointerException.class, () -> fixture.getDataValue(1, 1));
    assertThrows(NullPointerException.class, () -> fixture.getDataValue(1, 0));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueDefaultExample() {
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);

    assertEquals("Is", fixture.getDataValue(0, 0));
    assertEquals("this", fixture.getDataValue(0, 1));
    assertEquals("example", fixture.getDataValue(0, 2));
    assertEquals("more", fixture.getDataValue(0, 3));
    assertEquals("complicated", fixture.getDataValue(0, 4));
    assertEquals("than", fixture.getDataValue(0, 5));
    assertEquals("it", fixture.getDataValue(0, 6));
    assertEquals("appears", fixture.getDataValue(0, 7));
    assertEquals("to", fixture.getDataValue(0, 8));
    assertEquals("be", fixture.getDataValue(0, 9));
    assertEquals("?", fixture.getDataValue(0, 10));

    assertEquals("be", fixture.getDataValue(1, 0));
    assertEquals("this", fixture.getDataValue(1, 1));
    assertEquals("example", fixture.getDataValue(1, 2));
    assertEquals("more", fixture.getDataValue(1, 3));
    assertEquals("complicated", fixture.getDataValue(1, 4));
    assertEquals("than", fixture.getDataValue(1, 5));
    assertEquals("it", fixture.getDataValue(1, 6));
    assertEquals("appear", fixture.getDataValue(1, 7));
    assertEquals("to", fixture.getDataValue(1, 8));
    assertEquals("be", fixture.getDataValue(1, 9));
    assertEquals("?", fixture.getDataValue(1, 10));

    assertEquals("VBZ", fixture.getDataValue(2, 0));
    assertEquals("DT", fixture.getDataValue(2, 1));
    assertEquals("NN", fixture.getDataValue(2, 2));
    assertEquals("RBR", fixture.getDataValue(2, 3));
    assertEquals("JJ", fixture.getDataValue(2, 4));
    assertEquals("IN", fixture.getDataValue(2, 5));
    assertEquals("PRP", fixture.getDataValue(2, 6));
    assertEquals("VBZ", fixture.getDataValue(2, 7));
    assertEquals("TO", fixture.getDataValue(2, 8));
    assertEquals("VB", fixture.getDataValue(2, 9));
    assertEquals(".", fixture.getDataValue(2, 10));

    assertEquals("contrast-focus", fixture.getDataValue(3, 0));
    assertEquals("topic", fixture.getDataValue(3, 4));
    assertEquals("topic", fixture.getDataValue(3, 5));
    assertEquals("topic", fixture.getDataValue(3, 6));
    assertEquals("topic", fixture.getDataValue(3, 7));
    assertEquals("topic", fixture.getDataValue(3, 8));
    assertEquals("topic", fixture.getDataValue(3, 9));
    assertEquals("topic", fixture.getDataValue(3, 10));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueOverlappingExample() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertEquals("Overlapping", fixture.getDataValue(0, 0));
    assertEquals("spans", fixture.getDataValue(0, 1));
    assertEquals(",", fixture.getDataValue(0, 2));
    assertEquals("etc", fixture.getDataValue(0, 3));
    assertEquals(".", fixture.getDataValue(0, 4));

    assertEquals("six_tok_anno_0", fixture.getDataValue(1, 0));
    assertEquals("six_tok_anno_1", fixture.getDataValue(1, 1));
    assertEquals("six_tok_anno_2", fixture.getDataValue(1, 2));
    assertEquals("six_tok_anno_3", fixture.getDataValue(1, 3));
    assertEquals("six_tok_anno_4", fixture.getDataValue(1, 4));

    assertEquals("val_span_1", fixture.getDataValue(2, 0));
    assertEquals("val_span_2", fixture.getDataValue(2, 1));
    assertEquals("val_span_2", fixture.getDataValue(2, 2));
    assertEquals("val_span_2", fixture.getDataValue(2, 3));
    assertEquals("val_span_2", fixture.getDataValue(2, 4));

    assertEquals("val_span_3", fixture.getDataValue(3, 0));
    assertEquals("val_span_3", fixture.getDataValue(3, 1));
    assertEquals("val_span_4", fixture.getDataValue(3, 2));
    assertEquals("val_span_5", fixture.getDataValue(3, 3));
    assertEquals("val_span_5", fixture.getDataValue(3, 4));

    assertEquals("val_span_new1", fixture.getDataValue(4, 1));
    assertEquals("val_span_new1", fixture.getDataValue(4, 2));
    assertEquals("val_span_new2", fixture.getDataValue(4, 3));
    assertEquals("val_span_new2", fixture.getDataValue(4, 4));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getColumnCount()}.
   */
  @Test
  final void testGetColumnCount() {
    assertEquals(1, fixture.getColumnCount());

    fixture.setGraph(exampleGraph);
    assertEquals(1, fixture.getColumnCount());

    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(4, fixture.getColumnCount());

    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getColumnCount());
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getRowCount()}.
   */
  @Test
  final void testGetRowCount() {
    assertEquals(1, fixture.getRowCount());

    fixture.setGraph(exampleGraph);
    assertEquals(1, fixture.getRowCount());

    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(11, fixture.getRowCount());

    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getRowCount());
  }

  private SDocumentGraph retrieveGraph(String path) {
    File exampleProjectDirectory = new File(path);
    assertTrue(exampleProjectDirectory.isDirectory());
    URI exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
    SaltProject project = SaltFactory.createSaltProject();
    project.loadSaltProject(exampleProjectUri);
    SDocumentGraph graph =
        project.getCorpusGraphs().get(0).getDocuments().get(0).getDocumentGraph();
    assertNotNull(graph);
    return graph;
  }

  private STextualDS getFirstTextFromGraph(SDocumentGraph graph) {
    STextualDS text = graph.getTextualDSs().get(0);
    assertNotNull(text);
    return text;
  }

}
