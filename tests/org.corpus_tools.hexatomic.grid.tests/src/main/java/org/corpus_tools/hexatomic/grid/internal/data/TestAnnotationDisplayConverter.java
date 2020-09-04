package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link AnnotationDisplayConverter}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestAnnotationDisplayConverter {

  private static AnnotationDisplayConverter fixture = null;
  private SToken token = null;
  private SSpan span = null;
  private GraphDataProvider dataProvider = null;
  private ILayerCell tokenCell = null;
  private ILayerCell spanCell = null;
  private ILayerCell nullCell;

  /**
   * Sets up the fixture for unit tests.
   * 
   * @throws java.lang.Exception Any exception during setup
   */
  @BeforeEach
  void setUp() throws Exception {
    dataProvider = mock(GraphDataProvider.class);
    Column tokenColumn = mock(Column.class);
    @SuppressWarnings("unchecked")
    List<Column> columns = mock(ArrayList.class);
    Column spanColumn = mock(Column.class);
    Column nullColumn = mock(Column.class);
    when(columns.get(0)).thenReturn(tokenColumn);
    when(columns.get(1)).thenReturn(spanColumn);
    when(columns.get(2)).thenReturn(nullColumn);
    when(tokenColumn.getColumnValue()).thenReturn("annotation:token");
    when(spanColumn.getColumnValue()).thenReturn("annotation:span");
    when(nullColumn.getColumnValue()).thenReturn("annotation:does_not_exist");
    when(dataProvider.getColumns()).thenReturn(columns);

    token = mock(SToken.class);
    SAnnotation tokenAnnotation = mock(SAnnotation.class);
    when(tokenAnnotation.getValue()).thenReturn("token_pass");
    when(token.getAnnotation("annotation:token")).thenReturn(tokenAnnotation);
    tokenCell = Mockito.mock(ILayerCell.class);
    when(tokenCell.getColumnIndex()).thenReturn(0);
    when(tokenCell.getRowIndex()).thenReturn(0);

    span = mock(SSpan.class);
    SAnnotation spanAnnotation = mock(SAnnotation.class);
    when(spanAnnotation.getValue()).thenReturn("span_pass");
    when(span.getAnnotation("annotation:span")).thenReturn(spanAnnotation);
    spanCell = Mockito.mock(ILayerCell.class);
    when(spanCell.getColumnIndex()).thenReturn(1);
    when(spanCell.getRowIndex()).thenReturn(0);
    when(dataProvider.getDataValue(0, 0)).thenReturn(token);
    when(dataProvider.getDataValue(1, 0)).thenReturn(span);

    nullCell = mock(ILayerCell.class);
    when(nullCell.getColumnIndex()).thenReturn(2);
    when(nullCell.getRowIndex()).thenReturn(0);

    fixture = new AnnotationDisplayConverter(dataProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#AnnotationDisplayConverter(org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator)}.
   */
  @Test
  void testAnnotationDisplayConverter() {
    assertThrows(IllegalArgumentException.class, () -> new AnnotationDisplayConverter(null));
    new AnnotationDisplayConverter(dataProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#canonicalToDisplayValue(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell, org.eclipse.nebula.widgets.nattable.config.IConfigRegistry, java.lang.Object)}.
   */
  @Test
  void testCanonicalToDisplayValueILayerCell() {
    assertEquals("token_pass", fixture.canonicalToDisplayValue(tokenCell, null, token));
    assertEquals("span_pass", fixture.canonicalToDisplayValue(spanCell, null, span));
    assertNull(fixture.canonicalToDisplayValue(null, null, null));
    assertNull(fixture.canonicalToDisplayValue(nullCell, null, token));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#displayToCanonicalValue(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell, org.eclipse.nebula.widgets.nattable.config.IConfigRegistry, java.lang.Object)}.
   */
  @Test
  void testDisplayToCanonicalValueILayerCell() {
    assertEquals(token, fixture.displayToCanonicalValue(tokenCell, null, "token_pass"));
    assertEquals(span, fixture.displayToCanonicalValue(spanCell, null, "span_pass"));
  }

}
