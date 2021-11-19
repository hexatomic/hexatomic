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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.internal.ui.UnrenamedAnnotationsDialog;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.exceptions.SaltInsertionException;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Enables the use of an {@link SDocumentGraph} as a data source for the {@link NatTable}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
@Creatable
public class GraphDataProvider implements IDataProvider {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(GraphDataProvider.class);

  public static final String TOKEN_TEXT_COLUMN_LABEL = "Token";

  private STextualDS dataSource = null;
  private SDocumentGraph graph;

  private final List<SToken> orderedDsTokens = new ArrayList<>();
  private final LinkedHashMap<SSpan, Set<SToken>> spanTokenMap = new LinkedHashMap<>();

  // Alphabetically ordered maps of column titles to columns for the two annotated types
  private final TreeMap<String, Column> tokenColumns = new TreeMap<>();
  private final TreeMap<String, Column> spanColumns = new TreeMap<>();
  // To be compiled from the two tree sets, tokens first, then spans
  private final List<Column> columns = new ArrayList<>();

  @Inject
  ErrorService errors;

  @Inject
  ProjectManager projectManager;

  /**
   * Sets the data source field.
   * 
   * @param ds the ds to set
   */
  public void resolveDataSource(STextualDS ds) {
    log.debug("Setting data source {}.", ds);
    this.dataSource = ds;
    resolveGraph();
  }

  /**
   * Rebuilds the column model completely from scratch by resolving the {@link SDocumentGraph} from
   * scratch.
   */
  private void resolveGraph() {
    System.err.println("FULL RESOLVE");
    // Reset data
    orderedDsTokens.clear();
    tokenColumns.clear();
    spanColumns.clear();
    columns.clear();
    spanTokenMap.clear();

    log.debug("Starting to resolve SDocumentGraph of {} for data source {}.", graph.getDocument(),
        dataSource);

    // Only consider tokens that are based on the selected data source.
    List<SToken> unorderedTokens = new ArrayList<>();
    for (SRelation<?, ?> inRel : dataSource.getInRelations()) {
      if (inRel instanceof STextualRelation) {
        // Source of STextualRelation can only be token
        unorderedTokens.add((SToken) inRel.getSource());
      }
    }

    orderedDsTokens.addAll(graph.getSortedTokenByText(unorderedTokens));

    // Only consider spans spanning tokens in the selected data source.
    for (SToken token : orderedDsTokens) {
      for (SRelation<?, ?> inRel : token.getInRelations()) {
        if (inRel instanceof SSpanningRelation) {
          mapTokenToSpan(((SSpanningRelation) inRel).getSource(), token);
        }
      }
    }

    // Add a column for the token text as the first column
    Column tokenColumn = new Column(ColumnType.TOKEN_TEXT, TOKEN_TEXT_COLUMN_LABEL);
    for (int i = 0; i < orderedDsTokens.size(); i++) {
      SToken token = orderedDsTokens.get(i);
      try {
        tokenColumn.setRow(i, token);
      } catch (RuntimeException e) {
        reportSetCell(e);
      }
    }
    columns.add(tokenColumn);

    resolveTokenAnnotations(orderedDsTokens);
    resolveSpanAnnotations(orderedDsTokens);

    // Complete the list of columns
    // Order is kept correctly, because TreeMap.values()' iterator returns the values in
    // ascending order of the corresponding keys, i.e., the collection of values is sorted.
    columns.addAll(tokenColumns.values());
    columns.addAll(spanColumns.values());
    log.debug("Finished resolving SDocumentGraph of {}.", graph.getDocument());
  }

  private void mapTokenToSpan(SSpan span, SToken token) {
    Set<SToken> set = spanTokenMap.get(span);
    if (set != null) {
      set.add(token);
    } else {
      set = new HashSet<>();
      set.add(token);
    }
    spanTokenMap.put(span, set);
  }

  private void resolveSpanAnnotations(List<SToken> orderedTokens) {
    for (Entry<SSpan, Set<SToken>> entry : spanTokenMap.entrySet()) {
      Set<SToken> overlappedTokens = entry.getValue();
      List<Integer> tokenIndices = new ArrayList<>();
      // Build token index list, i.e., row indices covered by this span
      for (SToken token : overlappedTokens) {
        tokenIndices.add(orderedTokens.indexOf(token));
      }
      Collections.sort(tokenIndices);
      SSpan span = entry.getKey();
      for (SAnnotation annotation : span.getAnnotations()) {
        resolveAnnotationRecursively(tokenIndices, span, annotation, 1);
      }
    }
  }

  /**
   * Resolves annotations on spans recursively, so that overlapping spans with the same annotation
   * namespace and name are separated over columns. This is because while these overlaps may exist
   * in the data, they cannot be displayed within a single column.
   * 
   * <p>
   * This method adds annotation values to columns, and tracks existing columns. If a column for a
   * combination of annotation namespace and name does not exist yet, it creates a new
   * {@link Column} and adds the annotation values to the row cells identified by the token indices.
   * If a column of the combination does exist, it checks whether all row cells that the annotation
   * value would be written to is empty. If this is the case, it adds the annotation values to the
   * row cells identified by the token indices.
   * </p>
   * 
   * <p>
   * If, however, a value already exists in one of the cells that the current value would be written
   * to, the column index integer is set up by 1 and the method recurses on itself with the new
   * column index as parameter. As in the following run, no column with the specified column index
   * can exist, a new column will be created.
   * </p>
   * 
   * <p>
   * The column index is also passed to the constructor of {@link Column} iff it is > 1.
   * </p>
   * 
   * @param tokenIndices The indices of the tokens spanned over by the span carrying the annotation
   * @param span The span to operate on
   * @param annotation The annotation to resolve
   * @param spanColumnIndex An integer index tracking how many columns for the same annotation
   *        namespace-name combination already exist
   */
  private void resolveAnnotationRecursively(List<Integer> tokenIndices, SSpan span,
      SAnnotation annotation, Integer spanColumnIndex) {
    String columnName = null;
    String annotationQName = annotation.getQName();
    if (spanColumnIndex == 1) {
      columnName = annotationQName;
    } else {
      columnName = annotationQName + spanColumnIndex;
    }
    Column column = spanColumns.get(columnName);
    if (column != null) {
      // Try to add, otherwise iterate and re-run
      if (column.areRowsEmpty(tokenIndices.get(0), tokenIndices.get(tokenIndices.size() - 1))) {
        setMultipleCells(tokenIndices, column, span);
      } else {
        // Bump counter and re-run
        spanColumnIndex = spanColumnIndex + 1;
        resolveAnnotationRecursively(tokenIndices, span, annotation, spanColumnIndex);
      }
    } else {
      if (spanColumnIndex == 1) {
        column = new Column(ColumnType.SPAN_ANNOTATION, annotationQName);
      } else {
        column = new Column(ColumnType.SPAN_ANNOTATION, annotationQName, spanColumnIndex);
      }
      setMultipleCells(tokenIndices, column, span);
      spanColumns.put(columnName, column);
    }
  }

  private void resolveTokenAnnotations(List<SToken> orderedTokens) {
    for (SStructuredNode token : orderedTokens) {
      Set<SAnnotation> annos = token.getAnnotations();
      for (SAnnotation anno : annos) {
        // Check if we already have a column with that key
        Column column = tokenColumns.get(anno.getQName());
        if (column != null) {
          // There can be no two annotations of the same qualified name for a single token, so no
          // need to check for multiple rows as we do have to for spans.
          setSingleCell(column, orderedTokens.indexOf(token), token);
        } else {
          column = new Column(ColumnType.TOKEN_ANNOTATION, anno.getQName());
          // No overlap possible, so we can simply set the header to the qName
          setSingleCell(column, orderedTokens.indexOf(token), token);
          tokenColumns.put(anno.getQName(), column);
        }
      }
    }
    // Add in alphabetical order the newly added qualified annotation names.
    log.debug("Resolved annotations for tokens/spans in {}.", graph.getDocument());

  }

  /**
   * Returns the value to display for the given cell, as identified by column and row index.
   * 
   * <p>
   * This is either an annotation value; or a token text.
   * </p>
   */
  @Override
  public SStructuredNode getDataValue(int columnIndex, int rowIndex) {
    if (dataSource == null) {
      if (columnIndex == 0 && rowIndex == 0) {
        return null;
      }
    } else {
      if (orderedDsTokens.isEmpty() && columnIndex == 0 && rowIndex == 0) {
        return null;
      } else {
        Column column = null;
        try {
          column = columns.get(columnIndex);
        } catch (IndexOutOfBoundsException e) {
          errors.handleException(e.getMessage(), e, GraphDataProvider.class);
          return null;
        }
        return column.getDataObject(rowIndex);
      }
    }
    return null;
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    Column column = null;
    try {
      column = columns.get(columnIndex);
    } catch (IndexOutOfBoundsException e) {
      errors.handleException(e.getMessage(), e, GraphDataProvider.class);
      return;
    }
    ColumnType columnType = column.getColumnType();
    SStructuredNode node = column.getDataObject(rowIndex);
    String annotationQName = column.getColumnValue();

    // If new value is empty, remove annotation
    if (newValue == null || newValue.equals("")) {
      removeAnnotation(annotationQName, node, column, rowIndex);
    } else { // Value is not empty or null
      if (columnType == ColumnType.TOKEN_TEXT) {
        log.debug("Action not implemented: Set token text");
      } else if (node instanceof SStructuredNode) {
        changeAnnotationValue(newValue, column, node);
      } else if (node == null) { // Span to take annotation doesn't exist yet
        SToken tokenAtIndex = orderedDsTokens.get(rowIndex);
        if (columnType == ColumnType.SPAN_ANNOTATION) {
          // Create new span
          SSpan span = graph.createSpan(tokenAtIndex);
          column.setRow(rowIndex, span);
          log.debug("Created new span {}, spanning token {}.", span, tokenAtIndex);
          createAnnotation(newValue, columnIndex, span);
        } else if (columnType == ColumnType.TOKEN_ANNOTATION) {
          // While the token to attach the annotation is not null in the graph, the node backing up
          // the cell in the column is, so we need to set it first.
          column.setRow(rowIndex, tokenAtIndex);
          // Create new token annotation
          createAnnotation(newValue, columnIndex, tokenAtIndex);
        } else {
          log.debug("Action not implemented: Create token.");
        }
      } else {
        log.debug("Action not implemented: Set text on '{}' to '{}'.", node, newValue);
      }
    }
    projectManager.addCheckpoint();

  }

  private void setMultipleCells(List<Integer> tokenIndices, Column column,
      SStructuredNode annotationNode) {
    for (Integer idx : tokenIndices) {
      setSingleCell(column, idx, annotationNode);
    }
  }

  private void setSingleCell(Column column, Integer idx, SStructuredNode annotationNode) {
    try {
      column.setRow(idx, annotationNode);
    } catch (RuntimeException e) {
      reportSetCell(e);
    }
  }

  private void reportSetCell(RuntimeException e) {
    errors.handleException(
        "Encountered a set cell that should be empty. This is a bug, please create a new issue at https://github.com/hexatomic/hexatomic.",
        e, this.getClass());
  }

  private void removeAnnotation(String annotationQName, SStructuredNode node, Column column,
      int rowIndex) {
    // Remove annotation from Salt model
    if (node != null) {
      node.removeLabel(column.getColumnValue());
      log.debug("Removed annotation {} from node {}.", annotationQName, node);
    }
    // Remove annotation from all column cells
    if (node instanceof SToken) {
      // Annotation can only span a single token, i.e., a single row
      column.setRow(rowIndex, null);
    } else if (node instanceof SSpan) {
      List<SToken> overlappedTokens = graph.getOverlappedTokens(node);
      for (SToken token : overlappedTokens) {
        column.setRow(orderedDsTokens.indexOf(token), null);
      }
      // If, now, the span has no annotations, remove it
      if (node.getAnnotations().isEmpty()) {
        graph.removeNode(node);
        log.debug("Removed empty span {} from graph {}.", node, graph);
      }
    }
  }

  private void createAnnotation(Object newValue, int columnIndex, SStructuredNode node) {
    SAnnotation annotation = node.createAnnotation(getAnnotationNamespace(columnIndex),
        getAnnotationName(columnIndex), newValue);
    log.debug("Created new annotation {} with value '{}' on {} ({}, '{}').", annotation.getQName(),
        newValue, node.getClass().getSimpleName(), node.hashCode(), node);
  }

  private void changeAnnotationValue(Object newValue, Column column, SStructuredNode dataObject) {
    SStructuredNode node = dataObject;
    SAnnotation anno = node.getAnnotation(column.getColumnValue());
    if (anno == null) {
      throw new HexatomicRuntimeException(
          "Failed to retrieve annotation to set the value for on node " + node.toString() + ".");
    } else {
      log.debug("Setting value on annotation {} on {} ({}, '{}') to '{}'.", anno.getQName(),
          dataObject.getClass().getSimpleName(), dataObject.hashCode(), dataObject, newValue);
      anno.setValue(newValue);
    }
  }

  /**
   * Inserts a new column into the column model.
   */
  public void insertColumn() {

  }

  /**
   * Renames annotation on a set of given cells in bulk. The cells are identified by their
   * {@link PositionCoordinate}s, which are provided as a map of cell positions to sets of row
   * positions.
   * 
   * @param cellMapByColumn a map of cells from column positions to sets of row positions.
   * @param newQName the new qualified annotation name for the annotations to be renamed
   */
  public void bulkRenameAnnotations(Map<Integer, Set<Integer>> cellMapByColumn, String newQName) {
    Set<SStructuredNode> touchedNodes = new HashSet<>();
    Set<SStructuredNode> unchangedNodes = new HashSet<>();
    Pair<String, String> namespaceNamePair = SaltUtil.splitQName(newQName);
    final String namespace = namespaceNamePair.getLeft();
    final String name = namespaceNamePair.getRight();
    // Run the rename for all cells by column
    for (Entry<Integer, Set<Integer>> columnCoordinates : cellMapByColumn.entrySet()) {
      Integer columnPosition = columnCoordinates.getKey();
      Column column = getColumns().get(columnPosition);
      String currentQName = column.getColumnValue();
      if (currentQName.equals(newQName)) {
        // Simply ignore this column, as the name is the same
        log.debug("Ignoring rename operation on column {}, "
            + "as the current and new qualified annotation names are the same: "
            + "[current: {}]..[new: {}].", columnPosition, currentQName, newQName);
        continue;
      }
      for (Integer rowPosition : columnCoordinates.getValue()) {
        SStructuredNode node = column.getDataObject(rowPosition);
        if (node != null && !touchedNodes.contains(node)) {
          // Only proceed if the node is not null and if the node hasn't been touched yet.
          SAnnotation currentAnnotation = node.getAnnotation(currentQName);
          // Check if target annotation already exists
          if (node.getAnnotation(newQName) != null) {
            log.debug(
                "The following node already has an annotation with the qualified name '{}'. "
                    + "Ignoring it to avoid throwing {}:\n{}",
                newQName, SaltInsertionException.class.getSimpleName(), node);
            unchangedNodes.add(node);
            continue;
          }
          Object annotationValue = currentAnnotation.getValue();
          node.removeLabel(currentQName);
          node.createAnnotation(namespace, name, annotationValue);
          // Remember that the node has already been touched.
          touchedNodes.add(node);
        }
      }
    }
    if (!unchangedNodes.isEmpty()) {
      UnrenamedAnnotationsDialog.open(namespace, name, unchangedNodes);
    }
    projectManager.addCheckpoint();

  }

  /**
   * Creates a new {@link SSpan} over the tokens in the first {@link Column} in {@link #columns},
   * that are defined by the passed {@link PositionCoordinate#getColumnPosition()}s. Then, adds an
   * {@link SAnnotation} to the new span with an empty value.
   * 
   * @param selectedCoordinates a set of the {@link PositionCoordinate}s of the currently selected
   *        cells
   */
  public void createEmptyAnnotationSpan(Set<PositionCoordinate> selectedCoordinates) {
    // Get tokens
    List<Integer> selectedRows = selectedCoordinates.parallelStream()
        .map(PositionCoordinate::getRowPosition).collect(Collectors.toList());
    List<SStructuredNode> potentialTokens = selectedRows.parallelStream()
        .map(i -> getColumns().get(0).getDataObject(i)).collect(Collectors.toList());
    // Check that all potentialTokens are in fact tokens
    List<SToken> tokens = potentialTokens.parallelStream().map(n -> {
      if (n instanceof SToken) {
        return (SToken) n;
      } else {
        throw new HexatomicRuntimeException(
            "Expected an object of type " + SToken.class.getSimpleName()
                + " in the first column of the grid, but found a " + n.getClass().getSimpleName());
      }
    }).collect(Collectors.toList());
    SSpan span = graph.createSpan(tokens);
    log.debug("Created new span {}.", span);
    int columnIndex = selectedCoordinates.iterator().next().getColumnPosition();
    Column column = getColumns().get(columnIndex);

    for (int rowIndex : selectedRows) {
      column.setRow(rowIndex, span);
    }
    createAnnotation(null, columnIndex, span);
    log.debug("Annotated new span with empty value.");
  }


  /**
   * Set the {@link SDocumentGraph} that the data provider operates on.
   * 
   * @param graph the graph to set
   */
  public final void setGraph(SDocumentGraph graph) {
    this.graph = graph;
  }

  /**
   * Returns the list of {@link Column}s for the current model.
   * 
   * @return the columns
   */
  public List<Column> getColumns() {
    return columns;
  }

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public int getRowCount() {
    return orderedDsTokens.size();
  }

  private String getAnnotationNamespace(int columnIndex) {
    String[] splitColumnValue = getColumns().get(columnIndex).getColumnValue().split("::");
    if (splitColumnValue.length == 2) {
      return splitColumnValue[0];
    } else {
      return null;
    }
  }

  private String getAnnotationName(int columnIndex) {
    String[] splitColumnValue = getColumns().get(columnIndex).getColumnValue().split("::");
    if (splitColumnValue.length == 2) {
      return splitColumnValue[1];
    } else {
      return splitColumnValue[0];
    }
  }
}
