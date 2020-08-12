package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;

public class RemoveRelationFromLayerOperation<N extends Node, R extends Relation<N, N>>
    implements ReversibleOperation {

  private final Layer<N, R> layer;
  private final R relation;
  
  /**
   * Create a reversible operation for removing a relation from a layer.
   * 
   * @param layer The layer the relation was added to.
   * @param relation The relation that was added to the layer.
   */
  public RemoveRelationFromLayerOperation(Layer<N, R> layer, R relation) {
    super();
    this.layer = layer;
    this.relation = relation;
  }

  @Override
  public void restore() {
    layer.addRelation(relation);
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
