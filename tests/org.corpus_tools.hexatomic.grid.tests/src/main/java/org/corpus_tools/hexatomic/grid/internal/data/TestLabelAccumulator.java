/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.internal.style.StyleConfiguration;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LabelAccumulator}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestLabelAccumulator {

  private static LabelAccumulator fixture = null;
  private static GraphDataProvider fixtureProvider = null;
  private static SpanningDataLayer fixtureLayer = null;

  /**
   * Sets up the fixture for unit tests.
   * 
   * @throws java.lang.Exception Any exception during setup
   */
  @BeforeEach
  void setUp() throws Exception {
    fixtureProvider = mock(GraphDataProvider.class);
    fixtureLayer = mock(SpanningDataLayer.class);
    SToken token = mock(SToken.class);
    SSpan span = mock(SSpan.class);
    SSpanningRelation spanningRelation = mock(SSpanningRelation.class);
    when(spanningRelation.getSource()).thenReturn(span);
    when(spanningRelation.getTarget()).thenReturn(token);
    when(fixtureProvider.getDataValue(0, 0)).thenReturn(token);
    @SuppressWarnings("unchecked")
    List<Column> columns = mock(List.class);
    Column tokenColumn = mock(Column.class);
    Column spanColumn = mock(Column.class);
    when(tokenColumn.getColumnType()).thenReturn(ColumnType.TOKEN_TEXT);
    when(columns.get(0)).thenReturn(tokenColumn);
    when(spanColumn.getColumnType()).thenReturn(ColumnType.SPAN_ANNOTATION);
    when(columns.get(1)).thenReturn(spanColumn);
    when(fixtureProvider.getColumns()).thenReturn(columns);
    when(fixtureLayer.getColumnIndexByPosition(0)).thenReturn(0);
    when(fixtureLayer.getColumnIndexByPosition(1)).thenReturn(1);
    fixture = new LabelAccumulator(fixtureLayer, fixtureProvider);
    ILayerCell tokenCell = mock(ILayerCell.class);
    ILayerCell emptyCell = mock(ILayerCell.class);
    ILayerCell spanCell = mock(ILayerCell.class);
    when(tokenCell.getDataValue()).thenReturn(token);
    when(emptyCell.getDataValue()).thenReturn(null);
    when(spanCell.getDataValue()).thenReturn(span);
    when(fixtureLayer.getCellByPosition(0, 0)).thenReturn(tokenCell);
    when(fixtureLayer.getCellByPosition(1, 0)).thenReturn(spanCell);
    when(fixtureLayer.getCellByPosition(0, 1)).thenReturn(emptyCell);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#LabelAccumulator(org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer, org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testLabelAccumulator() {
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, fixtureProvider));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(fixtureLayer, null));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, null));
    assertDoesNotThrow(() -> new LabelAccumulator(fixtureLayer, fixtureProvider));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#accumulateConfigLabels(org.eclipse.nebula.widgets.nattable.layer.LabelStack, int, int)}.
   */
  @Test
  void testAccumulateConfigLabelsLabelStackIntInt() {
    // Test exception with non-existent cell
    RuntimeException thrownException =
        assertThrows(RuntimeException.class, () -> fixture.accumulateConfigLabels(null, -1, -1));
    assertEquals("There is no cell at column position -1, row position -1.",
        thrownException.getMessage());

    // Test empty cell
    LabelStack emptyStack = new LabelStack("");
    LabelStack tokenStack = new LabelStack("");
    LabelStack spanStack = new LabelStack("");
    fixture.accumulateConfigLabels(emptyStack, 0, 1);
    assertTrue(emptyStack.getLabels().contains(StyleConfiguration.EMPTY_CELL_STYLE));
    // Also has token text cell style and empty string
    assertEquals(3, emptyStack.getLabels().size());
    fixture.accumulateConfigLabels(tokenStack, 0, 0);
    assertTrue(tokenStack.getLabels().contains(StyleConfiguration.TOKEN_TEXT_CELL_STYLE));
    // Also has empty string
    assertEquals(2, tokenStack.getLabels().size());
    fixture.accumulateConfigLabels(spanStack, 1, 0);
    assertTrue(spanStack.getLabels().contains(StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE));
    assertEquals(2, spanStack.getLabels().size());
  }

}
