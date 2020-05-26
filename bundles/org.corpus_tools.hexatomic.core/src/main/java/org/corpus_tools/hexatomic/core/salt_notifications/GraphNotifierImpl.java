package org.corpus_tools.hexatomic.core.salt_notifications;

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

public class GraphNotifierImpl extends
    GraphImpl<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>>
    implements Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>> {

  private static final long serialVersionUID = 2590632940284255617L;

  private final IEventBroker events;

  private Graph<?, ?, ?> owner;

  public GraphNotifierImpl(IEventBroker events) {
    this.events = events;
  }

  public Graph<?, ?, ?> getOwner() {
    return owner;
  }

  public void setOwner(Graph<?, ?, ?> owner) {
    this.owner = owner;
  }

  private void sendEventBefore() {
    events.send(Topics.BEFORE_PROJECT_CHANGED, this.getId());
  }

  private void sendEventAfter() {
    events.send(Topics.PROJECT_CHANGED, this.getId());
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
  public void addNode(Node node) {
    sendEventBefore();
    super.addNode(node);
    // HACK: Reset to the actual owning graph before notifying the listeners.
    // It would be better if the super.addNode() would have an optional parameter for
    // the real graph.
    if (owner != null) {
      if (node instanceof NodeImpl) {
        ((NodeImpl) node).basicSetGraph_WithoutRemoving(owner);
      }
    }
    sendEventAfter();
  }

  @Override
  public void removeNode(Node node) {
    sendEventBefore();
    super.removeNode(node);
    sendEventAfter();
  }

  @Override
  public void addRelation(Relation<? extends Node, ? extends Node> relation) {
    sendEventBefore();
    super.addRelation(relation);
    // HACK: Reset to the actual owning graph before notifying the listeners.
    // It would be better if the super.addRelation() would have an optional parameter for
    // the real graph.
    if (owner != null) {
      if (relation instanceof RelationImpl<?, ?>) {
        ((RelationImpl<?, ?>) relation).basicSetGraph_WithoutRemoving(owner);
      }
    }
    sendEventAfter();
  }

  @Override
  public void removeRelation(Relation<? extends Node, ? extends Node> rel) {
    sendEventBefore();
    super.removeRelation(rel);
    sendEventAfter();
  }

  @Override
  public void removeRelations() {
    sendEventBefore();
    super.removeRelations();
    sendEventAfter();
  }

  @Override
  public void addLayer(Layer<Node, Relation<Node, Node>> layer) {
    sendEventBefore();
    super.addLayer(layer);
    sendEventAfter();
  }

  @Override
  public void removeLayer(Layer<Node, Relation<Node, Node>> layer) {
    sendEventBefore();
    super.removeLayer(layer);
    sendEventAfter();
  }
}
