package org.corpus_tools.hexatomic.core.events.salt;

public interface NotifyingElement<T> {
  public T getOwner();

  public void setOwner(T owner);
}
