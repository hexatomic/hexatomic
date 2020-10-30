package org.corpus_tools.hexatomic.grid.internal.layers;

import org.corpus_tools.hexatomic.grid.internal.handlers.DisplayAnnotationRenameDialogOnCellsCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.RenameAnnotationOnCellsCommandHandler;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;

/**
 * A {@link CompositeFreezeLayer} that registers commands triggered on body cells, and implements
 * their handling on the underlying model.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class GridFreezeLayer extends CompositeFreezeLayer {

  /**
   * Creates a {@link GridFreezeLayer}.
   * 
   * @param freezeLayer the underlying freeze layer.
   * @param viewportLayer the viewport layer.
   * @param selectionLayer the selection layer.
   */
  public GridFreezeLayer(FreezeLayer freezeLayer, ViewportLayer viewportLayer,
      SelectionLayer selectionLayer) {
    super(freezeLayer, viewportLayer, selectionLayer);
  }


  /**
   * Triggers a rename of the cell value in the underlying data model.
   * 
   * @param columnPosition the column position of the edited cell in this layer.
   * @param rowPosition the row position of the edited cell in this layer.
   * @param currentQName the current (to be changed) qualified annotation name of the cell value.
   * @param newQName the new qualified annotation name that the annotation of the cell value should
   *        be renamed to.
   * @return whether the cell value has successfully been renamed.
   */
  public boolean renameCellPosition(int columnPosition, int rowPosition, String currentQName,
      String newQName) {
    ILayerCell cellToEdit = getCellByPosition(columnPosition, rowPosition);
    Object nodeToEdit = cellToEdit.getDataValue();
    // TODO Missing implementation
    return true;
  }

  /**
   * Registers custom command handlers.
   */
  @Override
  protected void registerCommandHandlers() {
    super.registerCommandHandlers();
    registerCommandHandler(new RenameAnnotationOnCellsCommandHandler());
    registerCommandHandler(new DisplayAnnotationRenameDialogOnCellsCommandHandler());
  }
}
