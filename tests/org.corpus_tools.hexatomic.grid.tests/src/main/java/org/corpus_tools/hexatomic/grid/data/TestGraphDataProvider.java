package org.corpus_tools.hexatomic.grid.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
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

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new GraphDataProvider();
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
  final void testGetDataValue() {
    fail("Not yet implemented"); // TODO
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

}
