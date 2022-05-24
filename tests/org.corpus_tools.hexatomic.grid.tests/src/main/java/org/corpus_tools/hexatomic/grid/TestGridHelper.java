package org.corpus_tools.hexatomic.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GridHelper}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestGridHelper {

  /**
   * Tests that the expected exceptions are thrown when the {@link NatTable} is set up wrongly.
   */
  @Test
  void testGetColumnHeaderLayerThrowsHexatomicRuntimeException() {
    NatTable table = mock(NatTable.class);
    SelectionLayer pseudoGridLayer = mock(SelectionLayer.class);
    when(table.getUnderlyingLayerByPosition(0, 0)).thenReturn(pseudoGridLayer);
    assertThrows(HexatomicRuntimeException.class, () -> GridHelper.getColumnHeaderLayer(table));
  }

  /**
   * Tests that the expected exceptions are thrown when the {@link NatTable} is set up wrongly.
   */
  @Test
  void testGetColumnHeaderLayerThrowsLayerSetupException() {
    NatTable table = mock(NatTable.class);
    GridLayer pseudoGridLayer = mock(GridLayer.class);
    ColumnHeaderLayer pseudoColumnHeaderLayer = mock(ColumnHeaderLayer.class);
    when(pseudoGridLayer.getColumnHeaderLayer()).thenReturn(pseudoColumnHeaderLayer);
    when(table.getUnderlyingLayerByPosition(0, 0)).thenReturn(pseudoGridLayer);
    assertThrows(HexatomicRuntimeException.class, () -> GridHelper.getColumnHeaderLayer(table));
  }

  /**
   * Tests that the {@link GridHelper#getColumnHeaderLayer(NatTable)} returns the correct value.
   */
  @Test
  void testGetColumnHeaderLayer() {
    NatTable table = mock(NatTable.class);
    GridLayer pseudoGridLayer = mock(GridLayer.class);
    GridColumnHeaderLayer pseudoColumnHeaderLayer = mock(GridColumnHeaderLayer.class);
    when(pseudoGridLayer.getColumnHeaderLayer()).thenReturn(pseudoColumnHeaderLayer);
    when(table.getUnderlyingLayerByPosition(0, 0)).thenReturn(pseudoGridLayer);
    assertEquals(pseudoColumnHeaderLayer, GridHelper.getColumnHeaderLayer(table));
  }

}
