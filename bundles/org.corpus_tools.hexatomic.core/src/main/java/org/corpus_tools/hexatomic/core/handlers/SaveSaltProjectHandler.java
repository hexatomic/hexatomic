package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class SaveSaltProjectHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;

  private String lastPath;


  /**
   * Saves the Salt project and all opened documents.
   * 
   * @param shell The user interface shell
   * @param location If non-null, save the project to this location. If null, use the original
   *        location from where the project was loaded.
   */
  @Execute
  public void execute(Shell shell, @Optional @Named(CommandParams.LOCATION) String location) {

    String resultPath;
    if (location == null) {
      DirectoryDialog dialog = new DirectoryDialog(shell);
      
      if (lastPath != null) {
        dialog.setFilterPath(lastPath);
      }
      // Ask the user to choose a path
      resultPath = dialog.open();
    } else {
      // Use the given argument as location
      resultPath = location;
    }

    if (resultPath == null) {
      projectManager.save();
    } else {
      projectManager.saveTo(URI.createFileURI(resultPath));
      lastPath = resultPath;
    }
  }
}
