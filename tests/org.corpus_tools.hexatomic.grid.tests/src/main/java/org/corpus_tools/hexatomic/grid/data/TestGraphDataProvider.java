package org.corpus_tools.hexatomic.grid.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
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

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new GraphDataProvider();
    exampleGraph = buildSimpleGraph();
    exampleText = getFirstTextFromGraph(exampleGraph);
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
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#setDataValue(int, int, java.lang.Object)}.
   */
  @Test
  final void testSetDataValue() {
    fail("Not yet implemented"); // TODO
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getColumnCount()}.
   */
  @Test
  final void testGetColumnCount() {
    fail("Not yet implemented"); // TODO
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getRowCount()}.
   */
  @Test
  final void testGetRowCount() {
    fail("Not yet implemented"); // TODO
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#setDsAndResolveGraph(org.corpus_tools.salt.common.STextualDS)}.
   */
  @Test
  final void testSetDsAndResolveGraph() {
    fail("Not yet implemented"); // TODO
  }

  private SDocumentGraph buildSimpleGraph() {
    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/grid/example-corpus/");
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
