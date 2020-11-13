package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.grid.internal.test.TestHelper;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GridDisplayConverter}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestGridDisplayConverter {

  private GridDisplayConverter fixture = null;
  private SToken token = null;
  private SSpan span = null;
  private GraphDataProvider dataProvider = null;
  private ILayerCell tokenCell = mock(ILayerCell.class);
  private ILayerCell spanCell = mock(ILayerCell.class);
  private ILayerCell nullCell = null;
  private ILayerCell tokenTextCell = mock(ILayerCell.class);

  /**
   * Sets up the fixture for unit tests.
   */
  @BeforeEach
  void setUp() {
    dataProvider = TestHelper.createDataProvider();
    fixture = new GridDisplayConverter(dataProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#AnnotationDisplayConverter(org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator)}.
   */
  @Test
  void testGridDisplayConverter() {
    assertThrows(IllegalArgumentException.class, () -> new GridDisplayConverter(null));
    assertDoesNotThrow(() -> new GridDisplayConverter(dataProvider));
    when(tokenCell.getRowIndex()).thenReturn(0);
    when(tokenCell.getColumnIndex()).thenReturn(1);
    when(tokenTextCell.getColumnIndex()).thenReturn(0);
    when(tokenTextCell.getRowIndex()).thenReturn(0);
    when(spanCell.getColumnIndex()).thenReturn(3);
    when(spanCell.getRowIndex()).thenReturn(0);
    token = (SToken) dataProvider.getDataValue(0, 0);
    span = (SSpan) dataProvider.getDataValue(3, 0);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#canonicalToDisplayValue(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell, org.eclipse.nebula.widgets.nattable.config.IConfigRegistry, java.lang.Object)}.
   */
  @Test
  void testCanonicalToDisplayValueILayerCell() {
    when(tokenCell.getRowIndex()).thenReturn(0);
    when(tokenCell.getColumnIndex()).thenReturn(1);
    when(tokenTextCell.getColumnIndex()).thenReturn(0);
    when(tokenTextCell.getRowIndex()).thenReturn(0);
    when(spanCell.getColumnIndex()).thenReturn(3);
    when(spanCell.getRowIndex()).thenReturn(0);
    token = (SToken) dataProvider.getDataValue(0, 0);
    span = (SSpan) dataProvider.getDataValue(3, 0);
    assertEquals("be", fixture.canonicalToDisplayValue(tokenCell, null, token));
    assertEquals("contrast-focus", fixture.canonicalToDisplayValue(spanCell, null, span));
    assertNull(fixture.canonicalToDisplayValue(null, null, null));
    assertNull(fixture.canonicalToDisplayValue(tokenCell, null, mock(STextualRelation.class)));
    assertNull(fixture.canonicalToDisplayValue(nullCell, null, token));
    assertThrows(RuntimeException.class,
        () -> fixture.canonicalToDisplayValue(tokenCell, null, mock(SStructure.class)));
    assertEquals("Is", fixture.canonicalToDisplayValue(tokenTextCell, null, token));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.AnnotationDisplayConverter#displayToCanonicalValue(org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell, org.eclipse.nebula.widgets.nattable.config.IConfigRegistry, java.lang.Object)}.
   */
  @Test
  void testDisplayToCanonicalValueILayerCell() {
    // assertEquals(token, fixture.displayToCanonicalValue(tokenCell, null, "token_pass"));
    // assertEquals(span, fixture.displayToCanonicalValue(spanCell, null, "span_pass"));
    assertEquals("be", fixture.displayToCanonicalValue(tokenCell, null, "be"));
    assertEquals("contrast-focus",
        fixture.displayToCanonicalValue(spanCell, null, "contrast-focus"));
    assertEquals("Is", fixture.displayToCanonicalValue(tokenTextCell, null, "Is"));
  }

}
