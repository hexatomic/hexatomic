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

import java.util.LinkedList;
import java.util.List;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.undo.operations.AddLayerToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddNodeToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddRelationToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveLayerFromGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveNodeFromGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveRelationFromGraphOperation;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.GraphImpl;
import org.corpus_tools.salt.graph.impl.NodeImpl;
import org.corpus_tools.salt.graph.impl.RelationImpl;
import org.corpus_tools.salt.util.SaltUtil;
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
public class GraphNotifierImpl<N extends Node, R extends Relation<N, N>, L extends Layer<N, R>>
    extends
    GraphImpl<N, R, L>
    implements Graph<N, R, L>,
    NotifyingLabelableElement<Graph<N, R, L>> {

  private static final long serialVersionUID = 2590632940284255617L;

  private Graph<N, R, L> typedDelegation;

  @Override
  public Graph<N, R, L> getTypedDelegation() {
    return typedDelegation;
  }

  @Override
  public void setTypedDelegation(Graph<N, R, L> typedDelegation) {
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
  public void addNode(N node) {
    @SuppressWarnings("unchecked")
    Graph<N, ?, ?> oldGraph = node.getGraph();
    if (oldGraph != null) {
      oldGraph.removeNode(node);
    }
    super.addNode(node);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addNode() would have an optional parameter for
    // the real graph.
    if (typedDelegation != null && node instanceof NodeImpl) {
      ((NodeImpl) node).basicSetGraph_WithoutRemoving(typedDelegation);
    }
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new AddNodeToGraphOperation<Node>(node));
  }

  @Override
  public void removeNode(N node) {
    super.removeNode(node);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new RemoveNodeFromGraphOperation<N>(node, typedDelegation));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addRelation(Relation<? extends N, ? extends N> relation) {
    @SuppressWarnings("rawtypes")
    Graph oldGraph = relation.getGraph();
    if (oldGraph != null) {
      oldGraph.removeRelation(relation);
    }
    super.addRelation(relation);
    // HACK: Reset to the actual owning graph.
    // It would be better if the super.addRelation() would have an optional parameter for
    // the real graph.
    if (typedDelegation != null && relation instanceof RelationImpl<?, ?>) {
      ((RelationImpl<?, ?>) relation).basicSetGraph_WithoutRemoving(typedDelegation);
    }
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new AddRelationToGraphOperation<Node, Relation<Node, Node>>(
            (Relation<Node, Node>) relation));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void removeRelation(Relation<? extends N, ? extends N> rel) {
    super.removeRelation(rel);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new RemoveRelationFromGraphOperation<N, R>((R) rel,
            typedDelegation));
  }

  @Override
  public void removeRelations() {
    List<R> relations = new LinkedList<>(getRelations());
    super.removeRelations();
    for (R rel : relations) {
      sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
          new RemoveRelationFromGraphOperation<N, R>(rel, typedDelegation));
    }
  }

  @Override
  public void addLayer(L layer) {
    // Make sure the layer object does not have an existing ID label from being added
    // previously.
    layer.removeLabel(SaltUtil.LABEL_ID_QNAME);
    super.addLayer(layer);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new AddLayerToGraphOperation<Layer<N, R>>(layer));
  }

  @Override
  public void removeLayer(L layer) {
    super.removeLayer(layer);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new RemoveLayerFromGraphOperation<L>(typedDelegation, layer));
  }

  @Override
  public void setId(String id) {
    setNotficiationAwareId(this, id);
  }
}
