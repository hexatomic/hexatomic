package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Node;

public class AddNodeToGraphOperation<N extends Node> implements ReversibleOperation {

  private final String nodeID;
  private final Graph<N, ?, ?> graph;

  /**
   * Create a undo operation for a node that was added to a graph.
   * 
   * @param node The node that was added.
   */
  @SuppressWarnings("unchecked")
  public AddNodeToGraphOperation(N node) {
    super();
    this.nodeID = node.getId();
    this.graph = node.getGraph();
  }



  @Override
  public void restore(ProjectManager projectManager) {
    if (graph != null) {
      N n = graph.getNode(nodeID);
      if (n != null) {
        graph.removeNode(n);
      }
    }
  }

  @Override
  public Object getContainer() {
    return SaltHelper.resolveDelegation(graph);
  }

}
