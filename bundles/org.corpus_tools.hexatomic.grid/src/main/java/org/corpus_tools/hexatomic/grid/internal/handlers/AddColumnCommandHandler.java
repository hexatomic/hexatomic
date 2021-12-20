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

package org.corpus_tools.hexatomic.grid.internal.handlers;

import org.corpus_tools.hexatomic.grid.GridEditor;
import org.corpus_tools.hexatomic.grid.internal.commands.AddColumnCommand;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.corpus_tools.hexatomic.grid.internal.ui.AnnotationRenameDialog;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} for {@link AddColumnCommand}s, i.e., commands that trigger
 * the addition of a new column to a {@link NatTable} in the {@link GridEditor}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class AddColumnCommandHandler extends AbstractLayerCommandHandler<AddColumnCommand>
    implements ILayerCommandHandler<AddColumnCommand> {

  private static final Logger log = LoggerFactory.getLogger(AddColumnCommandHandler.class);
  private final GridFreezeLayer layer;

  /**
   * Creates a new instance of {@link AddColumnCommandHandler} with the given parameters.
   * 
   * @param gridFreezeLayer The underlying GridFreezeLayer where the handler is registered.
   */
  public AddColumnCommandHandler(GridFreezeLayer gridFreezeLayer) {
    this.layer = gridFreezeLayer;
  }

  @Override
  public Class<AddColumnCommand> getCommandClass() {
    return AddColumnCommand.class;
  }

  @Override
  protected boolean doCommand(AddColumnCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    int currentColumnIndex = command.getCurrentColumnIndex();
    ColumnType columnType = command.getColumnType();
    AnnotationRenameDialog dialog =
        new AnnotationRenameDialog(Display.getDefault().getActiveShell(), null);
    dialog.open();

    if (dialog.isCancelPressed()) {
      log.debug("Execution of command {} cancelled.", getCommandClass().getSimpleName());
      return true;
    }

    // If the index is -1, then we don't know where to insert the new column, so call the layer
    // function respectively with -1
    if (currentColumnIndex == -1) {
      this.layer.addAnnotationColumn(columnType, dialog.getNewQName(), currentColumnIndex);
    } else {
      this.layer.addAnnotationColumn(columnType, dialog.getNewQName(), currentColumnIndex + 1);
    }
    command.getNatTable().refresh();
    return true;
  }

}
