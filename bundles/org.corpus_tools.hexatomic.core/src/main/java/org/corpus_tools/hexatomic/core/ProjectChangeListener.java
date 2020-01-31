/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.core;

import javax.inject.Inject;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

/**
 * This class listens to changes to the global Salt project and notifies views that the state has
 * changed.
 * 
 * @author Thomas Krause
 *
 */
@Creatable
public class ProjectChangeListener implements Listener {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectChangeListener.class);

  @Inject
  private IEventBroker events;

  /**
   * {@inheritDoc}
   */
  @Override
  public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
      Object newValue, Object container) {

    log.trace(
        "Project update: type={} attribute={} oldValue={} newValue={} container={} "
            + "containerType={}",
        type, attribute, oldValue, newValue, container,
        container == null ? "null" : container.getClass().getSimpleName());

    // Notify all views

    // It seems that an update on the corpus graph does not trigger a corresponding event in Salt
    // yet, check if the updated
    // label is attached to a corpus graph
    Object modifiedObject = container;
    if (modifiedObject instanceof Label) {
      // get the object the label belongs to
      modifiedObject = ((Label) container).getContainer();
    }

    if (modifiedObject instanceof Node) {
      Node n = (Node) modifiedObject;
      if (n.getGraph() instanceof SCorpusGraph) {
        events.send(ProjectManager.TOPIC_CORPUS_STRUCTURE_CHANGED, null);
      }
    } else if (modifiedObject instanceof SCorpusGraph || modifiedObject instanceof SCorpus
        || modifiedObject instanceof SDocument) {
      events.send(ProjectManager.TOPIC_CORPUS_STRUCTURE_CHANGED, null);
    }
  }

}
