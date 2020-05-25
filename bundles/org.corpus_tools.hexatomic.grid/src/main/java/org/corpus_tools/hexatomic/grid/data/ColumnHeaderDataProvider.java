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

package org.corpus_tools.hexatomic.grid.data;

import java.util.List;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data provider for column headers, which display qualified annotation names.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class ColumnHeaderDataProvider implements IDataProvider {

  private static final Logger log = LoggerFactory.getLogger(ColumnHeaderDataProvider.class);

  private final GraphDataProvider provider;

  public ColumnHeaderDataProvider(GraphDataProvider bodyDataProvider) {
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
    column.setColumnValue(newValue.toString());
  }

  @Override
  public int getColumnCount() {
    return provider.getColumnCount();
  }

  @Override
  public int getRowCount() {
    return 1;
  }

}
