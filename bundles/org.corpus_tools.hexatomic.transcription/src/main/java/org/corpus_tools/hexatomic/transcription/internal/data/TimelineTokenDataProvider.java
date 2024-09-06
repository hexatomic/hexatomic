/*-
 * #%L
 * [bundle] Timeline Editor
 * %%
 * Copyright (C) 2018 - 2022 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.transcription.internal.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.STimelineRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

@Creatable
public class TimelineTokenDataProvider implements IDataProvider {

  private SDocumentGraph graph;

  @Inject
  private ProjectManager projectManager;

  private final Map<STextualDS, TreeMap<Integer, SToken>> tokenByPosition =
      new LinkedHashMap<STextualDS, TreeMap<Integer, SToken>>();

  /**
   * Set the graph that is used to provide the grid data. This can be a costly operation, because
   * several indexes have to be (re-) created.
   * 
   * @param graph The graph to use.
   */
  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
    // TODO: make this a question to the user
    if (this.graph.getTimeline() == null) {
      this.graph.createTimeline();
    }
    if (this.graph.getTimeline().getData() == null) {
      this.graph.getTimeline().increasePointOfTime();
    }

    updateTokenPosition();
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {

    Optional<SToken> tokenForDs = getTokenForTli(columnIndex, rowIndex).stream().findFirst();
    if (tokenForDs.isPresent()) {
      return tokenForDs.get();
    } else {
      return null;
    }
  }

  /** Find the tokens that are connected to TLI belonging to a data source. */
  private Optional<SToken> getTokenForTli(int columnIndex, int rowIndex) {

    synchronized (tokenByPosition) {
      // Get the datasource for this column and its position index
      STextualDS ds = graph.getTextualDSs().get(columnIndex);
      TreeMap<Integer, SToken> tokenPositionIndex = tokenByPosition.get(ds);
      if (tokenPositionIndex != null) {
        return Optional.ofNullable(tokenPositionIndex.get(rowIndex));
      }
      return Optional.empty();
    }
  }


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void onAnnotationChanged(@UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {

    // Check if this changeset is for the selected document
    if (element instanceof ChangeSet) {
      ChangeSet changes = (ChangeSet) element;
      if (!changes.containsDocument(graph.getDocument().getId())) {
        // This is for a different document, don't update index
        return;
      }
    }

    // Update the index for the positions of the token
    updateTokenPosition();
  }

  private void updateTokenPosition() {

    // Create a new map as background job
    String id = graph.getDocument().getName();

    Job job = Job.create("Calculating timeline index for " + id, monitor -> {
      monitor.beginTask("Calculating timeline index for " + id, 0);

      synchronized (tokenByPosition) {
        tokenByPosition.clear();

        for (STextualDS ds : graph.getTextualDSs()) {
          TreeMap<Integer, SToken> positionInText = new TreeMap<>();
          // Find all tokens that are connected to this data source
          Stream<SToken> tokens =
              ds.getInRelations().stream().filter(STextualRelation.class::isInstance)
                  .map(rel -> ((STextualRelation) rel).getSource());
          // Get all timeline relations for all token
          Stream<STimelineRelation> timeRels = tokens.flatMap(t -> t.getOutRelations().stream()
              .filter(STimelineRelation.class::isInstance).map(rel -> (STimelineRelation) rel));
          // Insert all TLI positions each token covers
          timeRels.forEach(rel -> {
            for (int i = rel.getStart(); i < rel.getEnd(); i++) {
              positionInText.putIfAbsent(i, rel.getSource());
            }
          });

          tokenByPosition.put(ds, positionInText);
        }
      }

      monitor.done();
    });

    job.schedule();
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

    // Get the token associated with this cell
    Optional<SToken> tokenForTli = getTokenForTli(columnIndex, rowIndex);

    if (newValue == null) {
      deleteToken(tokenForTli);
    } else if (newValue instanceof String) {
      String newText = (String) newValue;
      if (tokenForTli.isEmpty()) {
        // No token yet, create a new one
        createToken(columnIndex, Arrays.asList(rowIndex), newText);
      } else {
        // Change the text of the existing token
        SaltHelper.changeTokenText(tokenForTli.get(), newText);
        projectManager.addCheckpoint();
      }
    }
  }

  public void createToken(int columnIndex, Collection<Integer> rowIndex, String newText) {

    if (rowIndex.isEmpty()) {
      return;
    }

    STextualDS ds = graph.getTextualDSs().get(columnIndex);


    TreeSet<Integer> rowIndexSorted = new TreeSet<>(rowIndex);

    int firstRow = rowIndexSorted.first();
    int lastRow = rowIndexSorted.last();

    // get the next non-empty cell
    int rowCount = getRowCount();
    SToken nextToken = null;
    synchronized (tokenByPosition) {
      TreeMap<Integer, SToken> tokenPositionIndex = tokenByPosition.get(ds);
      if (tokenPositionIndex != null) {
        for (int i = lastRow + 1; i < rowCount; i++) {
          nextToken = tokenPositionIndex.get(i);
          if (nextToken != null) {
            break;
          }

        }
      }
    }
    int insertTextAt = ds.getText().length();
    if (nextToken != null) {
      // Determine the position of this token

      @SuppressWarnings("rawtypes")
      List<DataSourceSequence> overlappedSequences =
          graph.getOverlappedDataSourceSequence(nextToken, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
      if (overlappedSequences != null) {
        for (DataSourceSequence<?> seq : overlappedSequences) {
          insertTextAt = Math.min(insertTextAt, seq.getStart().intValue());
        }
      }
    }
    SToken newToken = SaltHelper.insertNewToken(ds, insertTextAt, newText, "");


    // Align new token with the selected TLI
    STimelineRelation timeLineRel = SaltFactory.createSTimelineRelation();
    timeLineRel.setSource(newToken);
    timeLineRel.setTarget(graph.getTimeline());

    timeLineRel.setStart(firstRow);
    timeLineRel.setEnd(lastRow + 1);
    graph.addRelation(timeLineRel);
    if ((lastRow + 1) == graph.getTimeline().getEnd()) {
      // Add an additional TLI at the end
      graph.getTimeline().increasePointOfTime();
    }
    projectManager.addCheckpoint();
  }

  private void deleteToken(Optional<SToken> tokenForTli) {
    if (tokenForTli.isPresent()) {
      SToken tok = tokenForTli.get();

      // The token text should not part of the textual data source anymore
      SaltHelper.changeTokenText(tok, "");
      graph.removeNode(tok);
      projectManager.addCheckpoint();
    }
  }

  @Override
  public int getColumnCount() {
    // Every textual data source is a column
    return graph.getTextualDSs().size();
  }

  @Override
  public int getRowCount() {
    if (graph != null && graph.getTimeline() != null && graph.getTimeline().getStart() != null
        && graph.getTimeline().getEnd() != null) {
      // The number of columns is the number of timeline items
      return graph.getTimeline().getEnd() - graph.getTimeline().getStart();
    } else {
      return 0;
    }
  }


}
