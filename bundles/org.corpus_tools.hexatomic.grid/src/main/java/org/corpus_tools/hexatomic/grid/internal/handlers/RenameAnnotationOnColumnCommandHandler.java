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

import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.columnRename.RenameColumnHeaderCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles renaming of annotations - and thus column
 * headers - on behalf of NatTable's generic {@link RenameColumnHeaderCommand}. Registered with the
 * {@link GridColumnHeaderLayer} to which it forwards the new annotation name.
 *
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class RenameAnnotationOnColumnCommandHandler
    extends AbstractLayerCommandHandler<RenameColumnHeaderCommand> {

  private static final Logger log =
      LoggerFactory.getLogger(RenameAnnotationOnColumnCommandHandler.class);
  private final GridColumnHeaderLayer customColumnHeaderLayer;

  /**
   * Creates a new {@link RenameAnnotationOnCellsCommandHandler}.
   * 
   * @param customColumnHeaderLayer the column header layer this handler operates on.
   */
  public RenameAnnotationOnColumnCommandHandler(GridColumnHeaderLayer customColumnHeaderLayer) {
    this.customColumnHeaderLayer = customColumnHeaderLayer;
  }

  @Override
  public Class<RenameColumnHeaderCommand> getCommandClass() {
    return RenameColumnHeaderCommand.class;
  }

  @Override
  protected boolean doCommand(RenameColumnHeaderCommand command) {
    log.trace("Executing command to rename column header.");
    return this.customColumnHeaderLayer.renameColumnPosition(command.getColumnPosition(),
        command.getCustomColumnName());
  }

}
