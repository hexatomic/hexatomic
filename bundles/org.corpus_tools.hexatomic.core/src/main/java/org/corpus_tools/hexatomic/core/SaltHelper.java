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

import java.util.Optional;
import org.corpus_tools.hexatomic.core.events.salt.NotifyingElement;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

/**
 * A utility class to handle Salt objects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 */
public class SaltHelper {
  
  private SaltHelper() {
    // Should not be initiated
  }

  /**
   * Delegated elements might have an actual owning element. This functions returns the actual
   * owning element or the element itself. If there is a chain of owners, the last element in this
   * chain will be returned.
   * 
   * @param element The element to get the owner for
   * @return The actual implementation object
   */
  public static Object resolveDelegation(Object element) {
    if (element == null) {
      return null;
    }
    Object currentElement = element;
    while (currentElement instanceof NotifyingElement<?>
        && ((NotifyingElement<?>) currentElement).getOwner() != null) {
      currentElement = ((NotifyingElement<?>) currentElement).getOwner();
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
}
