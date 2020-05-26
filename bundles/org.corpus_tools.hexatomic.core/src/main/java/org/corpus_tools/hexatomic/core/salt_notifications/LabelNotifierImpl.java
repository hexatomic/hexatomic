package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.impl.LabelImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class LabelNotifierImpl extends LabelImpl implements Label {

  private static final long serialVersionUID = 8010124349555159857L;

  private final IEventBroker events;

  public LabelNotifierImpl(IEventBroker events) {
    this.events = events;
  }

  private void sendEventBefore() {
    String id = null;
    if (getContainer() instanceof IdentifiableElement) {
      id = ((IdentifiableElement) getContainer()).getId();
    }
    events.send(Topics.BEFORE_PROJECT_CHANGED, id);
  }

  private void sendEventAfter() {
    String id = null;
    if (getContainer() instanceof IdentifiableElement) {
      id = ((IdentifiableElement) getContainer()).getId();
    }
    events.send(Topics.PROJECT_CHANGED, id);
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
  public void setNamespace(String namespace) {
    sendEventBefore();
    super.setNamespace(namespace);
    sendEventAfter();
  }

  @Override
  public void setName(String name) {
    sendEventBefore();
    super.setName(name);
    sendEventAfter();
  }

  @Override
  public void setQName(String newQName) {
    sendEventBefore();
    super.setQName(newQName);
    sendEventAfter();
  }

  @Override
  public void setValue(Object value) {
    sendEventBefore();
    super.setValue(value);
    sendEventAfter();
  }
}
