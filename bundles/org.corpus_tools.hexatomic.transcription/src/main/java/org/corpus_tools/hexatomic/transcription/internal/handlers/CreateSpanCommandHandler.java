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

package org.corpus_tools.hexatomic.transcription.internal.handlers;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import org.corpus_tools.hexatomic.transcription.internal.commands.CreateSpanCommand;
import org.corpus_tools.hexatomic.transcription.internal.data.TimelineTokenDataProvider;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.edit.EditController;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles {@link CreateSpanCommand}s and adds a new
 * span over the currently selected cells (in one column).
 * 
 * @author Thomas Krause {@literal <thomas.krause@hu-berlin.de>}
 */
public class CreateSpanCommandHandler extends AbstractLayerCommandHandler<CreateSpanCommand> {


  private final TimelineTokenDataProvider dataProvider;

  private Logger log = LoggerFactory.getLogger(CreateSpanCommandHandler.class);

  /**
   * Creates a {@link CreateSpanCommandHandler} and sets the {@link #layer} field.
   * 
   */
  public CreateSpanCommandHandler(TimelineTokenDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  @Override
  public Class<CreateSpanCommand> getCommandClass() {
    return CreateSpanCommand.class;
  }

  @Override
  protected boolean doCommand(CreateSpanCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    Set<PositionCoordinate> selectedCoordinates = command.getSelectedCells();

    OptionalInt optionalColumn =
        selectedCoordinates.stream().mapToInt(PositionCoordinate::getColumnPosition).min();

    if (optionalColumn.isPresent()) {
      int selectedColumn = optionalColumn.getAsInt();
      
      // Get the range of TLIs to cover
      List<Integer> coveredRows =
          selectedCoordinates.stream().mapToInt(PositionCoordinate::getRowPosition).boxed()
              .collect(Collectors.toList());
      
      // Add a new token with an empty value for the selected TLIs
      dataProvider.createToken(selectedColumn, coveredRows, "");

      ILayerCell firstSelectedCell =
          command.getTable().getCellByPosition(selectedColumn + 1, coveredRows.get(0) + 1);
      // Now that the span with an annotation of value null exists, we can edit the default way.
      EditController.editCell(firstSelectedCell, command.getTable(), "",
          command.getTable().getConfigRegistry());

      return true;
    }

    return false;

  }

}
