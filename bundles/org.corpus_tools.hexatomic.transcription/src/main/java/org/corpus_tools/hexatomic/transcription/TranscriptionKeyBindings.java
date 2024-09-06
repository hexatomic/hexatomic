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

package org.corpus_tools.hexatomic.transcription;

import java.util.Set;
import java.util.stream.Collectors;
import org.corpus_tools.hexatomic.transcription.internal.commands.CreateSpanCommand;
import org.corpus_tools.hexatomic.transcription.internal.commands.InsertRowCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.edit.action.DeleteSelectionAction;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;

public class TranscriptionKeyBindings extends AbstractUiBindingConfiguration
    implements IConfiguration {

  private final SelectionLayer selectionLayer;

  public TranscriptionKeyBindings(SelectionLayer selectionLayer) {
    this.selectionLayer = selectionLayer;
  }

  @Override
  public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
    uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.ALT, 's'), (table, keyEvent) -> {

      Set<PositionCoordinate> cellPositions = Set.of(selectionLayer.getSelectedCellPositions());
      Set<Integer> columns = cellPositions.stream().map(PositionCoordinate::getColumnPosition)
          .collect(Collectors.toSet());
      if (columns.size() == 1) {
        table.doCommand(new CreateSpanCommand(cellPositions, table));
      }
    });
    // Ctrl + Shift + (plus) inserts a row, which is consistent with Excel shortcuts
    uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.SHIFT | SWT.CTRL, '+'),
        (table, keyEvent) -> {

          Set<PositionCoordinate> cellPositions = Set.of(selectionLayer.getSelectedCellPositions());
          Set<Integer> columns = cellPositions.stream().map(PositionCoordinate::getColumnPosition)
              .collect(Collectors.toSet());
          if (columns.size() == 1) {
            table.doCommand(new InsertRowCommand(cellPositions));
          }
        });


    uiBindingRegistry.registerFirstKeyBinding(new KeyEventMatcher(SWT.DEL),
        new DeleteSelectionAction());
  }

}
