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

package org.corpus_tools.hexatomic.transcription.internal.commands;

import java.util.Set;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * Insert a row (timeline item) at a specific position.
 * 
 * @author Thomas Krause {@literal <thomas.krause@hu-berlin.de>}
 *
 */
public class InsertRowCommand extends AbstractContextFreeCommand {

  private final Set<PositionCoordinate> selectedCells;

  /**
   * Creates a new {@link InsertRowCommand}.
   * 
   * @param selectedTimelineItems A set of the {@link PositionCoordinate}s of the currently selected
   *        cells
   */
  public InsertRowCommand(Set<PositionCoordinate> selectedCells) {
    this.selectedCells = selectedCells;
  }

  /**
   * Returns the set of {@link PositionCoordinate}s of the currently selected cells.
   * 
   * @return The selected cell positions.
   */
  public final Set<PositionCoordinate> getSelectedCells() {
    return selectedCells;
  }

}
