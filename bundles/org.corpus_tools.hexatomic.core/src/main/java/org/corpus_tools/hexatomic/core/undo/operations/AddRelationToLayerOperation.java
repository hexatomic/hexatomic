package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class AddRelationToLayerOperation<N extends Node, R extends Relation<N, N>>
    implements ReversibleOperation {

  private final Layer<N, R> layer;
  private final R relation;
  
  /**
   * Create a reversible operation for adding a relation to a layer.
   * 
   * @param layer The layer the relation was added to.
   * @param relation The relation that was added to the layer.
   */
  public AddRelationToLayerOperation(Layer<N, R> layer, R relation) {
    super();
    this.layer = layer;
    this.relation = relation;
  }

  @Override
  public void restore() {
    layer.removeRelation(relation);
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(layer);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(relation);
  }

}
