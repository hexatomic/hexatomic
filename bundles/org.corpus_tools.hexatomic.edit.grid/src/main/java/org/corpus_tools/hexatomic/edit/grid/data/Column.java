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

package org.corpus_tools.hexatomic.edit.grid.data;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.graph.LabelableElement;

/**
 * Represents a column in the grid.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class Column {

  private final Map<Integer, LabelableElement> rowCells = new HashMap<>();
  private String title = null;
  private BitSet bits = new BitSet();

  // /**
  // * Package-protected constructor setting the row cells value.
  // *
  // * @param rowIndex The index at which the annotation should be added to the list of row cells
  // * @param anno The annotation to add to the list of row cells
  // */
  // Column(int rowIndex, SAnnotation anno) {
  // rowCells.add(rowIndex, anno);
  // }
  //
  // Column() {}

  /**
   * TODO.
   * 
   * @param rowIndex TODO
   * @return TODO
   */
  LabelableElement getDataObject(int rowIndex) {
    return rowCells.get(rowIndex);
  }

  // /**
  // * TODO.
  // *
  // * @param rowIndex
  // * @param anno
  // * @return
  // * @return
  // * @return
  // * @throws CellExistsException
  // */
  // boolean setRow(int rowIndex, SAnnotation anno) {
  // boolean isNewValue = rowCells.get(rowIndex) == null;
  // if (isNewValue) {
  // rowCells.add(rowIndex, anno);
  // }
  // return isNewValue;
  // }

  /**
   * TODO .
   * 
   * @param rowIndex TODO
   * @return TODO
   */
  boolean isRowEmpty(int rowIndex) {
    return !bits.get(rowIndex);
  }

  /**
   * @param from
   * @param to
   * @return
   */
  boolean areRowsEmpty(int from, int to) {
    // As the token index that is passed should be included in the check, we need to add +1 to the
    // to-index, as otherwise it is not checked (API's to is exclusive). Cardinality is 'number of
    // true bits in bit-subset'.
    return bits.get(from, to + 1).cardinality() == 0;
  }

  /**
   * TODO .
   * 
   * @param rowIndex TODO
   * @param dataObject TODO
   * @throws CellExistsException TODO
   */
  void setRow(int rowIndex, LabelableElement dataObject) throws RuntimeException {
    if (isRowEmpty(rowIndex)) {
      rowCells.put(rowIndex, dataObject);
      bits.set(rowIndex);
    } else {
      throw new RuntimeException("Cannot add " + dataObject + " to " + this.getTitle() + "::"
          + rowIndex + ": cell not empty (" + rowCells.get(rowIndex) + ")!");
    }
  }

  String getDisplayText(int rowIndex) {
    LabelableElement dataObject = rowCells.get(rowIndex);
    if (dataObject instanceof SToken) {
      SToken token = (SToken) dataObject;
      SDocumentGraph graph = token.getGraph();
      return graph.getText(token);
    } else if (dataObject instanceof SAnnotation) {
      SAnnotation annotation = (SAnnotation) dataObject;
      return annotation.getValue_STEXT();
    }
    return null;
  }

  void setTitle(String title) {
    this.title = title;
  }

  // /**
  // * Package-protected constructor setting the row cells value.
  // *
  // * @param rowIndex The index at which the annotation should be added to the list of row cells
  // * @param anno The annotation to add to the list of row cells
  // */
  // Column(int rowIndex, SAnnotation anno) {
  // rowCells.add(rowIndex, anno);
  // }
  //
  // Column() {}

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(title == null ? "null" : title);
    sb.append(":\n");
    for (int i = 0; i < rowCells.size(); i++) {
      String t = getDisplayText(i) == null ? "null" : getDisplayText(i);
      Object o = getDataObject(i) == null ? "null" : getDataObject(i).getClass().getSimpleName();
      sb.append(t + " (" + o + ").");
    }
    return sb.toString();
  }


}
