package org.corpus_tools.hexatomic.grid.handlers;

import org.corpus_tools.hexatomic.grid.layers.GridColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.columnRename.RenameColumnHeaderCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Handles renaming of annotations - and thus column headers - on behalf of NatTable's generic
 * {@link RenameColumnHeaderCommand}. Registered with the {@link GridColumnHeaderLayer} to which it
 * forwards the new annotation name.
 *
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class RenameAnnotationCommandHandler
    extends AbstractLayerCommandHandler<RenameColumnHeaderCommand> {

  private static final Logger log = LoggerFactory.getLogger(RenameAnnotationCommandHandler.class);
  private final GridColumnHeaderLayer customColumnHeaderLayer;

  public RenameAnnotationCommandHandler(GridColumnHeaderLayer customColumnHeaderLayer) {
    this.customColumnHeaderLayer = customColumnHeaderLayer;
  }

  @Override
  public Class<RenameColumnHeaderCommand> getCommandClass() {
    return RenameColumnHeaderCommand.class;
  }

  @Override
  protected boolean doCommand(RenameColumnHeaderCommand command) {
    log.debug("Executing command {}.", command.toString());
    return this.customColumnHeaderLayer.renameColumnPosition(command.getColumnPosition(),
        command.getCustomColumnName());
  }

}
