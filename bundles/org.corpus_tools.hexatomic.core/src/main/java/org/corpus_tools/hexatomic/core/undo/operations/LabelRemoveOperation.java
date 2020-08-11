package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
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
  public void restore(ProjectManager projectManager) {
    // TODO Auto-generated method stub

  }

}
