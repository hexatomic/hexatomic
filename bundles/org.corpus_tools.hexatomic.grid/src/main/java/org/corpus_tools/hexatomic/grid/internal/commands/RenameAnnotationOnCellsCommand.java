package org.corpus_tools.hexatomic.grid.internal.commands;

import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link AbstractContextFreeCommand} that is used to trigger a change of annotation names on a
 * set of table cells.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class RenameAnnotationOnCellsCommand extends AbstractContextFreeCommand {

  private final GridFreezeLayer gridFreezeLayer;
  private final Set<PositionCoordinate> selectedNonTokenCells;
  private final String newQName;

  /**
   * Creates a new {@link RenameAnnotationOnCellsCommand}.
   * 
   * @param gridFreezeLayer the freeze layer this command operates on.
   * @param selectedNonTokenCells the set of position coordinates this command operates on.
   * @param newQName the new annotation name that all cell value annotations this command operates
   *        on should be renamed to.
   */
  public RenameAnnotationOnCellsCommand(GridFreezeLayer gridFreezeLayer,
      Set<PositionCoordinate> selectedNonTokenCells, String newQName) {
    this.gridFreezeLayer = gridFreezeLayer;
    this.selectedNonTokenCells = selectedNonTokenCells;
    this.newQName = newQName;
  }

  /**
   * Returns the freeze layer this command operates on.
   * 
   * @return the gridFreezeLayer the freeze layer this command operates on.
   */
  public final GridFreezeLayer getFreezeLayer() {
    return gridFreezeLayer;
  }

  /**
   * Return the set of position coordinates this command operates on.
   * 
   * @return the selectedNonTokenCells the set of position coordinates this command operates on.
   */
  public final Set<PositionCoordinate> getSelectedNonTokenCells() {
    return selectedNonTokenCells;
  }

  /**
   * Returns the new annotation name that all cell value annotations this command operates on should
   * be renamed to.
   * 
   * @return the newQName the new qualified annotation name.
   */
  public final String getNewQName() {
    return newQName;
  }

}
