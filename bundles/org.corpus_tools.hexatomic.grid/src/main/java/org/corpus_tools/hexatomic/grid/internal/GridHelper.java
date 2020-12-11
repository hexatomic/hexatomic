/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.grid.internal;

import org.corpus_tools.hexatomic.grid.LayerSetupException;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * Helper class to access grid information.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class GridHelper {

  private GridHelper() {
    // Should not be initiated.
  }

  /**
   * Returns the column header layer (of type {@link GridColumnHeaderLayer}) for the given NatTable.
   * 
   * @param natTable the NatTable to get the column header layer for.
   * @return the {@link GridColumnHeaderLayer} of the NatTable.
   */
  public static GridColumnHeaderLayer getColumnHeaderLayer(NatTable natTable) {
    GridLayer gridLayer = getGridLayerForNatTable(natTable);
    ILayer columnHeaderLayer = gridLayer.getColumnHeaderLayer();
    if (!(columnHeaderLayer instanceof GridColumnHeaderLayer)) {
      throw new LayerSetupException("Column header layer", columnHeaderLayer,
          GridColumnHeaderLayer.class);
    } else {
      return (GridColumnHeaderLayer) columnHeaderLayer;
    }
  }

  /**
   * Returns the body layer (of type {@link GridFreezeLayer}) for the given NatTable.
   * 
   * @param natTable the NatTable to get the body layer for.
   * @return the {@link GridFreezeLayer} of the NatTable.
   */
  public static GridFreezeLayer getBodyLayer(NatTable natTable) {
    GridLayer gridLayer = getGridLayerForNatTable(natTable);
    ILayer bodyLayer = gridLayer.getBodyLayer();
    if (!(bodyLayer instanceof GridFreezeLayer)) {
      throw new LayerSetupException("Body layer", bodyLayer, GridFreezeLayer.class);
    } else {
      return (GridFreezeLayer) bodyLayer;
    }
  }

  private static GridLayer getGridLayerForNatTable(NatTable natTable) {
    ILayer underlyingLayer = natTable.getUnderlyingLayerByPosition(0, 0);
    if (!(underlyingLayer instanceof GridLayer)) {
      throw new LayerSetupException("Underlying layer of NatTable", underlyingLayer,
          GridLayer.class);
    } else {
      return (GridLayer) underlyingLayer;
    }
  }

}
