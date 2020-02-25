package org.corpus_tools.hexatomic.grid.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Column}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class TestColumn {

  private static Column fixture = null;
  private SToken token = null;
  private SAnnotation annotation = null;

  /**
   * Sets up the fixture for unit tests.
   * 
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    SDocumentGraph graph = mock(SDocumentGraph.class);

    token = mock(SToken.class);
    when(token.getGraph()).thenReturn(graph);
    when(graph.getText(token)).thenReturn("Test");

    annotation = mock(SAnnotation.class);
    when(annotation.getValue_STEXT()).thenReturn("testAnno");

    fixture = new Column();
    fixture.setHeader("Header");
    fixture.setRow(0, token);
    fixture.setRow(1, annotation);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.Column#setRow(int, org.corpus_tools.salt.graph.LabelableElement)}.
   */
  @Test
  final void testSetRow() {
    fixture.setRow(100, token);
    assertEquals(token, fixture.getCells().get(100));
    assertTrue(fixture.getBits().get(100));
    assertEquals(3, fixture.getBits().cardinality());
    assertThrows(RuntimeException.class, () -> fixture.setRow(0, mock(SToken.class)));
  }



  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.Column#getDataObject(int)}.
   */
  @Test
  final void testGetDataObject() {
    assertEquals(token, fixture.getDataObject(0));
    assertEquals(annotation, fixture.getDataObject(1));
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.Column#isRowEmpty(int)}.
   */
  @Test
  final void testIsRowEmpty() {
    assertTrue(fixture.isRowEmpty(2));
    assertFalse(fixture.isRowEmpty(1));
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.Column#areRowsEmpty(int, int)}.
   */
  @Test
  final void testAreRowsEmpty() {
    assertTrue(fixture.areRowsEmpty(2, 100));
    assertFalse(fixture.areRowsEmpty(0, 1));
    assertFalse(fixture.areRowsEmpty(1, 2));
    assertFalse(fixture.areRowsEmpty(1, 100));
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.Column#getDisplayText(int)}.
   */
  @Test
  final void testGetDisplayText() {
    assertEquals("Test", fixture.getDisplayText(0));
    assertEquals("testAnno", fixture.getDisplayText(1));
    fixture.setRow(2, mock(SStructure.class));
    assertNull(fixture.getDisplayText(2));
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.Column#getDisplayText(int)}.
   */
  @Test
  final void testGetHeader() {
    assertEquals("Header", fixture.getHeader());
  }

}
