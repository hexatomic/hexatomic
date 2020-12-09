package org.corpus_tools.hexatomic.core.handlers;

import org.corpus_tools.hexatomic.core.ui.AboutDialog;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

public class OpenOnlineDocumentationHandler {
  @Execute
  protected void execute(Shell shell) {
    Program.launch(AboutDialog.getOnlineDocumentationUrl());
  }
}
