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

import static org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory.sendEvent;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.undo.operations.SetSourceOperation;
import org.corpus_tools.hexatomic.core.undo.operations.SetTargetOperation;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.RelationImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

/**
 * Implements a Salt {@link Relation} which use the {@link IEventBroker} to send events when the
 * relation was updated.
 * 
 * <p>
 * The event will have the ID of the relation as argument.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class RelationNotifierImpl<S extends Node, T extends Node>
    extends RelationImpl<S, T>
    implements Relation<S, T>, NotifyingLabelableElement<Relation<?, ?>> {


  private static final long serialVersionUID = 1171405238664510985L;

  private Relation<?, ?> typedDelegation;

  @Override
  public Relation<?, ?> getTypedDelegation() {
    return typedDelegation;
  }

  @Override
  public void setTypedDelegation(Relation<?, ?> typedDelegation) {
    this.typedDelegation = typedDelegation;
  }


  @Override
  public void addLabel(Label label) {
    applyAddLabel(() -> super.addLabel(label), this, label);
  }

  @Override
  public void removeLabel(String qname) {
    applyRemoveLabelIfExisting(() -> super.removeLabel(qname), qname);
  }

  @Override
  public void removeAll() {
    applyRemoveAllLabels(super::removeAll, this);
  }

  @Override
  public void setSource(S source) {
    S oldNode = getSource();
    super.setSource(source);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED, new SetSourceOperation<S>(this, oldNode));
  }

  @Override
  public void setTarget(T target) {
    T oldNode = getTarget();
    super.setTarget(target);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED, new SetTargetOperation<T>(this, oldNode));
  }

  @Override
  public void setId(String id) {
    setNotficiationAwareId(this, id);
  }
}
