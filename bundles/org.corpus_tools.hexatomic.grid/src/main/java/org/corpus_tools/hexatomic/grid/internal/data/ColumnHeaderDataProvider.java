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

package org.corpus_tools.hexatomic.grid.internal.data;

import java.util.List;
import java.util.Map.Entry;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data provider for column headers.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class ColumnHeaderDataProvider implements IDataProvider {

  private static final Logger log = LoggerFactory.getLogger(ColumnHeaderDataProvider.class);

  private final GraphDataProvider provider;
  private final ProjectManager projectManager;

  /**
   * Constructor setting the body data provider. Throws a {@link RuntimeException} if the passed
   * argument is <code>null</code>.
   * 
   * @param bodyDataProvider The body data provider
   * @param projectManager The project manager to notify on changes
   */
  public ColumnHeaderDataProvider(GraphDataProvider bodyDataProvider,
      ProjectManager projectManager) {
    this.projectManager = projectManager;
    if (bodyDataProvider == null) {
      throw new HexatomicRuntimeException(
          "Body data provider in " + this.getClass().getSimpleName() + " must not be null.");
    }
    this.provider = bodyDataProvider;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    List<Column> columns = provider.getColumns();
    if (columnIndex > -1 && columnIndex < columns.size()) {
      return columns.get(columnIndex).getHeader();
    } else {
      return null;
    }
  }

  /**
   * Returns the value of the underlying {@link Column} at the specified index.
   * 
   * @param columnIndex the index of the Column in the underlying data model for which the value
   *        should be set.
   * @return the value of the Column specified via the passed index
   */
  public String getColumnValue(int columnIndex) {
    return provider.getColumns().get(columnIndex).getColumnValue();
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    log.debug("Setting new value '{}' to value of column {}.", newValue, columnIndex);
    Column column = provider.getColumns().get(columnIndex);
    if (column == null) {
      throw new HexatomicRuntimeException(
          "Column at " + columnIndex + " is null. Please report this as a bug.");
    }
    if (newValue == null || (newValue instanceof String && ((String) newValue).isEmpty())) {
      throw new HexatomicRuntimeException("New annotation name must not be null.");
    }
    column.setColumnValue(newValue.toString());
    projectManager.addCheckpoint();
  }

  @Override
  public int getColumnCount() {
    return provider.getColumnCount();
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  /**
   * Renames the column in the data model.
   * 
   * <p>
   * This (1) changes the underlying {@link Column}'s value (from which the header label is
   * computed), and (2) changes the qualified names of all annotations in the underlying
   * {@link Column} cells to the namespace::name combination computed from the <code>newQName</code>
   * parameter.
   * </p>
   * 
   * @param idx The index of the column that is being renamed
   * @param newQName The qualified annotation name to which the column should be renamed
   * @return <code>true</code> if the new qualified column name differs from the current one
   */
  public boolean renameColumnPosition(int idx, String newQName) {
    String name = null;
    try {
      name = SaltUtil.splitQName(newQName).getRight();
    } catch (NullPointerException e) {
      throw new NullPointerException(
          "Annotation name must not be null! Compound qualified name is " + newQName + ".");
    }
    log.debug("New qualified name: {}", newQName);

    // Get the respective Column object from the underlying data model.
    // Note that this is the NatTable's column index as the empty corner "column" is discounted.
    Column column = provider.getColumns().get(idx);
    String oldQName = column.getColumnValue();

    boolean renamed = (!oldQName.equals(newQName));
    if (renamed) {
      // Set the new qName in the column header
      setDataValue(idx, 0, newQName);
      log.debug("Set column value at index {} (old value: '{}') to '{}'.", idx, oldQName, newQName);

      // Rename annotations in the column
      for (Entry<Integer, SStructuredNode> cellEntry : column.getRowCells().entrySet()) {
        SStructuredNode node = cellEntry.getValue();
        SAnnotation annotation = node.getAnnotation(oldQName);
        if (annotation != null) {
          Object value = annotation.getValue();
          log.debug("Renaming annotation {} on node {}.", annotation, node.getName());
          node.removeLabel(oldQName);
          SAnnotation newAnnotation =
              node.createAnnotation(SaltUtil.splitQName(newQName).getLeft(), name, value);
          log.debug("Renamed annotation on node {} from {} to '{}'.", node.getName(),
              annotation.getQName(), newAnnotation.getQName());
          projectManager.addCheckpoint();
        }
      }
    }
    return renamed;

  }

}
