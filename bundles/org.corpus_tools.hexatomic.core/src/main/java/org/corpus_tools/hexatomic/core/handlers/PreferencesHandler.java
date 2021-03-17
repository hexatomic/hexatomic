package org.corpus_tools.hexatomic.core.handlers;

import org.corpus_tools.hexatomic.core.ui.PreferencesDialog;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

public class PreferencesHandler {
  @Execute
  protected void execute(Shell shell) {
    PreferencesDialog dialog = new PreferencesDialog(shell);
    dialog.open();
  }

}
