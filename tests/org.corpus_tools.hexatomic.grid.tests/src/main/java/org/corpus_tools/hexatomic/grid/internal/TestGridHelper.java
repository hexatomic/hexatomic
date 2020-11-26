package org.corpus_tools.hexatomic.grid.internal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.junit.jupiter.api.Test;

/**
 * Static tests for {@link GridHelper}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestGridHelper {

  private static final String LOG_MESSAGE__WRONG_LAYER_TYPE =
      " is not of type GridColumnHeaderLayer as expected! Please report this as a bug.\nOffending layer: ";
  private final NatTable natTable = mock(NatTable.class);
  private final GridColumnHeaderLayer columnHeaderLayer = mock(GridColumnHeaderLayer.class);
  private final GridFreezeLayer freezeLayer = mock(GridFreezeLayer.class);

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.GridHelper#isTokenColumnAtPosition(org.eclipse.nebula.widgets.nattable.NatTable, int, boolean)}.
   */
  @Test
  void testIsTokenColumnAtPosition() {
    ILayerCell cell = mock(ILayerCell.class);
    when(cell.getDataValue()).thenReturn("Token");
    when(natTable.getCellByPosition(1, 0)).thenReturn(cell);
    assertTrue(GridHelper.isTokenColumnAtPosition(natTable, 1, true));
    assertThrows(NullPointerException.class,
        () -> GridHelper.isTokenColumnAtPosition(natTable, 1, false));
    assertTrue(GridHelper.isTokenColumnAtPosition(natTable, 0, false));
    assertThrows(NullPointerException.class,
        () -> GridHelper.isTokenColumnAtPosition(natTable, 0, true));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.GridHelper#getColumnHeaderLayer(org.eclipse.nebula.widgets.nattable.NatTable)}.
   */
  @Test
  void testGetColumnHeaderLayer() {
    final GridLayer gridLayer = mock(GridLayer.class);
    when(gridLayer.getColumnHeaderLayer()).thenReturn(columnHeaderLayer);
    when(natTable.getUnderlyingLayerByPosition(0, 0)).thenReturn(gridLayer);
    assertEquals(columnHeaderLayer, GridHelper.getColumnHeaderLayer(natTable));
    ColumnHeaderLayer offendingLayer = mock(ColumnHeaderLayer.class);
    when(gridLayer.getColumnHeaderLayer()).thenReturn(offendingLayer);
    HexatomicRuntimeException exception = assertThrows(HexatomicRuntimeException.class,
        () -> GridHelper.getColumnHeaderLayer(natTable));
    assertEquals("Column header layer" + LOG_MESSAGE__WRONG_LAYER_TYPE + offendingLayer.toString(),
        exception.getMessage());
  }

  /**
   * Tests reporting of a bad composition of the the layer stack.
   */
  @Test
  void testGridLayerForNatTable() {
    final GridLayer gridLayer = mock(GridLayer.class);
    when(gridLayer.getColumnHeaderLayer()).thenReturn(columnHeaderLayer);
    when(natTable.getUnderlyingLayerByPosition(0, 0)).thenReturn(gridLayer);
    assertDoesNotThrow(() -> GridHelper.getColumnHeaderLayer(natTable));
    ColumnHeaderLayer offendingLayer = mock(ColumnHeaderLayer.class);
    when(natTable.getUnderlyingLayerByPosition(0, 0)).thenReturn(offendingLayer);
    HexatomicRuntimeException exception = assertThrows(HexatomicRuntimeException.class,
        () -> GridHelper.getColumnHeaderLayer(natTable));
    assertEquals(
        "Underlying layer of NatTable" + LOG_MESSAGE__WRONG_LAYER_TYPE + offendingLayer.toString(),
        exception.getMessage());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.GridHelper#getBodyLayer(org.eclipse.nebula.widgets.nattable.NatTable)}.
   */
  @Test
  void testGetBodyLayer() {
    final GridLayer gridLayer = mock(GridLayer.class);
    when(natTable.getUnderlyingLayerByPosition(0, 0)).thenReturn(gridLayer);
    when(gridLayer.getBodyLayer()).thenReturn(freezeLayer);
    assertEquals(freezeLayer, GridHelper.getBodyLayer(natTable));
    FreezeLayer offendingLayer = mock(FreezeLayer.class);
    when(gridLayer.getBodyLayer()).thenReturn(offendingLayer);
    HexatomicRuntimeException exception =
        assertThrows(HexatomicRuntimeException.class, () -> GridHelper.getBodyLayer(natTable));
    assertEquals("Body layer" + LOG_MESSAGE__WRONG_LAYER_TYPE + offendingLayer.toString(),
        exception.getMessage());
  }

}
