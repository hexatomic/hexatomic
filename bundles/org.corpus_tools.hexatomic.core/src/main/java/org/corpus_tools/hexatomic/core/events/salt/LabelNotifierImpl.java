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
 *
 */
public class LabelNotifierImpl extends LabelImpl implements Label, NotifyingElement<Label> {

  private static final long serialVersionUID = 8010124349555159857L;

  private final IEventBroker events;
  private final ProjectManager projectManager;
  private Label owner;

  public LabelNotifierImpl(IEventBroker events, ProjectManager projectManager) {
    this.events = events;
    this.projectManager = projectManager;
  }

  @Override
  public Label getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Label owner) {
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
    super.removeAll();
    sendEvent(Topics.ANNOTATION_MODIFIED, this);
  }

  @Override
  public void setNamespace(String namespace) {
    super.setNamespace(namespace);
    sendEvent(Topics.ANNOTATION_MODIFIED, this);
  }

  @Override
  public void setName(String name) {
    super.setName(name);
    sendEvent(Topics.ANNOTATION_MODIFIED, this);
  }

  @Override
  public void setQName(String newQName) {
    super.setQName(newQName);
    sendEvent(Topics.ANNOTATION_MODIFIED, this);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    sendEvent(Topics.ANNOTATION_MODIFIED, this);
  }
}
