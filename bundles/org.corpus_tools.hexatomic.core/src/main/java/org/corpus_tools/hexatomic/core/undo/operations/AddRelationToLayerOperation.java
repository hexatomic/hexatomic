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
