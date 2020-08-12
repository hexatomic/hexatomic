package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class AddRelationToGraphOperation<N extends Node, R extends Relation<N, N>>
    implements ReversibleOperation {

  private final R rel;
  private final Graph<N, R, ?> graph;

  /**
   * Create a undo operation for a relation that was added to a graph.
   * 
   * @param relation The relation that was added.
   */
  @SuppressWarnings("unchecked")
  public AddRelationToGraphOperation(R relation) {
    super();
    this.rel = relation;
    this.graph = relation.getGraph();
  }



  @Override
  public void restore() {
    if (graph != null) {
      graph.removeRelation(rel);
    }
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(graph);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(rel);
  }
}
