package org.corpus_tools.hexatomic.core.undo;

import org.corpus_tools.hexatomic.core.ProjectManager;

public interface ReversibleOperation {
  public void restore(ProjectManager projectManager);
}
