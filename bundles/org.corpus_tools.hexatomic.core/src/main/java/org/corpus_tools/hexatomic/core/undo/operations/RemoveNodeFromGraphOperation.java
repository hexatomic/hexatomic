package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Node;

public class RemoveNodeFromGraphOperation<N extends Node> implements ReversibleOperation {

  private final N node;
  private final Graph<N, ?, ?> graph;

  /**
   * Create a undo operation for a node that was removed from a graph.
   * 
   * @param node The node that was removed.
   * @param graph The graph the node was removed from.
   */
  public RemoveNodeFromGraphOperation(N node, Graph<N, ?, ?> graph) {
    super();
    this.node = node;
    this.graph = graph;
  }



  @Override
  public void restore(ProjectManager projectManager) {
    if (graph != null) {
      graph.addNode(node);
    }
  }

  @Override
  public Object getContainer() {
    return SaltHelper.resolveDelegation(graph);
  }

}
