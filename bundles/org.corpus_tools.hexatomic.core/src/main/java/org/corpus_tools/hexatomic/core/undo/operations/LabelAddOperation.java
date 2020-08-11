package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.graph.LabelableElement;

public class LabelAddOperation implements ReversibleOperation {

  private final String qname;

  private final LabelableElement container;

  public LabelAddOperation(LabelableElement container, String qname) {
    this.qname = qname;
    this.container = container;
  }

  @Override
  public void restore(ProjectManager projectManager) {
    if (container != null) {
      container.removeLabel(qname);
    }
  }

}
