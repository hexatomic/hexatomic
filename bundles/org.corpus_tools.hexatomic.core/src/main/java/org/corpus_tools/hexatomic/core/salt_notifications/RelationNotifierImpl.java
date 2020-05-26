package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.RelationImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class RelationNotifierImpl<S extends Node, T extends Node>
    extends RelationImpl<S, T> implements Relation<S, T> {


  private static final long serialVersionUID = 1171405238664510985L;

  private final IEventBroker events;

  private Relation<?, ?> wrapper;

  public RelationNotifierImpl(IEventBroker events) {
    this.events = events;
  }

  private void sendEventBefore() {
    events.send(Topics.BEFORE_PROJECT_CHANGED, this.getId());
  }

  private void sendEventAfter() {
    events.send(Topics.PROJECT_CHANGED, this.getId());
  }

  public Relation<?, ?> getWrapper() {
    return wrapper;
  }

  public void setWrapper(Relation<?, ?> wrapper) {
    this.wrapper = wrapper;
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
  public void setSource(S source) {
    sendEventBefore();
    super.setSource(source);
    sendEventAfter();
  }

  @Override
  public void setTarget(T target) {
    sendEventBefore();
    super.setTarget(target);
    sendEventAfter();
  }

}
