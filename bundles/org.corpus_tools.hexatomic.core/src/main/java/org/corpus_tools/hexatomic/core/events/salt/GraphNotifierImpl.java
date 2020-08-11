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
import org.corpus_tools.hexatomic.core.undo.operations.AddNodeToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveNodeFromGraphOperation;
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
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GraphNotifierImpl extends
    GraphImpl<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>>
    implements Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>>,
    NotifyingLabelableElement<Graph<?, ?, ?>> {

  private static final long serialVersionUID = 2590632940284255617L;

  private Graph<?, ?, ?> typedDelegation;

  @Override
  public Graph<?, ?, ?> getTypedDelegation() {
    return typedDelegation;
  }

  @Override
  public void setTypedDelegation(Graph<?, ?, ?> typedDelegation) {
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
  public void addNode(Node node) {
    super.addNode(node);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addNode() would have an optional parameter for
    // the real graph.
    if (typedDelegation != null && node instanceof NodeImpl) {
      ((NodeImpl) node).basicSetGraph_WithoutRemoving(typedDelegation);
    }
    SaltNotificationFactory.sendEvent(Topics.UNDO_OPERATION_ADDED,
        new AddNodeToGraphOperation<Node>(node));
  }

  @Override
  public void removeNode(Node node) {
    super.removeNode(node);
    SaltNotificationFactory.sendEvent(Topics.UNDO_OPERATION_ADDED,
        new RemoveNodeFromGraphOperation<Node>(node, this));
  }

  @Override
  public void addRelation(Relation<? extends Node, ? extends Node> relation) {
    super.addRelation(relation);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addRelation() would have an optional parameter for
    // the real graph.
    if (typedDelegation != null && relation instanceof RelationImpl<?, ?>) {
      ((RelationImpl<?, ?>) relation).basicSetGraph_WithoutRemoving(typedDelegation);
    }
    SaltNotificationFactory.sendEvent(Topics.ANNOTATION_ADDED, relation);
  }

  @Override
  public void removeRelation(Relation<? extends Node, ? extends Node> rel) {
    applyRemove(() -> super.removeRelation(rel), rel);
  }

  @Override
  public void removeRelations() {
    applyModification(super::removeRelations);
  }

  @Override
  public void addLayer(Layer<Node, Relation<Node, Node>> layer) {
    applyAdd(() -> super.addLayer(layer), layer);
  }

  @Override
  public void removeLayer(Layer<Node, Relation<Node, Node>> layer) {
    applyRemove(() -> super.removeLayer(layer), layer);
  }
}
