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

/**
 * Operation that represents a modification of a label (like setting the value, name etc.).
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public class LabelModifyOperation implements ReversibleOperation {

  private final LabelableElement container;
  private final Label label;
  private final String namespace;
  private final String name;
  private final Object value;


  /**
   * Creates a new label modification operation.
   * 
   * @param label The changed label.
   * @param namespace The old namespace.
   * @param name The old name.
   * @param value The old value.
   * 
   */
  public LabelModifyOperation(Label label, String namespace, String name, Object value) {

    this.container = label.getContainer();
    this.label = label;
    this.namespace = namespace;
    this.name = name;
    this.value = value;
  }

  @Override
  public void restore() {

    label.setNamespace(namespace);
    label.setName(name);
    label.setValue(value);
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
