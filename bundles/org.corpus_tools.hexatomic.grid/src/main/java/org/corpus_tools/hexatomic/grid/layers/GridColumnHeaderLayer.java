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

package org.corpus_tools.hexatomic.grid.layers;

import java.util.Map.Entry;
import org.corpus_tools.hexatomic.grid.data.Column;
import org.corpus_tools.hexatomic.grid.data.DataUtil;
import org.corpus_tools.hexatomic.grid.data.GraphDataProvider;
import org.corpus_tools.hexatomic.grid.handlers.DisplayAnnotationRenameDialogCommandHandler;
import org.corpus_tools.hexatomic.grid.handlers.RenameAnnotationCommandHandler;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.columnRename.event.RenameColumnHeaderEvent;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom implementation of a column header layer which implements changes in the underlying data
 * layer on column header renames instead of using a generic Map like its super class.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class GridColumnHeaderLayer extends ColumnHeaderLayer {

  private static final Logger log = LoggerFactory.getLogger(GridColumnHeaderLayer.class);
  private final GraphDataProvider graphDataProvider;

  /**
   * Constructor setting {@link #graphDataProvider} and delegating to a ColumnHeaderLayer
   * constructor.
   * 
   * @param baseLayer The unique index layer for this layer, typically a DataLayer
   * @param horizontalLayerDependency The layer to link the horizontal dimension to, typically the
   *        body layer
   * @param selectionLayer The SelectionLayer needed to respond to selection events
   * @param graphDataProvider The body data provider for the NatTable
   */
  public GridColumnHeaderLayer(IUniqueIndexLayer baseLayer, ILayer horizontalLayerDependency,
      SelectionLayer selectionLayer, GraphDataProvider graphDataProvider) {
    super(baseLayer, horizontalLayerDependency, selectionLayer);
    this.graphDataProvider = graphDataProvider;
  }

  /**
   * Renames the column in the NatTable.
   * 
   * <p>
   * This (1) changes the underlying {@link Column}'s value (from which the header label is
   * computed), and (2) changes the qualified names of all annotations in the underlying
   * {@link Column} cells to the namespace::name combination computed from the
   * <code>customColumnName</code> parameter.
   * </p>
   */
  @Override
  public boolean renameColumnPosition(int columnPosition, String newQName) {
    String name = DataUtil.splitNameFromQNameString(newQName);
    if (name == null || name.isEmpty()) {
      throw new RuntimeException(
          "Annotation name is null! Compund qualified name is " + newQName + ".");
    }
    log.debug("New qualified name: {}", newQName);
    IDataProvider columnHeaderDataProvider =
        ((DefaultColumnHeaderDataLayer) this.getBaseLayer()).getDataProvider();
    int idx = getColumnIndexByPosition(columnPosition);

    // Get the respective Column object from the underlying data model.
    // Note that this is the NatTable's column index as the empty corner "column" is discounted.
    Column column = graphDataProvider.getColumns().get(idx);
    String oldQName = column.getColumnValue();

    boolean renamed = (!oldQName.equals(newQName));
    if (renamed) {
      // Set the new qName in the column header
      columnHeaderDataProvider.setDataValue(idx, 0, newQName);
      log.debug("Set column value at index {} (old value: '{}') to '{}'.", idx, oldQName, newQName);

      // Rename annotations in the column
      for (Entry<Integer, SStructuredNode> cellEntry : column.getRowCells().entrySet()) {
        SStructuredNode node = cellEntry.getValue();
        SAnnotation annotation = node.getAnnotation(oldQName);
        if (annotation != null) {
          Object value = annotation.getValue();
          log.debug("Renaming annotation '{}' on node '{}'.", annotation.toString(),
              node.getName());
          node.removeLabel(oldQName);
          SAnnotation newAnnotation =
              node.createAnnotation(DataUtil.splitNamespaceFromQNameString(newQName), name, value);
          // Should ideally work like this:
          // annotation.setNamespace(namespace);
          // annotation.setName(name);
          log.debug("Renamed annotation on node {} from {} to '{}'.", node.getName(),
              annotation.getQName(), newAnnotation.getQName());
        }
        fireLayerEvent(new RenameColumnHeaderEvent(this, columnPosition));
      }
    }
    return renamed;
  }

  @Override
  protected void registerCommandHandlers() {
    registerCommandHandler(new RenameAnnotationCommandHandler(this));
    registerCommandHandler(new DisplayAnnotationRenameDialogCommandHandler(this));
  }

}
