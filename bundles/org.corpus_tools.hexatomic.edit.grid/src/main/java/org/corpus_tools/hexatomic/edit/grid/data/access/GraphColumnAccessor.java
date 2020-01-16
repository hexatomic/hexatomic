/*-
 * #%L
 * org.corpus_tools.hexatomic.edit.grid
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

package org.corpus_tools.hexatomic.edit.grid.data.access;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;

/**
 * An accessor for the data in an {@link SDocumentGraph} that should be displayed in the columns of
 * the annotation grid.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GraphColumnAccessor<G> implements IColumnAccessor<G> {

  private int columnCount;

  /**
   * Sets up the accessor to be used for a new instance of the grid editor and does some initial
   * processing.
   * 
   * @param graph The graph data source
   */
  public GraphColumnAccessor(SDocumentGraph graph) {
    this.columnCount = graph.getAnnotations().size();
    log.debug("Number of annotations: {}", this.columnCount);
  }

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(GraphColumnAccessor.class);

  /**
   * Retrieves annotation values for the respective column index from a virtual grid containing the
   * annotation data for nodes in the {@link SDocumentGraph}.
   */
  @Override
  public Object getDataValue(G rowObject, int columnIndex) {
    log.debug("G-type: {}", rowObject.getClass().getSimpleName());
    return "Challo";
  }

  @Override
  public void setDataValue(G rowObject, int columnIndex, Object newValue) {
    // TODO Auto-generated method stub
  }

  /**
   * Returns the number of columns to display in the grid.
   * 
   * As this method is called every time the grid is laid out, it would be expensive to calculate
   * the value dynamically each time. Therefore FIXME something needs to track the number of
   * columns.
   */
  @Override
  public int getColumnCount() {
    return 1; // columnCount;
  }

}
