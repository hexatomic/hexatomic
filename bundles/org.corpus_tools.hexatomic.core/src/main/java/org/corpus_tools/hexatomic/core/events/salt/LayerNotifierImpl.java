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
import org.corpus_tools.hexatomic.core.undo.operations.AddNodeToLayerOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddRelationToLayerOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveNodeFromLayerOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveRelationFromLayerOperation;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.LayerImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

/**
 * Implements a Salt {@link Layer} which use the {@link IEventBroker} to send events when the layer
 * was updated.
 * 
 * <p>
 * The event will have the ID of the layer as argument.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class LayerNotifierImpl<N extends Node, R extends Relation<N, N>> extends LayerImpl<N, R>
    implements Layer<N, R>, NotifyingLabelableElement<Layer<N, R>> {


  private static final long serialVersionUID = -4708308546018698463L;

  private Layer<N, R> typedDelegation;


  @Override
  public Layer<N, R> getTypedDelegation() {
    return typedDelegation;
  }

  @Override
  public void setTypedDelegation(Layer<N, R> typedDelegation) {
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
    super.addNode(node);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new AddNodeToLayerOperation<N>(typedDelegation, node));
  }

  @Override
  public void removeNode(N node) {
    if (this.getNodes().contains(node)) {
      super.removeNode(node);
      sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
          new RemoveNodeFromLayerOperation<N>(typedDelegation, node));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addRelation(Relation<? extends N, ? extends N> relation) {
    super.addRelation(relation);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
        new AddRelationToLayerOperation<N, R>(typedDelegation, (R) relation));

  }

  @SuppressWarnings("unchecked")
  @Override
  public void removeRelation(Relation<? extends N, ? extends N> rel) {
    if (this.getRelations().contains(rel)) {
      super.removeRelation(rel);
      sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
          new RemoveRelationFromLayerOperation<N, R>(typedDelegation, (R) rel));
    }
  }

  @Override
  public void setId(String id) {
    setNotficiationAwareId(this, id);
  }
}
