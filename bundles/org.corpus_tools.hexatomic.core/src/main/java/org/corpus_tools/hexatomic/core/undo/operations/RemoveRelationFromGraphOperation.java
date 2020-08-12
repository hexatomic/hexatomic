package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class RemoveRelationFromGraphOperation<N extends Node, R extends Relation<N, N>>
    implements ReversibleOperation {

  private final R relation;
  private final Graph<N, R, ?> graph;

  /**
   * Create a undo operation for a relation that was removed from a graph.
   * 
   * @param relation The relation that was removed.
   * @param graph The graph the relation was removed from.
   */
  public RemoveRelationFromGraphOperation(R relation, Graph<N, R, ?> graph) {
    super();
    this.relation = relation;
    this.graph = graph;
  }



  @Override
  public void restore(ProjectManager projectManager) {
    if (graph != null) {
      graph.addRelation(relation);
    }
  }

  @Override
  public Object getContainer() {
    return SaltHelper.resolveDelegation(graph);
  }

}
