package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Layer;

public class AddLayerToGraphOperation<L extends Layer<?, ?>> implements ReversibleOperation {

  private final L layer;
  private final Graph<?, ?, L> graph;

  /**
   * Create a undo operation for a layer that was added to a graph.
   * 
   * @param layer The layer that was added.
   */
  @SuppressWarnings("unchecked")
  public AddLayerToGraphOperation(L layer) {
    super();
    this.layer = layer;
    this.graph = layer.getGraph();
  }



  @Override
  public void restore() {
    if (graph != null) {
      graph.removeLayer(layer);
    }
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(graph);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(layer);
  }

}
