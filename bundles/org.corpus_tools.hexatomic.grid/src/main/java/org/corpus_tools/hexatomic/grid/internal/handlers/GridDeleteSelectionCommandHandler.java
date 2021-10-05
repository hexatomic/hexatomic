package org.corpus_tools.hexatomic.grid.internal.handlers;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.edit.command.DeleteSelectionCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.DeleteSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.command.EditUtils;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * Handler for deleting values in selected cells.
 * 
 * @see DeleteSelectionCommand
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridDeleteSelectionCommandHandler extends DeleteSelectionCommandHandler {

  private SelectionLayer selectionLayer;
  private IUniqueIndexLayer upperLayer;

  /**
   * Creates a new {@link GridDeleteSelectionCommandHandler}.
   *
   * @param selectionLayer The {@link SelectionLayer} needed to determine the currently selected
   *        cells.
   */
  public GridDeleteSelectionCommandHandler(SelectionLayer selectionLayer) {
    super(selectionLayer, null);
  }

  /**
   * Creates a new {@link GridDeleteSelectionCommandHandler} that performs the edit checks based on
   * the given upper layer. Needed for example if the upper layer adds information that is needed
   * for checks, e.g. a tree layer.
   *
   * @param selectionLayer The {@link SelectionLayer} needed to determine the currently selected
   *        cells.
   * @param upperLayer The layer on top of the given {@link SelectionLayer} to which the selection
   *        should be converted to. Can be <code>null</code> which causes the resulting selected
   *        cells to be related to the {@link SelectionLayer}.
   */
  public GridDeleteSelectionCommandHandler(SelectionLayer selectionLayer,
      IUniqueIndexLayer upperLayer) {
    super(selectionLayer, upperLayer);
    this.selectionLayer = selectionLayer;
    this.upperLayer = upperLayer;
  }

  @Override
  public boolean doCommand(ILayer layer, DeleteSelectionCommand command) {
    // Sort position coordinates, so that columns appear in reverse order, and are updated from the
    // last column forwards.
    PositionCoordinate[] coordsArray = this.selectionLayer.getSelectedCellPositions();
    Arrays.sort(coordsArray,
        Comparator.comparing(PositionCoordinate::getColumnPosition).reversed());
    if (EditUtils.allCellsEditable(this.selectionLayer, this.upperLayer,
        command.getConfigRegistry())) {
      for (PositionCoordinate coord : coordsArray) {
        coord.getLayer().doCommand(new UpdateDataCommand(coord.getLayer(),
            coord.getColumnPosition(), coord.getRowPosition(), null));
      }
    }
    return true;
  }


}
