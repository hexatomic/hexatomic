package org.corpus_tools.hexatomic.formats;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A class that can be injected to create a {@link WizardDialog}. <br />
 * <br />
 * Having this as an extra class allows to inject custom dialog providers, e.g. for mocked dialogs
 * in tests.
 *
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
public class WizardDialogProvider {

  /**
   * Creates a new wizard dialog.
   * 
   * @param shell The SWT shell to use as parent
   * @param exportWizard The acual export wizard instance.
   * @return The created dialog.
   */
  public WizardDialog createDialog(Shell shell, IWizard exportWizard) {
    return new WizardDialog(shell, exportWizard);
  }

}
