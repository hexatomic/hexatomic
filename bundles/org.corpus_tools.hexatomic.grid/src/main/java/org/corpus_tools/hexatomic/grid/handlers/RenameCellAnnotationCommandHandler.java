/**
 * 
 */
package org.corpus_tools.hexatomic.grid.handlers;

import org.corpus_tools.hexatomic.grid.commands.RenameCellAnnotationCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A handler for the {@link RenameCellAnnotationCommand}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class RenameCellAnnotationCommandHandler
    extends AbstractLayerCommandHandler<RenameCellAnnotationCommand> {

  private static final Logger log =
      LoggerFactory.getLogger(RenameCellAnnotationCommandHandler.class);

  @Override
  public Class<RenameCellAnnotationCommand> getCommandClass() {
    return RenameCellAnnotationCommand.class;
  }

  @Override
  protected boolean doCommand(RenameCellAnnotationCommand command) {
    log.debug("RUNNING THE COMMAND!");
    return true;
  }

}
