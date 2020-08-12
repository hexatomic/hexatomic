package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class SetTargetOperation<T extends Node> implements ReversibleOperation {

  private final T oldNode;
  private final Relation<?, T> relation;
  

  /**
   * Constructs a new operation that can reverse setting the target of the relation.
   * 
   * @param relation The relation
   * @param oldNode The previously set target node
   */
  public SetTargetOperation(Relation<?, T> relation, T oldNode) {
    super();
    this.relation = relation;
    this.oldNode = oldNode;
  }

  @Override
  public void restore(ProjectManager projectManager) {
    this.relation.setTarget(oldNode);
  }

  @Override
  public Object getContainer() {
    return SaltHelper.resolveDelegation(relation);
  }

}
