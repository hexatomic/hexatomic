package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class UndoHandler {

  @Inject
  private ProjectManager projectManager;

  @Execute
  public void execute() {
    projectManager.undo();
  }

  @CanExecute
  public boolean canExecute() {
    return projectManager.canUndo();
  }

}
