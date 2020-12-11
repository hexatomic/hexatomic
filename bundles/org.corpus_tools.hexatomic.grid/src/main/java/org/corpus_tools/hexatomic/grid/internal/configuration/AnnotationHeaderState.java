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

package org.corpus_tools.hexatomic.grid.internal.configuration;

import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;

/**
 * A menu item state which is active on columns which are not token columns.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
class AnnotationHeaderState implements IMenuItemState {

  /**
   * Return whether the column is a token column based on a check if the first cell sans the header
   * cell has a token config label.
   */
  @Override
  public boolean isActive(NatEventData natEventData) {
    NatTable table = natEventData.getNatTable();
    assert table != null;
    int columnPosition = natEventData.getColumnPosition();
    assert columnPosition != -1;

    ILayerCell cell = table.getCellByPosition(columnPosition, 1);
    assert cell != null;
    return !cell.getConfigLabels().getLabels().contains(StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
  }

}
