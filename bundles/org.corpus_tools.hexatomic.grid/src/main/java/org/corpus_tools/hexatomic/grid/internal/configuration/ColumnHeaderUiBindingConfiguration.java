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

package org.corpus_tools.hexatomic.grid.internal.configuration;

import org.corpus_tools.hexatomic.grid.internal.GridHelper;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.events.MouseEvent;

/**
 * A custom UI binding configuration for the column header.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ColumnHeaderUiBindingConfiguration extends AbstractUiBindingConfiguration {

  private final SelectionLayer selectionLayer;

  public ColumnHeaderUiBindingConfiguration(SelectionLayer selectionLayer) {
    this.selectionLayer = selectionLayer;
  }

  @Override
  public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {

    // Bind mouse double-clicks to renaming the annotation name
    uiBindingRegistry.registerDoubleClickBinding(MouseEventMatcher.columnHeaderLeftClick(0),
        new IMouseAction() {
          @Override
          public void run(NatTable natTable, MouseEvent event) {
            PositionCoordinate[] selectedCellCoordinates =
                selectionLayer.getSelectedCellPositions();
            // Get column and check if it's the token column
            for (PositionCoordinate coord : selectedCellCoordinates) {
              if (GridHelper.isTokenColumnAtPosition(natTable, coord.getColumnPosition(), false)) {
                System.err.println("TOKEN, IGNORING");
              } else {
                System.out.println("Double Click Detected on " + coord.getColumnPosition());
              }
            }
          }
        });
  }

}
