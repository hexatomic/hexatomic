package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class SetSourceOperation<S extends Node> implements ReversibleOperation {

  private final S oldNode;
  private final Relation<S, ? extends Node> relation;
  

  /**
   * Constructs a new operation that can reverse setting the source of the relation.
   * 
   * @param relation The relation
   * @param oldNode The previously set source node
   */
  public SetSourceOperation(Relation<S, ?> relation, S oldNode) {
    super();
    this.relation = relation;
    this.oldNode = oldNode;
  }

  @Override
  public void restore(ProjectManager projectManager) {
    this.relation.setSource(oldNode);
  }

  @Override
  public Object getContainer() {
    return SaltHelper.resolveDelegation(relation);
  }

}
