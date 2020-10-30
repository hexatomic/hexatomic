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

package org.corpus_tools.hexatomic.grid.internal.actions;

import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.commands.DisplayAnnotationRenameDialogOnCellsCommand;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * An {@link IKeyAction} that triggers a {@link DisplayAnnotationRenameDialogOnCellsCommand} for all
 * selected non-token cells of this instance at once.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ChangeAnnotationNameSelectionAction implements IKeyAction {

  private final Set<PositionCoordinate> selectedNonTokenCells;

  /**
   * Creates a new action with a set of {@link PositionCoordinate}s.
   * 
   * @param selectedNonTokenCells The set of position coordinates that this action should process.
   */
  public ChangeAnnotationNameSelectionAction(Set<PositionCoordinate> selectedNonTokenCells) {
    this.selectedNonTokenCells = selectedNonTokenCells;
  }

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    natTable.doCommand(
        new DisplayAnnotationRenameDialogOnCellsCommand(natTable, this.selectedNonTokenCells));
  }

}
