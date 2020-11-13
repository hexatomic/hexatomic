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
    when(gridLayer.getColumnHeaderLayer()).thenReturn(mock(ColumnHeaderLayer.class));
    HexatomicRuntimeException exception = assertThrows(HexatomicRuntimeException.class,
        () -> GridHelper.getColumnHeaderLayer(natTable));
    assertEquals(
        "Column header layer is not of type GridColumnHeaderLayer as expected! Please report this as a bug.",
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
    when(natTable.getUnderlyingLayerByPosition(0, 0)).thenReturn(mock(ColumnHeaderLayer.class));
    HexatomicRuntimeException exception = assertThrows(HexatomicRuntimeException.class,
        () -> GridHelper.getColumnHeaderLayer(natTable));
    assertEquals(
        "Underlying layer of NatTable is not of type GridLayer as expected! Please report this as a bug.",
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
    when(gridLayer.getBodyLayer()).thenReturn(mock(FreezeLayer.class));
    HexatomicRuntimeException exception =
        assertThrows(HexatomicRuntimeException.class, () -> GridHelper.getBodyLayer(natTable));
    assertEquals(
        "Body layer is not of type GridFreezeLayer as expected! Please report this as a bug.",
        exception.getMessage());
  }

}
