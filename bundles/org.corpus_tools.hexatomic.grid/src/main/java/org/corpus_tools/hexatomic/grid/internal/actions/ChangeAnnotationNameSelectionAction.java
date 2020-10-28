/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.actions;

import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.commands.ChangeAnnotationNameSelectionCommand;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * An {@link IKeyAction} that triggers a {@link ChangeAnnotationNameSelectionCommand} on all
 * selected non-token cells of this instance.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class ChangeAnnotationNameSelectionAction implements IKeyAction {

  private final Set<PositionCoordinate> selectedNonTokenCells;

  /**
   * Create a new action with a set of {@link PositionCoordinate}s.
   * 
   * @param selectedNonTokenCells The set of position coordinates that this action should process.
   */
  public ChangeAnnotationNameSelectionAction(Set<PositionCoordinate> selectedNonTokenCells) {
    this.selectedNonTokenCells = selectedNonTokenCells;
  }

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    for (PositionCoordinate cell : this.selectedNonTokenCells) {
      natTable.doCommand(new ChangeAnnotationNameSelectionCommand(null, cell.getColumnPosition(),
          cell.getRowPosition()));
    }
  }

}
