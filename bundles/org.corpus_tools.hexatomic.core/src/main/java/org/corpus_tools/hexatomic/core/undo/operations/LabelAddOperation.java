package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.LabelableElement;

public class LabelAddOperation implements ReversibleOperation {

  private final String qname;

  private final LabelableElement container;

  public LabelAddOperation(LabelableElement container, String qname) {
    this.qname = qname;
    this.container = container;
  }

  @Override
  public void restore() {
    if (container != null) {
      container.removeLabel(qname);
    }
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(container);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(container.getLabel(qname));
  }

}
