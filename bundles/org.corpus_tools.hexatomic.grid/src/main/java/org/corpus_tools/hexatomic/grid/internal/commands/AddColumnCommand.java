/*-
 * #%L
 * [bundle] Hexatomic Grid Editor
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;

/**
 * An {@link AbstractContextFreeCommand} that is used to create and add a new annotation column of a
 * given type after a given column index.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class AddColumnCommand extends AbstractContextFreeCommand {

  private final int currentColumnIndex;
  private final int currentRowIndex;
  private final ColumnType columnType;
  private final NatTable natTable;

  /**
   * Creates an instance of {@link AddColumnCommand} with the given parameters.
   * 
   * @param columnType The type of the column to add
   * @param currentColumnIndex The index after which to add the new column. If the index is -1, then
   *        we don't know where to insert the new column.
   * @param natTable The {@link NatTable} on which the column is added
   */
  public AddColumnCommand(ColumnType columnType, int currentColumnIndex, int currentRowIndex,
      NatTable natTable) {
    this.columnType = columnType;
    this.currentColumnIndex = currentColumnIndex;
    this.currentRowIndex = currentRowIndex;
    this.natTable = natTable;
  }

  /**
   * Gets the column index after which the column should be added or -1 if unknown.
   * 
   * @return the currentColumnIndex The index after which the column should be added
   */
  public final int getCurrentColumnIndex() {
    return currentColumnIndex;
  }

  /**
   * Gets the row index on which the context menu was brought up.
   * 
   * @return the currentRowIndex The index of the row that was clicked to bring up the context menu
   */
  public final int getCurrentRowIndex() {
    return currentRowIndex;
  }

  /**
   * Gets the {@link ColumnType} of the column to add.
   * 
   * @return the columnType The type of the clumn to add
   */
  public final ColumnType getColumnType() {
    return columnType;
  }

  /**
   * Gets the {@link NatTable} in which to add the column.
   * 
   * @return the natTable The {@link NatTable}
   */
  public final NatTable getNatTable() {
    return natTable;
  }

}
