package org.corpus_tools.hexatomic.formats;

import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ImportHandler {

  @Execute
  protected static void execute(Shell shell, ErrorService errorService) {
    try {
      WizardDialog dialog = new WizardDialog(shell, new ImportWizard(errorService));
      dialog.open();
    } catch (Exception e) {
      errorService.handleException("Could not initialize Pepper modules", e, ImportHandler.class);
    }
  }
}
