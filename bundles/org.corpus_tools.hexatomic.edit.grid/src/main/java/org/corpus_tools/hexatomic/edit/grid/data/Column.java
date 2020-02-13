package org.corpus_tools.hexatomic.edit.grid.data;

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
   * TODO
   * 
   * @param rowIndex
   * @return
   */
  LabelableElement getDataObject(int rowIndex) {
    return rowCells.get(rowIndex);
  }

  /**
   * TODO
   * 
   * @param rowIndex
   * @param anno
   * @return
   * @return
   */
  // boolean setRow(int rowIndex, SAnnotation anno) {
  // boolean isNewValue = rowCells.get(rowIndex) == null;
  // if (isNewValue) {
  // rowCells.add(rowIndex, anno);
  // }
  // return isNewValue;
  // }

  public boolean setRow(int rowIndex, LabelableElement dataObject) {
    LabelableElement existingDataObject = rowCells.get(rowIndex);
    if (existingDataObject != null) {
      return false;
    } else {
      rowCells.put(rowIndex, dataObject);
      return true;
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


}
