/*-
 * #%L
 * [bundle] Hexatomic Grid Editor
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.grid;

import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * A type providing static helper methods.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridHelper {

  private GridHelper() {
    // Private constructor to avoid instantiations
  }

  /**
   * Retrieves the body data layer, i.e., the {@link GridFreezeLayer} for a given {@link NatTable}.
   * 
   * @param natTable the {@link NatTable} hosting the {@link GridFreezeLayer} to retrieve
   * @return the given {@link NatTable}'s body data {@link GridFreezeLayer}
   */
  public static GridFreezeLayer getBodyLayer(NatTable natTable) {
    // Due to how the NatTable is set up, this returns a GridLayer
    ILayer layer = natTable.getUnderlyingLayerByPosition(0, 0);
    while (layer != null) {
      if (layer instanceof GridFreezeLayer) {
        return (GridFreezeLayer) layer;
      }
      // Getting the underlying layer by position 1, 1 is necessary to hit a position in the grid
      // body, i.e., excluding the header rows and columns.
      layer = layer.getUnderlyingLayerByPosition(1, 1);
    }
    throw new LayerSetupException("Bottom layer of NatTable", layer, GridFreezeLayer.class);
  }

  /**
   * Retrieves the column header layer, i.e., the {@link GridColumnHeaderLayer} for a given
   * {@link NatTable}
   * 
   * @param natTable The {@link NatTable} hosting the {@link GridColumnHeaderLayer} to retrieve
   * @return the given {@link NatTable}'s column header layer
   */
  public static GridColumnHeaderLayer getColumnHeaderLayer(NatTable natTable) {
    ILayer layer = natTable.getUnderlyingLayerByPosition(0, 0);
    if (layer instanceof GridLayer) {
      ILayer columnHeaderLayer = ((GridLayer) layer).getColumnHeaderLayer();
      if (columnHeaderLayer instanceof GridColumnHeaderLayer) {
        return (GridColumnHeaderLayer) columnHeaderLayer;
      } else {
        throw new LayerSetupException("Column header layer of NatTable", columnHeaderLayer,
            GridColumnHeaderLayer.class);
      }
    }
    throw new HexatomicRuntimeException(
        "Could not find a layer of type " + GridColumnHeaderLayer.class.getSimpleName()
            + " in the NatTable. Please report this as a bug.");
  }

  /**
   * Tests whether all passed {@link PositionCoordinate}s are in the same column, and that the
   * column is a span annotation column.
   * 
   * @param selectedCellCoordinates the {@link PositionCoordinate}s of the currently selected cells
   * @param selectionLayer The {@link NatTable}s {@link SelectionLayer}
   * @return whether all selected cells are in a single span annotation column
   */
  public static boolean areSelectedCellsInSingleSpanColumn(
      PositionCoordinate[] selectedCellCoordinates, SelectionLayer selectionLayer) {
    int singleColumnPosition = -1;
    for (PositionCoordinate coord : selectedCellCoordinates) {
      int columnPosition = coord.getColumnPosition();
      // Check for each coordinate pair whether it has the same column position as the first
      // pair (otherwise the cell is in a different column).
      if (singleColumnPosition == -1) {
        singleColumnPosition = columnPosition;
      } else if (columnPosition != singleColumnPosition) {
        return false;
      }
    }
    // At this point, singleColumnPosition should be set
    // Return whether the single column is a span column
    return isSpanColumn(singleColumnPosition, selectionLayer);
  }

  /**
   * Tests whether all cells for the passed position coordinates are empty.
   * 
   * @param selectedCellCoordinates An array of {@link PositionCoordinate}s for the currently
   *        selected cells.
   * @param selectionLayer The selection layer
   * @return if all cells for the passed position coordinates are empty
   */
  public static boolean areSelectedCellsEmpty(PositionCoordinate[] selectedCellCoordinates,
      SelectionLayer selectionLayer) {
    for (PositionCoordinate coord : selectedCellCoordinates) {
      if (!isCellEmpty(selectionLayer, coord.columnPosition, coord.rowPosition)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests whether all selected cells are in span columns.
   * 
   * @param selectedCellCoordinates The {@link PositionCoordinate}s for the selected cells
   * @param selectionLayer The {@link SelectionLayer}
   * @return whether all selected cells are in a span column
   */
  public static boolean areAllSelectedCellsInSpanColumns(
      PositionCoordinate[] selectedCellCoordinates, SelectionLayer selectionLayer) {
    for (PositionCoordinate coord : selectedCellCoordinates) {
      if (!isSpanColumn(coord.columnPosition, selectionLayer)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isCellEmpty(SelectionLayer selectionLayer, int columnPosition,
      int rowPosition) {
    return selectionLayer.getDataValueByPosition(columnPosition, rowPosition) == null;
  }
  
  private static boolean isSpanColumn(int singleColumnPosition, SelectionLayer selectionLayer) {
    LabelStack configLabels = selectionLayer.getConfigLabelsByPosition(singleColumnPosition, 0);
    return configLabels.getLabels().contains(StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE);
  }

}
