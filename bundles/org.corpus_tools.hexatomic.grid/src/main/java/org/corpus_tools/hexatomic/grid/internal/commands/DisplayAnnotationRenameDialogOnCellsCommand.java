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

import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;

/**
 * An {@link AbstractContextFreeCommand} that is used to display a dialog to the user where they can
 * define annotation namespace and name to be used in a rename command.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class DisplayAnnotationRenameDialogOnCellsCommand extends AbstractContextFreeCommand {

  private final Map<Integer, Set<Integer>> cellMapByColumn;
  private final NatTable natTable;
  private final boolean displayOldQName;

  /**
   * Creates a new {@link DisplayAnnotationRenameDialogOnCellsCommand}, which in turn triggers a
   * {@link RenameAnnotationOnCellsCommand} for all cells that have been passed it.
   * 
   * @param natTable the NatTable.
   * @param cellMapByColumn a map of cells from column position to row position within that column.
   */
  public DisplayAnnotationRenameDialogOnCellsCommand(NatTable natTable,
      Map<Integer, Set<Integer>> cellMapByColumn) {
    this.natTable = natTable;
    this.cellMapByColumn = cellMapByColumn;
    this.displayOldQName = cellMapByColumn.keySet().size() == 1;
  }

  /**
   * Returns the NatTable.
   * 
   * @return the natTable
   */
  public final NatTable getNatTable() {
    return natTable;
  }

  /**
   * Returns whether the old gualified name should be displayed in the dialog.
   * 
   * @return whether the old qualified name should be displayed.
   */
  public final boolean displayOldQName() {
    return displayOldQName;
  }

  /**
   * Returns cells as a map of column positions to row positions within that column.
   * 
   * @return the cellMapByColumn
   */
  public final Map<Integer, Set<Integer>> getCellMapByColumn() {
    return cellMapByColumn;
  }

}
