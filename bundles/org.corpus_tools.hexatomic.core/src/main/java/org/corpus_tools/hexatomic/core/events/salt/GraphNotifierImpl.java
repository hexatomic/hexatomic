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
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.GraphImpl;
import org.corpus_tools.salt.graph.impl.NodeImpl;
import org.corpus_tools.salt.graph.impl.RelationImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

/**
 * Implements a Salt {@link Graph} which use the {@link IEventBroker} to send events when the graph
 * was updated.
 * 
 * <p>
 * The event will have the ID of the graph as argument.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public class GraphNotifierImpl extends
    GraphImpl<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>>
    implements Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>>,
    NotifyingElement<Graph<?, ?, ?>> {

  private static final long serialVersionUID = 2590632940284255617L;

  private final IEventBroker events;
  private final ProjectManager projectManager;

  private Graph<?, ?, ?> owner;

  public GraphNotifierImpl(IEventBroker events, ProjectManager projectManager) {
    this.events = events;
    this.projectManager = projectManager;
  }

  @Override
  public Graph<?, ?, ?> getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Graph<?, ?, ?> owner) {
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
  public void addNode(Node node) {
    super.addNode(node);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addNode() would have an optional parameter for
    // the real graph.
    if (owner != null) {
      if (node instanceof NodeImpl) {
        ((NodeImpl) node).basicSetGraph_WithoutRemoving(owner);
      }
    }
    sendEvent(Topics.ANNOTATION_ADDED, node);
  }

  @Override
  public void removeNode(Node node) {
    sendEvent(Topics.ANNOTATION_REMOVED, node);
    super.removeNode(node);
  }

  @Override
  public void addRelation(Relation<? extends Node, ? extends Node> relation) {
    super.addRelation(relation);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addRelation() would have an optional parameter for
    // the real graph.
    if (owner != null) {
      if (relation instanceof RelationImpl<?, ?>) {
        ((RelationImpl<?, ?>) relation).basicSetGraph_WithoutRemoving(owner);
      }
    }
    sendEvent(Topics.ANNOTATION_ADDED, relation);
  }

  @Override
  public void removeRelation(Relation<? extends Node, ? extends Node> rel) {
    sendEvent(Topics.ANNOTATION_REMOVED, rel);
    super.removeRelation(rel);
  }

  @Override
  public void removeRelations() {
    super.removeRelations();
    sendEvent(Topics.ANNOTATION_REMOVED, this);
  }

  @Override
  public void addLayer(Layer<Node, Relation<Node, Node>> layer) {
    super.addLayer(layer);
    sendEvent(Topics.ANNOTATION_ADDED, layer);
  }

  @Override
  public void removeLayer(Layer<Node, Relation<Node, Node>> layer) {
    sendEvent(Topics.ANNOTATION_REMOVED, layer);
    super.removeLayer(layer);
  }
}
