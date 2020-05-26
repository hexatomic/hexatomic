package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.GraphImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class GraphNotifierImpl<N extends Node, R extends Relation<N, N>, L extends Layer<N, R>>
    extends GraphImpl<N, R, L> implements Graph<N, R, L> {

  private static final long serialVersionUID = 2590632940284255617L;

  private final IEventBroker events;

  private Graph<?, ?, ?> wrapper;

  public GraphNotifierImpl(IEventBroker events) {
    this.events = events;
  }

  public Graph<?, ?, ?> getWrapper() {
    return wrapper;
  }

  public void setWrapper(Graph<?, ?, ?> wrapper) {
    this.wrapper = wrapper;
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
  public void addNode(N node) {
    sendEventBefore();
    super.addNode(node);
    sendEventAfter();
  }

  @Override
  public void removeNode(N node) {
    sendEventBefore();
    super.removeNode(node);
    sendEventAfter();
  }

  @Override
  public void addRelation(Relation<? extends N, ? extends N> relation) {
    sendEventBefore();
    super.addRelation(relation);
    sendEventAfter();
  }

  @Override
  public void removeRelation(Relation<? extends N, ? extends N> rel) {
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
  public void addLayer(L layer) {
    sendEventBefore();
    super.addLayer(layer);
    sendEventAfter();
  }

  @Override
  public void removeLayer(L layer) {
    sendEventBefore();
    super.removeLayer(layer);
    sendEventAfter();
  }
}
