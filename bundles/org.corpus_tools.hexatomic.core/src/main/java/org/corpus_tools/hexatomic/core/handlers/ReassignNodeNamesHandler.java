package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ReassignNodeNamesHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;



  @Execute
  protected void execute(Shell shell) {
    boolean performReassign = MessageDialog.openQuestion(shell, "Re-assign Node Names",
        "Do you want to assign automatically generated names to all nodes and token? "
            + "This will be applied to all documents in all opened corpora. "
            + "Token will get the prefix \"t\" and a number (e.g. \"t1\") and "
            + "other nodes the prefix \"n\" and a number (e.g. \"n2\").");
    if (performReassign) {
      MessageDialog.openInformation(shell, "Info", "Not implemented yet");

    }
  }

  @CanExecute
  public boolean canExecute() {
    return projectManager.getProject().getCorpusGraphs().stream()
        .anyMatch(cg -> !cg.getDocuments().isEmpty());
  }

}
