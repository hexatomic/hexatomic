package org.corpus_tools.hexatomic.formats;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ImportHandler {

	@Execute
	protected void execute(Shell shell) {
		WizardDialog dialog = new WizardDialog(shell, new ImportWizard());
		dialog.open();
	}
}
