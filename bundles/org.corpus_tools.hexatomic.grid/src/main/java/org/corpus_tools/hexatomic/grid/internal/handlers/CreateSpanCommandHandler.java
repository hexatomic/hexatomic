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

package org.corpus_tools.hexatomic.grid.internal.handlers;

import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.commands.CreateSpanCommand;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.edit.EditController;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles {@link CreateSpanCommand}s, i.e., the
 * creation of new spans over empty cells in an existing span column..
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class CreateSpanCommandHandler extends AbstractLayerCommandHandler<CreateSpanCommand> {

  private final GridFreezeLayer layer;
  private Logger log = LoggerFactory.getLogger(CreateSpanCommandHandler.class);

  /**
   * Creates a {@link CreateSpanCommandHandler} and sets the {@link #layer} field.
   * 
   * @param gridFreezeLayer the {@link GridFreezeLayer} where this handler is registered.
   */
  public CreateSpanCommandHandler(GridFreezeLayer gridFreezeLayer) {
    this.layer = gridFreezeLayer;
  }

  @Override
  public Class<CreateSpanCommand> getCommandClass() {
    return CreateSpanCommand.class;
  }

  @Override
  protected boolean doCommand(CreateSpanCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    NatTable natTable = command.getNatTable();
    Set<PositionCoordinate> selectedCoordinates = command.getSelectedNonTokenCells();
    PositionCoordinate firstCoordinate = selectedCoordinates.iterator().next();
    // Spans without annotations aren't displayed in the grid, therefore create a new span with the
    // correctly named annotation but a null value first, then immediately edit the value.
    layer.createEmptyAnnotationSpan(selectedCoordinates);
    // Get the first selected cell (as arbitrary choice of any cells that contain the new span) to
    // call the edit command on. Add 1 to column and row positions, as the NatTable (in contrast to
    // the body layer) includes header columns and rows in position counts.
    ILayerCell firstSelectedCell = natTable.getCellByPosition(
        firstCoordinate.getColumnPosition() + 1, firstCoordinate.getRowPosition() + 1);
    // Now that the span with an annotation of value null exists, we can edit the default way.
    EditController.editCell(firstSelectedCell, natTable, null, natTable.getConfigRegistry());
    return true;
  }

}
