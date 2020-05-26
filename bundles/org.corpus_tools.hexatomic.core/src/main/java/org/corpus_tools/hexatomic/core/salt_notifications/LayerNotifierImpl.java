package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.LayerImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class LayerNotifierImpl<N extends Node, R extends Relation<N, N>> extends LayerImpl<N, R>
    implements Layer<N, R> {


  private static final long serialVersionUID = -4708308546018698463L;

  private final IEventBroker events;
  private final ProjectManager projectManager;

  public LayerNotifierImpl(IEventBroker events, ProjectManager projectManager) {
    this.events = events;
    this.projectManager = projectManager;
  }

  private void sendEventBefore() {
    if (!projectManager.isSuppressingEvents()) {
      events.send(Topics.BEFORE_PROJECT_CHANGED, this.getId());
    }
  }

  private void sendEventAfter() {
    if (!projectManager.isSuppressingEvents()) {
      events.send(Topics.PROJECT_CHANGED, this.getId());
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
}
