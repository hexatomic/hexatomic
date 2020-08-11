/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

package org.corpus_tools.hexatomic.core.events.salt;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.impl.LabelImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

/**
 * Implements a Salt {@link Label} which use the {@link IEventBroker} to send events when the label
 * was updated.
 * 
 * <p>
 * The event will have the ID of the element this label belongs to as argument.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class LabelNotifierImpl extends LabelImpl
    implements Label, NotifyingLabelableElement<Label> {

  private static final long serialVersionUID = 8010124349555159857L;

  private Label typedDelegation;


  @Override
  public Label getTypedDelegation() {
    return typedDelegation;
  }

  @Override
  public void setTypedDelegation(Label typedDelegation) {
    this.typedDelegation = typedDelegation;
  }


  @Override
  public void addLabel(Label label) {
    applyAdd(() -> super.addLabel(label), label);
  }

  @Override
  public void removeLabel(String qname) {
    applyRemoveLabelIfExisting(() -> super.removeLabel(qname), qname);
  }

  @Override
  public void removeAll() {
    applyModification(super::removeAll);
  }

  @Override
  public void setNamespace(String namespace) {
    modified(() -> super.setNamespace(namespace));
  }

  @Override
  public void setName(String name) {
    modified(() -> super.setName(name));
  }

  @Override
  public void setValue(Object value) {
    modified(() -> super.setValue(value));
  }

  protected void modified(GraphModificationAction action) {
    SaltNotificationFactory.sendEvent(Topics.LABEL_MODIFY, this);
    action.apply();
  }

}
