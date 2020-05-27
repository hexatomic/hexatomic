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

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.Topics;
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
 *
 */
public class RelationNotifierImpl<S extends Node, T extends Node>
    extends RelationImpl<S, T> implements Relation<S, T>, NotifyingElement<Relation<?, ?>> {


  private static final long serialVersionUID = 1171405238664510985L;

  private final IEventBroker events;
  private final ProjectManager projectManager;
  private Relation<?, ?> owner;

  public RelationNotifierImpl(IEventBroker events, ProjectManager projectManager) {
    this.events = events;
    this.projectManager = projectManager;
  }

  @Override
  public Relation<?, ?> getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Relation<?, ?> owner) {
    this.owner = owner;
  }

  private void sendEvent(String topic, Object element) {
    if (!projectManager.isSuppressingEvents()) {
      events.send(topic, SaltHelper.resolveDelegation(element));
    }
  }

  @Override
  public void addLabel(Label label) {
    super.addLabel(label);
    sendEvent(Topics.ANNOTATION_ADDED, label);
  }

  @Override
  public void removeLabel(String qname) {
    if (qname != null) {
      Label label = getLabel(qname);
      sendEvent(Topics.ANNOTATION_REMOVED, label);
      super.removeLabel(qname);
    }
  }

  @Override
  public void removeAll() {
    sendEvent(Topics.ANNOTATION_BEFORE_MODIFICATION, this);
    super.removeAll();
    sendEvent(Topics.ANNOTATION_AFTER_MODIFICATION, this);
  }

  @Override
  public void setSource(S source) {
    sendEvent(Topics.ANNOTATION_BEFORE_MODIFICATION, this);
    super.setSource(source);
    sendEvent(Topics.ANNOTATION_AFTER_MODIFICATION, this);
  }

  @Override
  public void setTarget(T target) {
    sendEvent(Topics.ANNOTATION_BEFORE_MODIFICATION, this);
    super.setTarget(target);
    sendEvent(Topics.ANNOTATION_AFTER_MODIFICATION, this);
  }

}
