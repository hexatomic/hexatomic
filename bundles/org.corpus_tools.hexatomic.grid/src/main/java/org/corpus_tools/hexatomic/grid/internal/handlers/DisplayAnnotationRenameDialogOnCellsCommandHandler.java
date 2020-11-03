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

import org.corpus_tools.hexatomic.grid.internal.GridHelper;
import org.corpus_tools.hexatomic.grid.internal.commands.DisplayAnnotationRenameDialogOnCellsCommand;
import org.corpus_tools.hexatomic.grid.internal.commands.RenameAnnotationOnCellsCommand;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.corpus_tools.hexatomic.grid.internal.ui.AnnotationRenameDialog;
import org.eclipse.nebula.widgets.nattable.columnRename.RenameColumnHeaderCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles the display of a dialog to change annotation
 * names, and spawns a command to rename the annotation, if the OK button in the dialog is pressed.
 * Registered with the {@link GridFreezeLayer} on which it calls the rename command.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class DisplayAnnotationRenameDialogOnCellsCommandHandler
    extends AbstractLayerCommandHandler<DisplayAnnotationRenameDialogOnCellsCommand> {

  private static final Logger log =
      LoggerFactory.getLogger(DisplayAnnotationRenameDialogOnCellsCommandHandler.class);

  @Override
  protected boolean doCommand(DisplayAnnotationRenameDialogOnCellsCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());

    // If only cells from one column have been selected, get the current qualified annotation name,
    // so that it can be displayed.
    String oldQName = null;
    if (command.displayOldQName()) {
      GridColumnHeaderLayer columnHeaderLayer =
          GridHelper.getColumnHeaderLayer(command.getNatTable());
      oldQName = (String) columnHeaderLayer
          .getDataValueByPosition(command.getCellMapByColumn().keySet().iterator().next(), 0);
    }

    AnnotationRenameDialog dialog =
        new AnnotationRenameDialog(Display.getDefault().getActiveShell(), oldQName);
    dialog.open();

    if (dialog.isCancelPressed()) {
      log.debug("Execution of command {} cancelled.", getCommandClass().getSimpleName());
      return true;
    }

    log.debug("Returning delegate command {}.", RenameColumnHeaderCommand.class.getSimpleName());
    return command.getNatTable().doCommand(new RenameAnnotationOnCellsCommand(command.getNatTable(),
        command.getCellMapByColumn(), dialog.getNewQName()));
  }

  @Override
  public Class<DisplayAnnotationRenameDialogOnCellsCommand> getCommandClass() {
    return DisplayAnnotationRenameDialogOnCellsCommand.class;
  }

}
