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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.data.Column.ColumnType;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.LabelableElement;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Enables the use of an {@link SDocumentGraph} as a data source for the {@link NatTable}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
@Creatable
public class GraphDataProvider implements IDataProvider {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(GraphDataProvider.class);

  public static final String TOKEN_TEXT_COLUMN_LABEL = "Token";

  private STextualDS dataSource = null;
  private SDocumentGraph graph;

  private final List<SToken> orderedDsTokens = new ArrayList<SToken>();
  private final LinkedHashMap<SSpan, Set<SToken>> spanTokenMap = new LinkedHashMap<>();

  // Alphabetically ordered maps of column titles to columns for the two annotated types
  private final TreeMap<String, Column> tokenColumns = new TreeMap<>();
  private final TreeMap<String, Column> spanColumns = new TreeMap<>();
  // To be compiled from the two tree sets, tokens first, then spans
  private final List<Column> columns = new ArrayList<>();

  @Inject
  ErrorService errors;

  private void resolveGraph() {
    // Reset data
    orderedDsTokens.clear();
    tokenColumns.clear();
    spanColumns.clear();
    columns.clear();
    spanTokenMap.clear();


    log.debug("Starting to resolve SDocumentGraph of {} for data source {}.", graph.getDocument(),
        dataSource);

    // Only consider tokens that are based on the selected data source.
    List<SToken> unorderedTokens = new ArrayList<SToken>();
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
        reportSetRow(e);
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
      set = new HashSet<SToken>();
      set.add(token);
    }
    spanTokenMap.put(span, set);
  }

  private void resolveSpanAnnotations(List<SToken> orderedTokens) {
    for (SSpan span : spanTokenMap.keySet()) {
      Set<SToken> overlappedTokens = spanTokenMap.get(span);
      List<Integer> tokenIndices = new ArrayList<>();
      // Build token index list, i.e., row indices covered by this span
      for (SToken token : overlappedTokens) {
        tokenIndices.add(orderedTokens.indexOf(token));
      }
      Collections.sort(tokenIndices);
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
        setMultipleRows(tokenIndices, column, span);
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
      setMultipleRows(tokenIndices, column, span);
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
          setSingleRow(column, orderedTokens.indexOf(token), token);
        } else {
          column = new Column(ColumnType.TOKEN_ANNOTATION, anno.getQName());
          // No overlap possible, so we can simply set the header to the qName
          setSingleRow(column, orderedTokens.indexOf(token), token);
          tokenColumns.put(anno.getQName(), column);
        }
      }
    }
    // Add in alphabetical order the newly added qualified annotation names.
    log.debug("Resolved annotations for tokens/spans in {}.", graph.getDocument());

  }

  private void setMultipleRows(List<Integer> tokenIndices, Column column,
      SStructuredNode annotationNode) {
    for (Integer idx : tokenIndices) {
      setSingleRow(column, idx, annotationNode);
    }
  }

  private void setSingleRow(Column column, Integer idx, SStructuredNode annotationNode) {
    try {
      column.setRow(idx, annotationNode);
    } catch (RuntimeException e) {
      reportSetRow(e);
    }
  }

  private void reportSetRow(RuntimeException e) {
    errors.handleException(
        "Encountered a set cell that should be empty. This is a bug, please create a new issue at https://github.com/hexatomic/hexatomic.",
        e, this.getClass());
  }

  /**
   * Returns the value to display for the given cell, as identified by column and row index.
   * 
   * <p>
   * This is either an annotation value; or a token text; or, if no {@link STextualDS} has been
   * selected, a message to select a text; or, if the selected {@link STextualDS} contains no
   * tokens, a message notifying the user of this.
   * </p>
   */
  @Override
  public String getDataValue(int columnIndex, int rowIndex) {
    if (dataSource == null) {
      if (columnIndex == 0 && rowIndex == 0) {
        return "Please select data source!";
      }
    } else {
      if (orderedDsTokens.size() == 0 && columnIndex == 0 && rowIndex == 0) {
        return "Data source contains no tokens!";
      } else {
        Column column = null;
        try {
          column = columns.get(columnIndex);
        } catch (IndexOutOfBoundsException e) {
          errors.handleException(e.getMessage(), e, GraphDataProvider.class);
          return null;
        }
        return column.getDisplayText(rowIndex);
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
    LabelableElement dataObject = column.getDataObject(rowIndex);
    if (column.getColumnType() == ColumnType.TOKEN_TEXT) {
      log.debug("Action not implemented: Set token text");
    } else if (dataObject instanceof SStructuredNode) {
      SStructuredNode node = (SStructuredNode) dataObject;
      SAnnotation anno = node.getAnnotation(column.getColumnValue());
      if (anno == null) {
        throw new RuntimeException(
            "Failed to retrieve annotation to set the value for on node " + node.toString() + ".");
      } else {
        log.debug("Setting value on {} ({}, '{}') to '{}'.", dataObject.getClass().getSimpleName(),
            dataObject.hashCode(), dataObject, newValue);
        anno.setValue(newValue);
      }
    } else if (dataObject == null) {
      // Check which column type we're looking at
      ColumnType columnType = column.getColumnType();
      if (columnType == ColumnType.SPAN_ANNOTATION) {
        // Create new span
        log.debug("Creating new span?");
        // TODO HIER WEITER
      } else if (columnType == ColumnType.TOKEN_ANNOTATION) {
        // Handle token annotation
        log.debug("Creating new token annotation?");
      } else {
        log.debug("Action not implemented: Set token text");
      }
    } else {
      log.debug("Action not implemented: Set text on '{}' to '{}'", dataObject.toString());
    }

  }

  @Override
  public int getColumnCount() {
    if (dataSource == null) {
      return 1;
    } else if (orderedDsTokens.size() == 0) {
      return 1;
    }
    return columns.size();
  }

  @Override
  public int getRowCount() {
    if (dataSource == null) {
      return 1;
    } else if (orderedDsTokens.size() == 0) {
      return 1;
    }
    return orderedDsTokens.size();
  }

  /**
   * Sets the data source field.
   * 
   * @param ds the ds to set
   */
  public void setDsAndResolveGraph(STextualDS ds) {
    log.debug("Setting data source {}.", ds);
    this.dataSource = ds;
    resolveGraph();
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

}
