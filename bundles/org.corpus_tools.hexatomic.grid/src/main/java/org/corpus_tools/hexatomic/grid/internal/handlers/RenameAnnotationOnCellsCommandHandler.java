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

import org.corpus_tools.hexatomic.grid.LayerSetupException;
import org.corpus_tools.hexatomic.grid.internal.commands.RenameAnnotationOnCellsCommand;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
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
    GridFreezeLayer freezeLayer = getBodyLayer(command.getNatTable());
    freezeLayer.bulkRenameCellPositions(command.getCellMapByColumn(), command.getNewQName());
    return true;
  }

  private GridFreezeLayer getBodyLayer(NatTable natTable) {
    // Due to how the NatTable is set up, this returns a GridLayer
    ILayer layer = natTable.getUnderlyingLayerByPosition(0, 0);
    while (layer != null) {
      if (layer instanceof GridFreezeLayer) {
        return (GridFreezeLayer) layer;
      }
      // Getting the underlying layer by position 1, 1 is necessary to hit a position in the grid
      // body, i.e., excluding the header rows and columns.
      layer = layer.getUnderlyingLayerByPosition(1, 1);
    }
    throw new LayerSetupException("Bottom layer of NatTable", layer, GridFreezeLayer.class);
  }

}
