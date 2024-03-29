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

package org.corpus_tools.hexatomic.grid.internal.actions;

import org.corpus_tools.hexatomic.grid.internal.commands.AddColumnCommand;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * An {@link IContextFreeAction} that triggers an {@link AddColumnCommand} for a given cell and a
 * given {@link ColumnType} for which the action was triggered.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class AddColumnAction implements IContextFreeAction, IKeyAction {

  private int colIndex;
  private final ColumnType columnType;
  private final int rowIndex;

  /**
   * Creates an {@link AddColumnAction} with the given parameters.
   * 
   * @param columnType The type of the column to add
   * @param colIndex The index after which the column should be added
   * @param rowIndex The index of the row which was clicked to bring up the context menu
   */
  public AddColumnAction(ColumnType columnType, int colIndex, int rowIndex) {
    this.columnType = columnType;
    this.colIndex = colIndex;
    this.rowIndex = rowIndex;
  }

  @Override
  public void run(NatTable natTable) {
    natTable.doCommand(new AddColumnCommand(columnType, colIndex, rowIndex, natTable));
  }

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    run(natTable);
  }

}
