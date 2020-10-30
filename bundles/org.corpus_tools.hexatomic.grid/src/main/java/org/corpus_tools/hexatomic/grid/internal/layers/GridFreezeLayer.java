/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
