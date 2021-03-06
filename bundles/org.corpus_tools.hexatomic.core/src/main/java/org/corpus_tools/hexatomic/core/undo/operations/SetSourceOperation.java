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
  public void restore() {
    this.relation.setSource(oldNode);
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(relation);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(relation);
  }

}
