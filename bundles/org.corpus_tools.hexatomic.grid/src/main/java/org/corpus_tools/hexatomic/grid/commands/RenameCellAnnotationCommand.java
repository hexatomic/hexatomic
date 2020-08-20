/**
 * 
 */
package org.corpus_tools.hexatomic.grid.commands;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class RenameCellAnnotationCommand implements ILayerCommand {

  private final PositionCoordinate[] selectedCellPositions;
  private final NatTable natTable;

  public RenameCellAnnotationCommand(PositionCoordinate[] selectedCellPositions,
      NatTable natTable) {
    this.selectedCellPositions = selectedCellPositions;
    this.natTable = natTable;
  }

  @Override
  public boolean convertToTargetLayer(ILayer targetLayer) {
    return true;
  }

  @Override
  public ILayerCommand cloneCommand() {
    return new RenameCellAnnotationCommand(selectedCellPositions, natTable);
  }

}
