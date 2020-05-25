package org.corpus_tools.hexatomic.core.undo;

import org.corpus_tools.salt.extensions.notification.Listener.NOTIFICATION_TYPE;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;

public class SaltUpdateEvent {
  private final NOTIFICATION_TYPE type;
  private final GRAPH_ATTRIBUTES attribute;
  private final Object oldValue;
  private final Object newValue;
  private final Object container;

  /**
   * Creates a new wrapper object around an Salt notification.
   * 
   * @param type The type of the update.
   * @param attribute Which attribute has been changed.
   * @param oldValue Old value of the object.
   * @param newValue New value of the object.
   * @param container On which container the update has been executed on.
   */
  public SaltUpdateEvent(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
      Object newValue, Object container) {
    super();
    this.type = type;
    this.attribute = attribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.container = container;
  }

  public NOTIFICATION_TYPE getType() {
    return type;
  }

  public GRAPH_ATTRIBUTES getAttribute() {
    return attribute;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public Object getContainer() {
    return container;
  }
}
