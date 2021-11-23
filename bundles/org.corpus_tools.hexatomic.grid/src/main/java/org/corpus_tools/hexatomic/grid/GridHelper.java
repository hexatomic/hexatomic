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

}
