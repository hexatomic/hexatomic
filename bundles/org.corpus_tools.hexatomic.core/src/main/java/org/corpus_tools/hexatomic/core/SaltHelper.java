package org.corpus_tools.hexatomic.core;

import java.util.Optional;
import org.corpus_tools.hexatomic.core.events.salt.NotifyingElement;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class SaltHelper {

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
   * @return The graph or empty value if not found.
   */
  public static Optional<Graph<?, ?, ?>> getGraphForObject(Object object) {

    if (object instanceof Graph<?, ?, ?>) {
      // Basic case: the object is already a graph
      return Optional.of((Graph<?, ?, ?>) object);
    } else if (object instanceof Node) {
      // Directly get the graph the node is attached to (or not if null)
      return Optional.ofNullable(((Node) object).getGraph());
    } else if (object instanceof Relation<?, ?>) {
      // Directly get the graph the relation is attached to (or not if null)
      Graph<?, ?, ?> g = ((Relation<?, ?>) object).getGraph();
      if (g instanceof Graph<?, ?, ?>) {
        return Optional.of((Graph<?, ?, ?>) g);
      }
    } else if (object instanceof Label) {
      // Labels might not be directly attached to graphs, but to other elements of the graph
      LabelableElement container = ((Label) object).getContainer();
      if (container instanceof Graph<?, ?, ?>) {
        return Optional.of((Graph<?, ?, ?>) container);
      } else if (container != null) {
        // The container can be a label by itself or another graph item. Use recursion to find the
        // transitive graph.
        return getGraphForObject(container);
      }
    }

    return Optional.empty();
  }
}
