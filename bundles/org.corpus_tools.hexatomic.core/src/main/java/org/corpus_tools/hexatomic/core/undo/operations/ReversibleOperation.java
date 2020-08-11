package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.ProjectManager;

public interface ReversibleOperation {
  public void restore(ProjectManager projectManager);
}
