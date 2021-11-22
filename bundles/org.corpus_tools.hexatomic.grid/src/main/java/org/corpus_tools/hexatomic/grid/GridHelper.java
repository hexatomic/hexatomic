package org.corpus_tools.hexatomic.grid;

import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * A type providing static helper methods.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridHelper {

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

}
