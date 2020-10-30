/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.commands.RenameAnnotationOnCellsCommand;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles renaming of annotations in selected cells.
 * Registered with the {@link GridFreezeLayer} to which it forwards the new annotation name.
 *
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class RenameAnnotationOnCellsCommandHandler
    extends AbstractLayerCommandHandler<RenameAnnotationOnCellsCommand> {

  private static final Logger log =
      LoggerFactory.getLogger(RenameAnnotationOnCellsCommandHandler.class);

  @Override
  public Class<RenameAnnotationOnCellsCommand> getCommandClass() {
    return RenameAnnotationOnCellsCommand.class;
  }

  @Override
  protected boolean doCommand(RenameAnnotationOnCellsCommand command) {
    log.trace("Executing command to rename annotations in cells.");

    // Get the required layers
    ILayer underlyingLayer = command.getNatTable().getUnderlyingLayerByPosition(0, 0);
    GridLayer gridLayer = null;
    if (!(underlyingLayer instanceof GridLayer)) {
      throw new HexatomicRuntimeException("Underlying layer of NatTable is not of type "
          + GridLayer.class.getSimpleName() + "! Please report this as a bug.");
    } else {
      gridLayer = (GridLayer) underlyingLayer;
    }
    // We can be certain that the column header layer is of the correct type, as only one column
    // header layer can be registered.
    ILayer columnHeaderLayer = gridLayer.getColumnHeaderLayer();
    // We should make sure that this is actually a GridFreezeLayer,otherwise the body layer will not
    // provide the right API we want to call later on.
    ILayer freezeLayerCandidate = gridLayer.getBodyLayer();
    GridFreezeLayer freezeLayer = null;
    if (!(freezeLayerCandidate instanceof GridFreezeLayer)) {
      throw new HexatomicRuntimeException("Freeze layer is not of expected type "
          + GridFreezeLayer.class.getSimpleName() + "! Please report this as a bug.");
    } else {
      freezeLayer = (GridFreezeLayer) freezeLayerCandidate;
    }
    // Run the rename for all cells
    for (PositionCoordinate cellPosition : command.getSelectedNonTokenCells()) {
      // Get the old qualified name from the column header
      Object columnValue =
          columnHeaderLayer.getDataValueByPosition(cellPosition.getColumnPosition(), 0);
      if (columnValue == null || !(columnValue instanceof String)) {
        throw new HexatomicRuntimeException("Column value is null or not an instance of String!");
      } else {
        // Call the actual rename
        return freezeLayer.renameCellPosition(cellPosition.getColumnPosition(),
            cellPosition.getRowPosition(), (String) columnValue, command.getNewQName());
      }
    }
    return false;
  }

}
