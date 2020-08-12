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
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;

public class LabelRemoveOperation implements ReversibleOperation {

  private final Label label;

  private final LabelableElement container;


  /**
   * Creates a new operation for a removed label.
   * 
   * @param label The label that was removed.
   * @param container The container the label was removed from.
   */
  public LabelRemoveOperation(Label label, LabelableElement container) {
    super();
    this.label = label;
    this.container = container;
  }

  @Override
  public void restore() {
    if (container != null && label != null) {
      container.addLabel(label);
    }
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(container);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(label);
  }

}
