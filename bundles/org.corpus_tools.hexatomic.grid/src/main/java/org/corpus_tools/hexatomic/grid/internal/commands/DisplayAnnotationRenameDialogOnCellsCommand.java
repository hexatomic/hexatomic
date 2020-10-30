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

package org.corpus_tools.hexatomic.grid.internal.commands;

import java.util.Set;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link AbstractContextFreeCommand} that is used to display a dialog to the user where they can
 * define annotation namespace and name to be used in a rename command.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class DisplayAnnotationRenameDialogOnCellsCommand extends AbstractContextFreeCommand {

  private final Set<PositionCoordinate> selectedNonTokenCells;
  private final NatTable natTable;

  /**
   * Creates a new {@link DisplayAnnotationRenameDialogOnCellsCommand}, which in turn triggers a
   * {@link RenameAnnotationOnCellsCommand} for all cells that have been passed it.
   * 
   * @param selectedNonTokenCells the cells that this command should trigger a
   *        {@link RenameAnnotationOnCellsCommand} on.
   * @param natTable the NatTable.
   */
  public DisplayAnnotationRenameDialogOnCellsCommand(NatTable natTable,
      Set<PositionCoordinate> selectedNonTokenCells) {
    this.natTable = natTable;
    this.selectedNonTokenCells = selectedNonTokenCells;

  }

  /**
   * Returns the selected cells, which do not contain token text.
   * 
   * @return the selectedNonTokenCells
   */
  public final Set<PositionCoordinate> getSelectedNonTokenCells() {
    return selectedNonTokenCells;
  }

  /**
   * Returns the NatTable.
   * 
   * @return the natTable
   */
  public final NatTable getNatTable() {
    return natTable;
  }

}
