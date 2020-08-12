package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;

public class AddNodeToLayerOperation<N extends Node>
    implements ReversibleOperation {

  private final Layer<N, ?> layer;
  private final N node;
  
  /**
   * Create a reversible operation for adding a node to a layer.
   * 
   * @param layer The layer the node was added to.
   * @param node The node that was added to the layer.
   */
  public AddNodeToLayerOperation(Layer<N, ?> layer, N node) {
    super();
    this.layer = layer;
    this.node = node;
  }

  @Override
  public void restore() {
    layer.removeNode(node);
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(layer);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(node);
  }

}
