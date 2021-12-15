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
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.ui.AnnotationRenameDialog;
import org.eclipse.nebula.widgets.nattable.columnRename.DisplayColumnRenameDialogCommand;
import org.eclipse.nebula.widgets.nattable.columnRename.RenameColumnHeaderCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles the display of a dialog to change annotation
 * names, and spawns a command to rename the annotation, if the OK button in the dialog is pressed.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class DisplayAnnotationRenameDialogOnColumnCommandHandler
    extends AbstractLayerCommandHandler<DisplayColumnRenameDialogCommand> {

  private final ColumnHeaderLayer columnHeaderLayer;

  private static final Logger log =
      LoggerFactory.getLogger(DisplayAnnotationRenameDialogOnColumnCommandHandler.class);

  public DisplayAnnotationRenameDialogOnColumnCommandHandler(ColumnHeaderLayer columnHeaderLayer) {
    this.columnHeaderLayer = columnHeaderLayer;
  }

  @Override
  protected boolean doCommand(DisplayColumnRenameDialogCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    int columnPosition = command.getColumnPosition();
    String originalQName = null;
    if (!(columnHeaderLayer instanceof GridColumnHeaderLayer)) {
      throw new LayerSetupException("ColumnHeaderLayer", this.columnHeaderLayer,
          GridColumnHeaderLayer.class);
    } else {
      GridColumnHeaderLayer gridColumnHeaderLayer = (GridColumnHeaderLayer) this.columnHeaderLayer;
      originalQName = gridColumnHeaderLayer.getAnnotationQName(columnPosition);
    }

    AnnotationRenameDialog dialog =
        new AnnotationRenameDialog(Display.getDefault().getActiveShell(), originalQName);
    dialog.open();

    if (dialog.isCancelPressed()) {
      log.debug("Execution of command {} cancelled.", getCommandClass().getSimpleName());
      return true;
    }

    log.debug("Returning delegate command {}.", RenameColumnHeaderCommand.class.getSimpleName());
    return this.columnHeaderLayer.doCommand(new RenameColumnHeaderCommand(this.columnHeaderLayer,
        columnPosition, dialog.getNewQName()));
  }

  @Override
  public Class<DisplayColumnRenameDialogCommand> getCommandClass() {
    return DisplayColumnRenameDialogCommand.class;
  }

}
