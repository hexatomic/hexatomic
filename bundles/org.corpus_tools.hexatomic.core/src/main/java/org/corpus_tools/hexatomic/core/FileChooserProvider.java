package org.corpus_tools.hexatomic.core;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A class that can be injected to create a {@link FileDialog} or {@link DirectoryDialog}. <br />
 * <br />
 * Having this as an extra class allows to inject custom dialog providers, e.g. for mocked dialogs
 * in tests.
 *
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
public class FileChooserProvider {

  /**
   * Creates a new file chooser dialog.
   * 
   * @param shell The SWT shell to use as parent
   * @return The created dialog.
   */
  public FileDialog createFileDialog(Shell shell) {
    return new FileDialog(shell);
  }

  /**
   * Creates a new directory dialog.
   * 
   * @param shell The SWT shell to use as parent
   * @return The created dialog.
   */
  public DirectoryDialog createDirectoryDialog(Shell shell) {
    return new DirectoryDialog(shell);
  }
}
