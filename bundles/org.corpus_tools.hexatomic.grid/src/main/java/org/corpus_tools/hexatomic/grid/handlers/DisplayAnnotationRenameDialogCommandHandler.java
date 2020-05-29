package org.corpus_tools.hexatomic.grid.handlers;

import org.corpus_tools.hexatomic.grid.ui.AnnotationRenameDialog;
import org.eclipse.nebula.widgets.nattable.columnRename.DisplayColumnRenameDialogCommand;
import org.eclipse.nebula.widgets.nattable.columnRename.RenameColumnHeaderCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the display of a dialog to change annotation names, and spawns a command to rename the
 * annotation, if the OK button in the dialog is pressed.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class DisplayAnnotationRenameDialogCommandHandler
    extends AbstractLayerCommandHandler<DisplayColumnRenameDialogCommand> {

  private final ColumnHeaderLayer columnHeaderLayer;

  private static final Logger log =
      LoggerFactory.getLogger(DisplayAnnotationRenameDialogCommandHandler.class);

  public DisplayAnnotationRenameDialogCommandHandler(ColumnHeaderLayer columnHeaderLayer) {
    this.columnHeaderLayer = columnHeaderLayer;
  }

  @Override
  protected boolean doCommand(DisplayColumnRenameDialogCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    int columnPosition = command.getColumnPosition();
    String originalQName = this.columnHeaderLayer.getOriginalColumnLabel(columnPosition);
    String newQName = this.columnHeaderLayer.getRenamedColumnLabel(columnPosition);

    AnnotationRenameDialog dialog =
        new AnnotationRenameDialog(Display.getDefault().getActiveShell(), originalQName, newQName);
    Rectangle colHeaderBounds = this.columnHeaderLayer.getBoundsByPosition(columnPosition, 0);
    Point point = new Point(colHeaderBounds.x, colHeaderBounds.y + colHeaderBounds.height);
    dialog.setLocation(command.toDisplayCoordinates(point));
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
