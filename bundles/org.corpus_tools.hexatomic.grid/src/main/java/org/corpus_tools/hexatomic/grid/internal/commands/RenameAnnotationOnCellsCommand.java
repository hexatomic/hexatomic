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
 * An {@link AbstractContextFreeCommand} that is used to trigger a change of annotation names on a
 * set of table cells.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class RenameAnnotationOnCellsCommand extends AbstractContextFreeCommand {

  private final Map<Integer, Set<Integer>> cellMapByColumn;
  private final String newQName;
  private final NatTable natTable;

  /**
   * Creates a new {@link RenameAnnotationOnCellsCommand}.
   * 
   * @param natTable the NatTable.
   * @param cellMapByColumn a map of cells from column position to row position within that column.
   * @param newQName the new annotation name that all cell value annotations this command operates
   *        on should be renamed to.
   */
  public RenameAnnotationOnCellsCommand(NatTable natTable,
      Map<Integer, Set<Integer>> cellMapByColumn, String newQName) {
    this.natTable = natTable;
    this.cellMapByColumn = cellMapByColumn;
    this.newQName = newQName;
  }

  /**
   * Returns the new annotation name that all cell value annotations this command operates on should
   * be renamed to.
   * 
   * @return the newQName the new qualified annotation name.
   */
  public final String getNewQName() {
    return newQName;
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
   * Returns cells as a map of column positions to row positions within that column.
   * 
   * @return the cellMapByColumn
   */
  public final Map<Integer, Set<Integer>> getCellMapByColumn() {
    return cellMapByColumn;
  }

}
