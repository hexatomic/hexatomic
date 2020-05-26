package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.impl.NodeImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class NodeNotifierImpl extends NodeImpl implements Node {

  private static final long serialVersionUID = -7940440063671378198L;
  private final IEventBroker events;


  public NodeNotifierImpl(IEventBroker events) {
    this.events = events;
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
}
