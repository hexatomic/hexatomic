/*-
 * #%L
 * org.corpus_tools.hexatomic.core
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

package org.corpus_tools.hexatomic.core;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.events.salt.NotifyingElement;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.util.DataSourceSequence;

/**
 * A utility class to handle Salt objects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class SaltHelper {

  /**
   * A namespace use for (processing) annotations used by Hexatomic.
   */
  public static final String HEXATOMIC_NAMESPACE = "hexatomic";

  /**
   * The name for the processing annotation that contains the original location of an imported or
   * exported corpus.
   */
  public static final String CORPUS_ORIGIN = "corpus-origin";


  private SaltHelper() {
    // Should not be initiated
  }

  /**
   * Delegated elements might link to an actual typed element (the delegate). This function returns
   * the actual typed element or the original element itself. If there is a chain of links, the last
   * element in this chain will be returned.
   * 
   * @param element The element to get the last typed object element for
   * @return The actual typed object
   */
  public static Object resolveDelegation(Object element) {
    if (element == null) {
      return null;
    }
    Object currentElement = element;
    while (currentElement instanceof NotifyingElement<?>
        && ((NotifyingElement<?>) currentElement).getTypedDelegation() != null) {
      currentElement = ((NotifyingElement<?>) currentElement).getTypedDelegation();
    }
    return currentElement;
  }


  /**
   * Gets the graph the object is transitively attached to.
   * 
   * @param object The object to get the graph for.
   * @param graphType A {@link Class} instance of the graph type which is requested, e.g. a
   *        {@link SDocumentGraph}.
   * @return The graph or empty value if not found or the graph has the wrong type
   */
  public static <G extends SGraph> Optional<G> getGraphForObject(Object object,
      Class<G> graphType) {

    if (graphType.isInstance(object)) {
      // Basic case: the object is already a graph
      return Optional.of(graphType.cast(object));
    } else if (object instanceof Node && graphType.isInstance(((Node) object).getGraph())) {
      // Directly get the graph the node is attached to (or not if null)
      return Optional.ofNullable(graphType.cast(((Node) object).getGraph()));
    } else if (object instanceof Relation<?, ?>
        && graphType.isInstance(((Relation<?, ?>) object).getGraph())) {
      // Directly get the graph the relation is attached to (or not if null)
      return Optional.of(graphType.cast(((Relation<?, ?>) object).getGraph()));
    } else if (object instanceof Label) {
      // Labels might not be directly attached to graphs, but to other elements of the graph
      LabelableElement container = ((Label) object).getContainer();
      Object resolvedContainer = resolveDelegation(container);
      if (resolvedContainer instanceof LabelableElement) {
        container = (LabelableElement) resolvedContainer;
      }
      if (graphType.isInstance(container)) {
        return Optional.of(graphType.cast(container));
      } else if (container != null) {
        // The container can be a label by itself or another graph item. Use recursion to find the
        // transitive graph.
        return getGraphForObject(container, graphType);
      }
    }

    return Optional.empty();
  }

  /**
   * Checks if any of the corpus graphs has a special feature annotation that stores the path from
   * where the project was originally imported from or exported to.
   * 
   * @param project The Salt project to check
   * @return If the feature annotation exists, the location as string.
   */
  public static Optional<String> getOriginalCorpusLocation(SaltProject project) {

    for (SCorpusGraph cg : project.getCorpusGraphs()) {
      SFeature anno = cg.getFeature(HEXATOMIC_NAMESPACE, CORPUS_ORIGIN);
      if (anno != null) {
        return Optional.ofNullable(anno.getValue_STEXT());
      }
    }

    return Optional.empty();
  }

  /**
   * Set a special feature annotation that stores the path from where the project was imported from
   * or exported to.
   * 
   * @param project The Salt project to extend
   * @param location The location which should be remembered.
   * @param appendCorpusName If true, append the corpus name to the project
   */

  public static void setOriginalCorpusLocation(SaltProject project, String location,
      boolean appendCorpusName) {
    for (SCorpusGraph cg : project.getCorpusGraphs()) {
      File f = new File(location);
      if (appendCorpusName) {
        // Get the name of the root corpus
        List<SNode> roots = cg.getRoots();
        if (roots.size() == 1) {
          f = new File(f, roots.get(0).getName());
        }
      }
      cg.removeLabel(SaltHelper.HEXATOMIC_NAMESPACE, SaltHelper.CORPUS_ORIGIN);
      cg.createFeature(SaltHelper.HEXATOMIC_NAMESPACE, SaltHelper.CORPUS_ORIGIN,
          f.getAbsolutePath());
    }
  }

  /**
   * Changes the covered text for an existing token.
   * 
   * <p>
   * This inserts the new text at the correct position in textual datasource and updates all textual
   * relations to use the new indexes.
   * </p>
   * 
   * @param token The token to change the covered text for.
   * @param newTokenText The new text the token should cover.
   */
  public static void changeTokenText(SToken token, String newTokenText) {
    SDocumentGraph graph = token.getGraph();
    @SuppressWarnings("rawtypes")
    List<DataSourceSequence> allSequences =
        graph.getOverlappedDataSourceSequence(token, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
    if (allSequences != null && !allSequences.isEmpty()) {
      DataSourceSequence<?> oldTokenSequence = allSequences.get(0);
      if (oldTokenSequence.getDataSource() instanceof STextualDS) {
        // To change the token value, we need to change the covered text in the textual relation
        STextualDS textDS = (STextualDS) oldTokenSequence.getDataSource();
        String textBefore = textDS.getText().substring(0, oldTokenSequence.getStart().intValue());
        String textAfter = textDS.getText().substring(oldTokenSequence.getEnd().intValue());
        textDS.setData(textBefore + newTokenText + textAfter);

        // In order for all other textual relations to be still valid, we need to update the
        // references to the text for the affected and all following token.
        int oldTokenTextLength =
            oldTokenSequence.getEnd().intValue() - oldTokenSequence.getStart().intValue();
        int offset = newTokenText.length() - oldTokenTextLength;
        updateTextRelationsWithOffset(graph, offset, textDS, oldTokenSequence);
      }
    }
  }


  private static void updateTextRelationsWithOffset(SDocumentGraph graph, int offset, STextualDS ds,
      DataSourceSequence<? extends Number> oldTokenSequence) {
    for (STextualRelation rel : graph.getTextualRelations()) {
      if (rel.getTarget() == ds) {
        if (rel.getStart() == oldTokenSequence.getStart().intValue()
            && rel.getEnd() == oldTokenSequence.getEnd().intValue()) {
          rel.setEnd(oldTokenSequence.getEnd().intValue() + offset);
        } else if (rel.getStart() >= oldTokenSequence.getEnd().intValue()) {
          rel.setStart(rel.getStart() + offset);
          rel.setEnd(rel.getEnd() + offset);
        }
      }
    }
  }
}
