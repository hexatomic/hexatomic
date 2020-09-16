package org.corpus_tools.hexatomic.formats.exporter;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.Activator;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ExportHandler {

  @Execute
  protected static void execute(Shell shell, ErrorService errorService) {
    WizardDialog dialog = new WizardDialog(shell,
        new ExportWizard());
    dialog.open();
  }

  @CanExecute
  protected static boolean canExecute(ProjectManager projectManager) {
    return Activator.getPepper().isPresent()
        && !projectManager.getProject().getCorpusGraphs().isEmpty();
  }

}
