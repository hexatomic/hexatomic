package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.graph.LabelableElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Column}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class TestColumn {

  private static Column fixtureTokenTextColumn = null;
  private static Column fixtureAnnotationColumn = null;
  private SToken token1 = null;
  private SToken token2 = null;
  private SAnnotation annotation1 = null;
  private SAnnotation annotation2 = null;

  /**
   * Sets up the fixture for unit tests.
   * 
   * @throws java.lang.Exception Any exception during setup
   */
  @BeforeEach
  void setUp() throws Exception {
    SDocumentGraph graph = mock(SDocumentGraph.class);

    token1 = mock(SToken.class);
    when(token1.getGraph()).thenReturn(graph);
    when(graph.getText(token1)).thenReturn("Unit");
    token2 = mock(SToken.class);
    when(token2.getGraph()).thenReturn(graph);
    when(graph.getText(token2)).thenReturn("Test");

    fixtureTokenTextColumn = new Column(ColumnType.TOKEN_TEXT, "Header");
    fixtureTokenTextColumn.setRow(0, token1);
    fixtureTokenTextColumn.setRow(1, token2);

    annotation1 = mock(SAnnotation.class);
    when(annotation1.getQName()).thenReturn("test::anno");
    when(annotation1.getValue()).thenReturn("anno1");
    annotation2 = mock(SAnnotation.class);
    when(annotation2.getQName()).thenReturn("test::anno");
    when(annotation2.getValue()).thenReturn("anno2");

    when(token1.getAnnotation(annotation1.getQName())).thenReturn(annotation1);
    when(token2.getAnnotation(annotation2.getQName())).thenReturn(annotation2);

    fixtureAnnotationColumn = new Column(ColumnType.TOKEN_ANNOTATION, annotation1.getQName());
    fixtureAnnotationColumn.setRow(0, token1);
    fixtureAnnotationColumn.setRow(1, token2);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.Column#setRow(int, LabelableElement)}.
   */
  @Test
  final void testSetRow() {
    fixtureTokenTextColumn.setRow(100, token1);
    assertEquals(token1, fixtureTokenTextColumn.getCells().get(100));
    assertTrue(fixtureTokenTextColumn.getBits().get(100));
    assertEquals(3, fixtureTokenTextColumn.getBits().cardinality());
    assertThrows(RuntimeException.class,
        () -> fixtureTokenTextColumn.setRow(0, mock(SToken.class)));
  }



  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.Column#getDataObject(int)}.
   */
  @Test
  final void testGetDataObject() {
    assertEquals(token1, fixtureTokenTextColumn.getDataObject(0));
    assertEquals(token2, fixtureTokenTextColumn.getDataObject(1));
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.internal.data.Column#isRowEmpty(int)}.
   */
  @Test
  final void testIsRowEmpty() {
    assertTrue(fixtureTokenTextColumn.isRowEmpty(2));
    assertFalse(fixtureTokenTextColumn.isRowEmpty(1));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.Column#areRowsEmpty(int, int)}.
   */
  @Test
  final void testAreRowsEmpty() {
    assertTrue(fixtureTokenTextColumn.areRowsEmpty(2, 100));
    assertFalse(fixtureTokenTextColumn.areRowsEmpty(0, 1));
    assertFalse(fixtureTokenTextColumn.areRowsEmpty(1, 2));
    assertFalse(fixtureTokenTextColumn.areRowsEmpty(1, 100));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.Column#getDisplayText(int)}.
   */
  @Test
  final void testGetDisplayText() {
    assertEquals("Unit", fixtureTokenTextColumn.getDisplayText(0));
    assertEquals("Test", fixtureTokenTextColumn.getDisplayText(1));
    assertEquals("anno1", fixtureAnnotationColumn.getDisplayText(0));
    assertEquals("anno2", fixtureAnnotationColumn.getDisplayText(1));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.Column#getDisplayText(int)} which triggers
   * a RuntimeException by adding the wrong type of data object to the token text type column.
   */
  @Test
  final void testGetDisplayTextThrowsException() {
    assertEquals("Unit", fixtureTokenTextColumn.getDisplayText(0));
    assertEquals("Test", fixtureTokenTextColumn.getDisplayText(1));
    SStructure structure = mock(SStructure.class);
    fixtureTokenTextColumn.setRow(2, structure);
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> fixtureTokenTextColumn.getDisplayText(2));
    assertEquals(
        "A column of type TOKEN_TEXT can only contain data objects of type "
            + "org.corpus_tools.salt.common.SToken. Encountered: class "
            + "org.corpus_tools.salt.common.SStructure$MockitoMock",
        e.getMessage().split("\\$[\\d]+")[0]);
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.internal.data.Column#getHeader()}.
   */
  @Test
  final void testGetHeader() {
    assertEquals("Header", fixtureTokenTextColumn.getHeader());
    assertEquals("test::anno", fixtureAnnotationColumn.getHeader());

    // Test indexed header
    Column indexedColumn = new Column(ColumnType.SPAN_ANNOTATION, "test", 2);
    assertEquals("test (2)", indexedColumn.getHeader());
  }

}
