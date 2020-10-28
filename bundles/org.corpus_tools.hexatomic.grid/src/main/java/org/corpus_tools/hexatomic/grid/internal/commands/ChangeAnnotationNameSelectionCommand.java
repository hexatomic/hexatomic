package org.corpus_tools.hexatomic.grid.internal.commands;

import org.eclipse.nebula.widgets.nattable.command.AbstractPositionCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * An {@link AbstractPositionCommand} that is used to change the annotation names on a cell at the
 * given position.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ChangeAnnotationNameSelectionCommand extends AbstractPositionCommand {

  /**
   * Creates a new {@link ChangeAnnotationNameSelectionCommand}.
   * 
   * @param layer The {@link ILayer} to which the column and row position correlate.
   * @param columnPosition The column position for which the command should be processed.
   * @param rowPosition The row position for which the command should be processed.
   */
  public ChangeAnnotationNameSelectionCommand(ILayer layer, int columnPosition, int rowPosition) {
    super(layer, columnPosition, rowPosition);
  }

  private ChangeAnnotationNameSelectionCommand(
      ChangeAnnotationNameSelectionCommand changeAnnotationNameSelectionCommand) {
    super(changeAnnotationNameSelectionCommand);
  }

  @Override
  public ILayerCommand cloneCommand() {
    return new ChangeAnnotationNameSelectionCommand(this);
  }

}
