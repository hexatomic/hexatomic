package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

public class SaveHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;



  /**
   * Saves the Salt project and all opened documents under its current location.
   * 
   * @param shell The user interface shell
   */
  @Execute
  public void execute(Shell shell) {
    projectManager.save();
  }

  @CanExecute
  public boolean canExecute() {
    return projectManager.getLocation().isPresent();
  }
}
