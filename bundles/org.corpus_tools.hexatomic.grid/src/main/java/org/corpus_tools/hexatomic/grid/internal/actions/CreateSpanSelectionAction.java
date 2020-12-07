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
import org.corpus_tools.hexatomic.grid.internal.commands.CreateSpanCommand;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link IContextFreeAction} that triggers a {@link CreateSpanCommand} for the currently
 * selected empty cells in an existing span column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class CreateSpanSelectionAction implements IContextFreeAction {

  private final Set<PositionCoordinate> selectedNonTokenCells;

  /**
   * Creates a new {@link CreateSpanSelectionAction}.
   * 
   * @param selectedNonTokenCells a set of {@link PositionCoordinate}s of the currently selected
   *        cells.
   */
  public CreateSpanSelectionAction(Set<PositionCoordinate> selectedNonTokenCells) {
    this.selectedNonTokenCells = selectedNonTokenCells;
  }

  @Override
  public void run(NatTable natTable) {
    natTable.doCommand(new CreateSpanCommand(natTable, selectedNonTokenCells));

  }

}
