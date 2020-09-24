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

package org.corpus_tools.hexatomic.grid.internal.data;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;

/**
 * Represents a column in the grid. A column has a title, and a list of row cells, the values of
 * which can be {@link SStructuredNode}s.
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
  public enum ColumnType {
    // To be used for the column providing token texts
    TOKEN_TEXT,
    // To be used for columns providing token annotations
    TOKEN_ANNOTATION,
    // To be used for columns providing span annotations
    SPAN_ANNOTATION
  }


  private final Map<Integer, SStructuredNode> rowCells = new HashMap<>();
  private BitSet bits = new BitSet();
  private final ColumnType columnType;
  private String columnValue;

  private final Integer columnIndex;

  /**
   * Constructs a new column with a column index.
   * 
   * @param columnType The type of the column
   * @param annotationQName The qualified name of the {@link SAnnotation} for which the column holds
   *        values.
   * @param columnIndex The index of the column in a list of columns holding values for the same
   *        type of column value (e.g., annotation values for annotations with the same qualified
   *        name)
   */
  public Column(ColumnType columnType, String annotationQName, Integer columnIndex) {
    this.columnType = columnType;
    this.columnValue = annotationQName;
    this.columnIndex = columnIndex;
  }

  /**
   * Constructs a new column with the column index set to <code>null</code>.
   * 
   * @param columnType The type of the column
   * @param annotationQName The qualified name of the {@link SAnnotation} for which the column holds
   *        values.
   */
  public Column(ColumnType columnType, String annotationQName) {
    this(columnType, annotationQName, null);
  }

  /**
   * Returns the data object behind the row cell at the specified index.
   * 
   * @param rowIndex The index of the row cell in this column contains the data object to be
   *        returned
   * @return The data object behind the specified row cell in this column
   */
  SStructuredNode getDataObject(int rowIndex) {
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
   * Sets the specified {@link SStructuredNode} data object to a row cell at the specified index.
   * 
   * @param rowIndex The index of the row cell to set the data object to
   * @param node The node data object to set to the cell
   * @throws RuntimeException if the row cell to set the data object to is already set
   */
  void setRow(int rowIndex, SStructuredNode node) throws RuntimeException {
    if (isRowEmpty(rowIndex)) {
      rowCells.put(rowIndex, node);
      bits.set(rowIndex);
    } else if (!isRowEmpty(rowIndex) && node == null) {
      rowCells.remove(rowIndex);
      bits.clear(rowIndex);
    } else {
      throw new RuntimeException("Cannot add " + node + " to " + this.getHeader() + "::" + rowIndex
          + ": cell not empty (" + rowCells.get(rowIndex) + ")!");
    }
  }

  /**
   * Returns the text to display for the specified row cell.
   * 
   * @param rowIndex The index for which to return the display text
   * @return the text to display
   */
  String getDisplayText(int rowIndex) {
    SStructuredNode dataObject = rowCells.get(rowIndex);
    if (getColumnType() == ColumnType.TOKEN_TEXT) {
      if (dataObject instanceof SToken) {
        SToken token = (SToken) dataObject;
        SDocumentGraph graph = token.getGraph();
        return graph.getText(token);
      } else if (dataObject == null) {
        // As is the case when the first cell is used to display a message
        return null;
      } else {
        throw new RuntimeException(
            "A column of type " + ColumnType.TOKEN_TEXT + " can only contain data objects of type "
                + SToken.class.getName() + ". Encountered: " + dataObject.getClass() + ".");
      }
    } else if (dataObject != null) {
      // Column is for providing annotations, hence columnValue should be a valid annotation qName
      SAnnotation annotation = dataObject.getAnnotation(this.columnValue);
      if (annotation == null) {
        return null;
      } else {
        Object value = annotation.getValue();
        return value.toString();
      }
    } else {
      return null;
    }
  }

  /**
   * Returns the column header.
   * 
   * @return the column header
   */
  public String getHeader() {
    if (columnIndex == null) {
      return this.columnValue;
    } else {
      return this.columnValue + " (" + this.columnIndex + ")";
    }
  }

  BitSet getBits() {
    return bits;
  }

  Map<Integer, SStructuredNode> getCells() {
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

  /**
   * Sets the column value.
   * 
   * @param columnValue the columnValue to set
   */
  public void setColumnValue(String columnValue) {
    this.columnValue = columnValue;
  }

  /**
   * Returns the column value for this column. For columns containing token text, this is an
   * arbitrary string. For columns containing annotations, this is the qualified name of the
   * {@link SAnnotation} for which the column holds values.
   * 
   * @return the columnValue
   */
  public String getColumnValue() {
    return columnValue;
  }

  /**
   * Returns the map of row indices to nodes contained in the cells of the row.
   * 
   * @return the rowCells
   */
  public Map<Integer, SStructuredNode> getRowCells() {
    return rowCells;
  }

}
