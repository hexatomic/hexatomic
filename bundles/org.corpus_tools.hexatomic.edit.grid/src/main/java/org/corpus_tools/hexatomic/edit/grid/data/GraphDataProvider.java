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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Enables the use of an {@link SDocumentGraph} as a data source for the {@link NatTable}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GraphDataProvider implements IDataProvider {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(GraphDataProvider.class);


  private STextualDS ds = null;
  private final SDocumentGraph graph;
  private int columnCount = 1;
  private int tokenCount = 1;
  private final List<SToken> dsTokens = new ArrayList<SToken>();
  private final List<SSpan> dsSpans = new ArrayList<SSpan>();
  private List<String> columnHeaderLabels =
      new ArrayList<String>(Arrays.asList(new String[] {"Token"}));



  /**
   * Initializes the graph to grid resolution.
   * 
   * @param graph The graph data source
   */
  public GraphDataProvider(SDocumentGraph graph) {
    this.graph = graph;
  }

  private void resolveGraph() {
    dsTokens.clear();
    dsSpans.clear();
    log.debug("Starting to resolve SDocumentGraph of {} for data source {}.", graph.getDocument(),
        ds);

    // Resolve which tokens should be taken into account based on ds and save to dsTokens.
    for (SRelation<?, ?> inRel : ds.getInRelations()) {
      if (inRel instanceof STextualRelation) {
        // Source can only be token
        dsTokens.add((SToken) inRel.getSource());
      }
    }
    tokenCount = dsTokens.size();

    // FIXME Resolve which spans should be taken into account based on ds.
    for (SSpan span : graph.getSpans()) {
      List<DataSourceSequence> overlappedSequences =
          graph.getOverlappedDataSourceSequence(span, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
      for (DataSourceSequence seq : overlappedSequences) {
        if (seq.getDataSource() == ds)
          dsSpans.add(span);
      }
    }

    // Count token annotations and add to column count
    resolveAnnotations(new ArrayList<SStructuredNode>(dsTokens));

    // Count span annotations and add to column count
    resolveAnnotations(new ArrayList<SStructuredNode>(dsSpans));

    log.debug("Finished resolving SDocumentGraph of {}.", graph.getDocument());
  }

  private void resolveAnnotations(List<SStructuredNode> nodes) {
    Set<String> annotationQNames = new TreeSet<>();
    for (SStructuredNode node : nodes) {
      Set<SAnnotation> annos = node.getAnnotations();
      for (SAnnotation anno : annos) {
        annotationQNames.add(anno.getQName());
      }
    }
    columnCount += annotationQNames.size();
    // Add in alphabetical order the newly added qualified annotation names.
    columnHeaderLabels.addAll(annotationQNames);
    log.debug("Resolved annotations for tokens/spans in {}.", graph.getDocument());

  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    if (ds == null) {
      if (columnIndex == 0 && rowIndex == 0) {
        return "Please select data source!";
      }
    } else {
      if (columnIndex == 0) {
        // Token text
        // FIXME Change to select based on ds!
        return graph.getText(graph.getSortedTokenByText().get(rowIndex));
      }
      String annotationQName = columnHeaderLabels.get(columnIndex);
      SAnnotation anno = null;

      // Get token and see if it has such an annotation
      SToken tok = graph.getSortedTokenByText().get(rowIndex);
      anno = tok.getAnnotation(annotationQName);
      if (anno != null) {
        return anno.getValue_STEXT();
      }

      // TODO No token annotation found, so check span annotations
      for (SSpan span : graph.getSpans()) {
        if (graph.getOverlappedTokens(span).contains(tok)) {
          anno = span.getAnnotation(annotationQName);
        }
      }
      if (anno != null) {
        return anno.getValue_STEXT();
      }

    }
    return null;
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getColumnCount() {
    if (ds == null) {
      return 1;
    }
    return columnCount;
  }

  @Override
  public int getRowCount() {
    return tokenCount;
  }

  /**
   * @param ds the ds to set
   */
  public void setDs(STextualDS ds) {
    this.ds = ds;
    resolveGraph();
  }


}
