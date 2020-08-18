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

package org.corpus_tools.hexatomic.grid.commands;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * Command to display an annotation rename dialog on.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class DisplayAnnotationRenameDialogCommand extends AbstractContextFreeCommand {

  private final NatTable natTable;
  private final SelectionLayer selectionLayer;
  private final PositionCoordinate[] selectedCellPositions;

  /**
   * TODO.
   * 
   * @param natTable The natTable
   * @param selectionLayer The selection layer
   * @param selectedCellPositions The positions
   */
  public DisplayAnnotationRenameDialogCommand(NatTable natTable, SelectionLayer selectionLayer,
      PositionCoordinate[] selectedCellPositions) {
    this.natTable = natTable;
    this.selectionLayer = selectionLayer;
    this.selectedCellPositions = selectedCellPositions;
  }


}
