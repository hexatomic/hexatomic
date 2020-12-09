package org.corpus_tools.hexatomic.core.handlers;

import org.corpus_tools.hexatomic.core.ui.AboutDialog;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

public class AboutHandler {


  @Execute
  protected void execute(Shell shell) {
    AboutDialog dialog = new AboutDialog(shell);
    dialog.open();
  }
}
