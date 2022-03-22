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

import java.util.Map;
import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.data.Column;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider;
import org.corpus_tools.hexatomic.grid.internal.handlers.AddColumnCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.CreateSpanCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.DisplayAnnotationRenameDialogOnCellsCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.RenameAnnotationOnCellsCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.SplitSpanCommandHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Display;

/**
 * A {@link CompositeFreezeLayer} that registers commands triggered on body cells, and implements
 * their handling on the underlying model.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class GridFreezeLayer extends CompositeFreezeLayer {

  private static final String COLUMN_ALREADY_EXISTS = "Column already exists";
  private final GraphDataProvider bodyDataProvider;
  private final SelectionLayer selectionLayer;

  /**
   * Creates a {@link GridFreezeLayer}.
   * 
   * @param freezeLayer the underlying freeze layer.
   * @param viewportLayer the viewport layer.
   * @param selectionLayer the selection layer.
   * @param bodyDataProvider the body data layer.
   */
  public GridFreezeLayer(FreezeLayer freezeLayer, ViewportLayer viewportLayer,
      SelectionLayer selectionLayer, GraphDataProvider bodyDataProvider) {
    super(freezeLayer, viewportLayer, selectionLayer);
    this.selectionLayer = selectionLayer;
    this.bodyDataProvider = bodyDataProvider;

  }

  /**
   * Triggers a bulk rename of cell values in the underlying data model.
   * 
   * @param cellMapByColumn a map of column positions to sets of row positions.
   * @param newQName the new qualified annotation name.
   */
  public void bulkRenameCellPositions(Map<Integer, Set<Integer>> cellMapByColumn, String newQName) {
    bodyDataProvider.bulkRenameAnnotations(cellMapByColumn, newQName);
  }

  /**
   * Triggers the creation of a new span annotated with a <code>null</code> value in the underlying
   * data model.
   * 
   * @param selectedCoordinates a set of the {@link PositionCoordinate}s of the currently selected
   *        cells
   */
  public void createEmptyAnnotationSpan(Set<PositionCoordinate> selectedCoordinates) {
    bodyDataProvider.createEmptyAnnotationSpan(selectedCoordinates);
  }

  /**
   * Triggers the splitting of spans into single cell spans in the underlying data model.
   * 
   * @param selectedCoordinates a set of the {@link PositionCoordinate}s of the currently selected
   *        cells
   */
  public void splitAnnotationSpans(Set<PositionCoordinate> selectedCoordinates) {
    bodyDataProvider.splitAnnotationsSpans(selectedCoordinates);
  }

  /**
   * Creates a new column of the given type, with the given qualified annotation name, and adds it
   * to the list of columns at the given insertion index.
   * 
   * @param type The type of the column to be created
   * @param annoQName The qualified annotation name of the column
   * @param insertionIndex The index at which the column to be created should be inserted into the
   *        list of columns, or -1 when it should be added at the end of the list
   */
  public void addAnnotationColumn(ColumnType type, String annoQName, int insertionIndex) {
    // Counter starts at one as the added column represents the first existing column 
    // for the specified qualified annotation name.
    int existingColumnCounter = 1;
    for (Column column : bodyDataProvider.getColumns()) {
      if (column.getColumnValue().equals(annoQName)) {
        existingColumnCounter++;
      }
    }
    Column newColumn = null;
    if (existingColumnCounter > 1) {
      if (type == ColumnType.TOKEN_ANNOTATION) {
        MessageDialog.openError(Display.getCurrent().getActiveShell(), COLUMN_ALREADY_EXISTS,
            "A token annotation column for the token annotation " + annoQName + " already exists!");
        return;
      } else {
        newColumn = new Column(type, annoQName, existingColumnCounter);
      }
    } else {
      newColumn = new Column(type, annoQName);
    }
    if (insertionIndex == -1) {
      bodyDataProvider.getColumns().add(newColumn);
    } else {
      bodyDataProvider.getColumns().add(insertionIndex, newColumn);
    }
  }

  /**
   * Registers custom command handlers.
   */
  @Override
  protected void registerCommandHandlers() {
    super.registerCommandHandlers();
    registerCommandHandler(new RenameAnnotationOnCellsCommandHandler());
    registerCommandHandler(new DisplayAnnotationRenameDialogOnCellsCommandHandler(this));
    registerCommandHandler(new CreateSpanCommandHandler(this));
    registerCommandHandler(new SplitSpanCommandHandler(this));
    registerCommandHandler(new AddColumnCommandHandler(this));
  }

  /**
   * Returns the selection layer for this body layer.
   * 
   * @return the selectionLayer the {@link SelectionLayer} of the body layer
   */
  public final SelectionLayer getSelectionLayer() {
    return selectionLayer;
  }
}
