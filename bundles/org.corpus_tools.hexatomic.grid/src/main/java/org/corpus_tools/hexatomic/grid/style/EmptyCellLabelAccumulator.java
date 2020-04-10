/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

package org.corpus_tools.hexatomic.grid.style;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * A cell label accumulator which assigns empty cells a custom configuration label.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class EmptyCellLabelAccumulator implements IConfigLabelAccumulator {

  private final SpanningDataLayer bodyDataLayer;

  /**
   * Constructor setting the {@link #bodyDataLayer} field.
   * 
   * <p>
   * The lowermost layer in the layer stack is needed to determine properties of cells that should
   * be assigned custom configuration labels.
   * </p>
   * 
   * @param bodyDataLayer The {@link SpanningDataLayer} to set to the {@link #bodyDataLayer} field
   */
  public EmptyCellLabelAccumulator(SpanningDataLayer bodyDataLayer) {
    this.bodyDataLayer = bodyDataLayer;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    ILayerCell bodyCell = bodyDataLayer.getCellByPosition(columnPosition, rowPosition);

    // Assign the empty cell style iff the cell in the data body layer is null
    if (bodyCell.getDataValue() == null) {
      configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
    }
  }

}
