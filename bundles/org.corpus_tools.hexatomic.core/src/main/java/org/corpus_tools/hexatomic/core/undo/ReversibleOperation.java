package org.corpus_tools.hexatomic.core.undo;

public interface ReversibleOperation {
  public void restore();

  public Object getChangedContainer();

  public Object getChangedElement();
}
