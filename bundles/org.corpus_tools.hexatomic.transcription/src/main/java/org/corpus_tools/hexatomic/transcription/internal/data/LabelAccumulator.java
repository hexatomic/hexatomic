/*-
 * #%L
 * [bundle] Transcription Editor
 * %%
 * Copyright (C) 2018 - 2024 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.transcription.internal.data;

import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

public class LabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final ILayer layer;

  public LabelAccumulator(ILayer layer) {
    super(layer);
    this.layer = layer;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    // Inherit defaults
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

    Object value = layer.getDataValueByPosition(columnPosition, rowPosition);
    if (value == null) {
      configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
    } else {
      configLabels.addLabel(StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
    }
  }

}
