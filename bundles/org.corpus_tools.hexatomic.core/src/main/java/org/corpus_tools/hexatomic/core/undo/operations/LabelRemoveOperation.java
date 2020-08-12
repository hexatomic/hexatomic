package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;

public class LabelRemoveOperation implements ReversibleOperation {

  private final Label label;

  private final LabelableElement container;


  /**
   * Creates a new operation for a removed label.
   * 
   * @param label The label that was removed.
   * @param container The container the label was removed from.
   */
  public LabelRemoveOperation(Label label, LabelableElement container) {
    super();
    this.label = label;
    this.container = container;
  }

  @Override
  public void restore() {
    if (container != null && label != null) {
      container.addLabel(label);
    }
  }

  @Override
  public Object getChangedContainer() {
    return SaltHelper.resolveDelegation(container);
  }

  @Override
  public Object getChangedElement() {
    return SaltHelper.resolveDelegation(label);
  }

}
