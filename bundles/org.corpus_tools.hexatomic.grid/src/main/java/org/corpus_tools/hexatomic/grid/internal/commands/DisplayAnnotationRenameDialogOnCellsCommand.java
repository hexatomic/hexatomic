package org.corpus_tools.hexatomic.grid.internal.commands;

import java.util.Set;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link AbstractContextFreeCommand} that is used to display a dialog to the user where they can
 * define annotation namespace and name to be used in a rename command.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class DisplayAnnotationRenameDialogOnCellsCommand extends AbstractContextFreeCommand {

  private final Set<PositionCoordinate> selectedNonTokenCells;
  private final NatTable natTable;

  /**
   * Creates a new {@link DisplayAnnotationRenameDialogOnCellsCommand}, which in turn triggers a
   * {@link RenameAnnotationOnCellsCommand} for all cells that have been passed it.
   * 
   * @param selectedNonTokenCells the cells that this command should trigger a
   *        {@link RenameAnnotationOnCellsCommand} on.
   * @param natTable the NatTable.
   */
  public DisplayAnnotationRenameDialogOnCellsCommand(NatTable natTable,
      Set<PositionCoordinate> selectedNonTokenCells) {
    this.natTable = natTable;
    this.selectedNonTokenCells = selectedNonTokenCells;

  }

  /**
   * Returns the selected cells, which do not contain token text.
   * 
   * @return the selectedNonTokenCells
   */
  public final Set<PositionCoordinate> getSelectedNonTokenCells() {
    return selectedNonTokenCells;
  }

  /**
   * Returns the NatTable.
   * 
   * @return the natTable
   */
  public final NatTable getNatTable() {
    return natTable;
  }

}
