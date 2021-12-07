/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider;
import org.corpus_tools.hexatomic.grid.internal.handlers.DisplayAnnotationRenameDialogOnColumnCommandHandler;
import org.corpus_tools.hexatomic.grid.internal.handlers.RenameAnnotationOnColumnCommandHandler;
import org.eclipse.nebula.widgets.nattable.columnRename.event.RenameColumnHeaderEvent;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * A custom implementation of a column header layer which implements changes in the underlying data
 * layer on column header renames instead of using a generic Map like its super class.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class GridColumnHeaderLayer extends ColumnHeaderLayer {

  @Inject
  ErrorService errors;

  /**
   * Constructor delegating to a ColumnHeaderLayer constructor.
   * 
   * @param baseLayer The unique index layer for this layer, typically a DataLayer
   * @param horizontalLayerDependency The layer to link the horizontal dimension to, typically the
   *        body layer
   * @param selectionLayer The SelectionLayer needed to respond to selection events
   */
  public GridColumnHeaderLayer(IUniqueIndexLayer baseLayer, ILayer horizontalLayerDependency,
      SelectionLayer selectionLayer) {
    super(baseLayer, horizontalLayerDependency, selectionLayer);
  }

  /**
   * Triggers a rename of the column in the underlying data model.
   */
  @Override
  public boolean renameColumnPosition(int columnPosition, String newQName) {
    ColumnHeaderDataProvider columnHeaderDataProvider =
        (ColumnHeaderDataProvider) ((DefaultColumnHeaderDataLayer) this.getBaseLayer())
            .getDataProvider();
    int idx = getColumnIndexByPosition(columnPosition);
    boolean renamed = columnHeaderDataProvider.renameColumnPosition(idx, newQName);
    if (renamed) {
      fireLayerEvent(new RenameColumnHeaderEvent(this, columnPosition));
    }
    return renamed;
  }

  @Override
  protected void registerCommandHandlers() {
    registerCommandHandler(new RenameAnnotationOnColumnCommandHandler(this));
    registerCommandHandler(new DisplayAnnotationRenameDialogOnColumnCommandHandler(this));
  }

  /**
   * Returns the first part of the column's display name as split on whitespace + '('. Using
   * whitespaces and brackets in annotation names are discouraged, but the display name may contain
   * them if there are more than one columns for the same qualified annotation name.
   * 
   * @param columnPosition The position of the column for which to retrieve the annotation name
   * @return The qualified annotation name as determined by splitting on ' ('.
   */
  public String getAnnotationQName(int columnPosition) {
    Object dataValue = getDataValueByPosition(columnPosition, 0);
    if (dataValue instanceof String) {
      return ((String) dataValue).split(" \\(")[0];
    } else {
      throw new IllegalArgumentException("Expected column header data value at column position "
          + columnPosition + " to be a string, but got " + dataValue.getClass().getSimpleName());
    }
  }

}
