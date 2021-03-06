/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Layer;

public class RemoveLayerFromGraphOperation<L extends Layer<?, ?>> implements ReversibleOperation {

  private final L layer;
  private final Graph<?, ?, L> graph;

  /**
   * Create a undo operation for a layer that was removed from a graph.
   * 
   * @param graph The graph the layer belonged to.
   * @param layer The layer that was removed.
   */
  public RemoveLayerFromGraphOperation(Graph<?, ?, L> graph, L layer) {
    super();
    this.layer = layer;
    this.graph = graph;
  }



  @Override
  public void restore() {
    if (graph != null) {
      graph.addLayer(layer);
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
