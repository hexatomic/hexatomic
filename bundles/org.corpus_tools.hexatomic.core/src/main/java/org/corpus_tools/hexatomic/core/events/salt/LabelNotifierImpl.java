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
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.impl.LabelImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class LabelNotifierImpl extends LabelImpl implements Label {

  private static final long serialVersionUID = 8010124349555159857L;

  private final IEventBroker events;
  private final ProjectManager projectManager;

  public LabelNotifierImpl(IEventBroker events, ProjectManager projectManager) {
    this.events = events;
    this.projectManager = projectManager;
  }

  private void sendEventBefore() {
    if (!projectManager.isSuppressingEvents()) {
      String id = null;
      if (getContainer() instanceof IdentifiableElement) {
        id = ((IdentifiableElement) getContainer()).getId();
      }
      events.send(Topics.BEFORE_PROJECT_CHANGED, id);
    }
  }

  private void sendEventAfter() {
    if (!projectManager.isSuppressingEvents()) {
      String id = null;
      if (getContainer() instanceof IdentifiableElement) {
        id = ((IdentifiableElement) getContainer()).getId();
      }
      events.send(Topics.PROJECT_CHANGED, id);
    }
  }

  @Override
  public void addLabel(Label label) {
    sendEventBefore();
    super.addLabel(label);
    sendEventAfter();
  }

  @Override
  public void removeLabel(String namespace, String name) {
    sendEventBefore();
    super.removeLabel(namespace, name);
    sendEventAfter();
  }

  @Override
  public void removeAll() {
    sendEventBefore();
    super.removeAll();
    sendEventAfter();
  }

  @Override
  public void setNamespace(String namespace) {
    sendEventBefore();
    super.setNamespace(namespace);
    sendEventAfter();
  }

  @Override
  public void setName(String name) {
    sendEventBefore();
    super.setName(name);
    sendEventAfter();
  }

  @Override
  public void setQName(String newQName) {
    sendEventBefore();
    super.setQName(newQName);
    sendEventAfter();
  }

  @Override
  public void setValue(Object value) {
    sendEventBefore();
    super.setValue(value);
    sendEventAfter();
  }
}
