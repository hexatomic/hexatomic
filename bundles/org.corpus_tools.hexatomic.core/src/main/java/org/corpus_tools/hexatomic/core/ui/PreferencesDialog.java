package org.corpus_tools.hexatomic.core.ui;

import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.equinox.p2.ui.RepositoryManipulationPage;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PreferencesDialog extends TitleAreaDialog {
  private RepositoryManipulationPage page;

  /**
   * Create the dialog.
   * 
   * @param parentShell The parent.
   */
  public PreferencesDialog(Shell parentShell) {
    super(parentShell);
  }

  @Override
  public void create() {
    super.create();
    setTitle("This is my first custom dialog");
    setMessage("This is a TitleAreaDialog");
  }

  /**
   * Create contents of the dialog.
   * 
   * @param parent The parent
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    GridLayout layout = new GridLayout(2, false);
    container.setLayout(layout);
    Label lblVersion = new Label(container, SWT.NONE);
    lblVersion.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    lblVersion.setText("Version: ");
    

    page = new RepositoryManipulationPage();
    page.setProvisioningUI(ProvisioningUI.getDefaultUI());
    page.setVisible(true);
    //page.setContainer(container);
    //page.createControl(container);
    //return page.getControl();
    return container;
  }

  protected void okPressed() {
    if (page.performOk()) {
      super.okPressed();
    }
  }

  protected void cancelPressed() {
    if (page.performCancel()) {
      super.cancelPressed();
    }
  }

}

