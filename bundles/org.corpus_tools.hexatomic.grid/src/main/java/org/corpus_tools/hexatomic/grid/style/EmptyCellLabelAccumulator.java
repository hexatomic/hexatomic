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
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;

/**
 * A cell label accumulator which assigns empty cells a custom configuration label.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class EmptyCellLabelAccumulator implements IConfigLabelAccumulator {

  private final DefaultBodyLayerStack bodyLayer;

  /**
   * Constructor setting the {@link #bodyLayer} field.
   * 
   * <p>
   * A {@link DefaultBodyLayerStack} is needed to determine properties of cells that should be
   * assigned custom configuration labels.
   * </p>
   * 
   * @param bodyLayer The bodyLayer to set to the {@link #bodyLayer} field
   */
  public EmptyCellLabelAccumulator(DefaultBodyLayerStack bodyLayer) {
    this.bodyLayer = bodyLayer;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    ILayerCell cell = bodyLayer.getCellByPosition(columnPosition, rowPosition);
    if (cell.getDataValue() == null) {
      configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
    }
  }

}
