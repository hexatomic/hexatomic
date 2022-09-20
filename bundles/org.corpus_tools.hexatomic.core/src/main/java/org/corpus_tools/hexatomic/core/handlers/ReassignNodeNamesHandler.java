package org.corpus_tools.hexatomic.core.handlers;

import org.corpus_tools.hexatomic.core.ui.ReassignNodeNamesDialog;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

public class ReassignNodeNamesHandler {

  @Execute
  protected void execute(Shell shell) {
    ReassignNodeNamesDialog dialog = new ReassignNodeNamesDialog(shell);
    dialog.open();
  }
  
}
