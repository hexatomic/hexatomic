/**
 * 
 */
package org.corpus_tools.hexatomic.grid.handlers;

import org.corpus_tools.hexatomic.grid.commands.DisplayAnnotationRenameDialogCommand;
import org.corpus_tools.hexatomic.grid.layers.GridSpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles changes of annotation name on specific cells (as opposed to the column header which is
 * done via the {@link DisplayAnnotationRenameDialogOnColumnCommandHandler}).
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class DisplayAnnotationRenameDialogCommandHandler
    extends AbstractLayerCommandHandler<DisplayAnnotationRenameDialogCommand> {

  private static final Logger log =
      LoggerFactory.getLogger(DisplayAnnotationRenameDialogCommandHandler.class);
  private final GridSpanningDataLayer dataLayer;

  public DisplayAnnotationRenameDialogCommandHandler(GridSpanningDataLayer gridSpanningDataLayer) {
    this.dataLayer = gridSpanningDataLayer;
  }

  @Override
  public Class<DisplayAnnotationRenameDialogCommand> getCommandClass() {
    return DisplayAnnotationRenameDialogCommand.class;
  }

  @Override
  protected boolean doCommand(DisplayAnnotationRenameDialogCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    return true;
  }

}
