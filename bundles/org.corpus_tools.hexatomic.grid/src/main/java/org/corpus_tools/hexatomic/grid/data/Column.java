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

package org.corpus_tools.hexatomic.grid.data;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.graph.LabelableElement;

/**
 * Represents a column in the grid. A column has a title, and a list of row cells, the values of
 * which can be {@link LabelableElement}s (the first common superclass of {@link SAnnotation} and
 * {@link SToken}).
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class Column {

  /**
   * An enum for distinguishing the type of elements a {@link Column} provides annotations for.
   * 
   * @author Stephan Druskat (mail@sdruskat.net)
   *
   */
  enum ColumnType {
    // To be used for the column providing token texts
    TOKEN_TEXT,
    // To be used for columns providing token annotations
    TOKEN_ANNOTATION,
    // To be used for columns providing span annotations
    SPAN_ANNOTATION
  }


  private final Map<Integer, LabelableElement> rowCells = new HashMap<>();
  private String header = null;
  private BitSet bits = new BitSet();
  private final ColumnType columnType;

  public Column(ColumnType columnType) {
    this.columnType = columnType;
  }

  /**
   * Returns the data object behind the row cell at the specified index.
   * 
   * @param rowIndex The index of the row cell in this column contains the data object to be
   *        returned
   * @return The data object behind the specified row cell in this column
   */
  LabelableElement getDataObject(int rowIndex) {
    return rowCells.get(rowIndex);
  }

  /**
   * Checks if a row cell is empty, or has a data object set to it.
   * 
   * <p>
   * This works by checking whether the corresponding bit at the specified index in the backing
   * {@link BitSet} has not been set.
   * </p>
   * 
   * @param rowIndex The index of the row cell in this column for which the check should be
   *        performed
   * @return whether the row cell at the specified index in this column is unset
   */
  boolean isRowEmpty(int rowIndex) {
    return !bits.get(rowIndex);
  }

  /**
   * Checks if a range of row cells is empty, or has at least one data object set to a row cell
   * within the specified range.
   * 
   * @param inclusiveFrom The inclusive first index of the range of row cells that should be checked
   * @param inclusiveTo The inclusive last index of the range of row cells that should be checked
   * @return whether any of the row cells within the specified range are set
   */
  boolean areRowsEmpty(int inclusiveFrom, int inclusiveTo) {
    // Check whether the cardinality of the BitSet subset containing the bits
    // at the indices specified in the range equals 0 (i.e., no bits in the subset are set to
    // true).
    // As the token index that is passed should be included in the check, we need to add +1 to the
    // to-index, as otherwise it is not checked (BitSet#get API's to is exclusive).
    return bits.get(inclusiveFrom, inclusiveTo + 1).isEmpty();
  }

  /**
   * Sets the specified data object to a row cell at the specified index.
   * 
   * @param rowIndex The index of the row cell to set the data object to
   * @param dataObject The data object to set to the cell
   * @throws RuntimeException if the row cell to set the data object to is already set
   */
  void setRow(int rowIndex, LabelableElement dataObject) throws RuntimeException {
    if (isRowEmpty(rowIndex)) {
      rowCells.put(rowIndex, dataObject);
      bits.set(rowIndex);
    } else {
      throw new RuntimeException("Cannot add " + dataObject + " to " + this.getHeader() + "::"
          + rowIndex + ": cell not empty (" + rowCells.get(rowIndex) + ")!");
    }
  }

  /**
   * Returns the text to display for the specified row cell.
   * 
   * @param rowIndex The index for which to return the display text
   * @return the text to display
   */
  String getDisplayText(int rowIndex) {
    LabelableElement dataObject = rowCells.get(rowIndex);
    if (dataObject instanceof SToken) {
      SToken token = (SToken) dataObject;
      SDocumentGraph graph = token.getGraph();
      return graph.getText(token);
    } else if (dataObject instanceof SAnnotation) {
      SAnnotation annotation = (SAnnotation) dataObject;
      Object value = annotation.getValue();
      if (value == null) {
        return null;
      } else {
        return value.toString();
      }
    }
    return null;
  }

  /**
   * Sets the column header.
   * 
   * @param header The header to set
   */
  void setHeader(String header) {
    this.header = header;
  }

  /**
   * Returns the column header.
   * 
   * @return the column header
   */
  public String getHeader() {
    return header;
  }

  BitSet getBits() {
    return bits;
  }

  Map<Integer, LabelableElement> getCells() {
    return rowCells;
  }

  /**
   * Returns the {@link ColumnType} for this column.
   * 
   * @return the columnType
   */
  public ColumnType getColumnType() {
    return columnType;
  }

}
