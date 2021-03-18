package org.corpus_tools.hexatomic.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PreferencesDialog extends Dialog {
  private static boolean updateEnabled = true;
  
  /**
   * Create the dialog.
   * 
   * @param parentShell The parent.
   */
  public PreferencesDialog(Shell parentShell) {
    super(parentShell);
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Enable Startup-Checks");
  }
  
  /**
   * Create contents of the dialog.
   * 
   * @param parent The parent
   */

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Label label = new Label(area, SWT.BORDER);
    label.setText("When checked Hexatomic will search for p2-Updates at each startup");
    //label.setToolTipText("This is the tooltip of this label");
    Button button =  new Button(area, SWT.CHECK);
    button.setText("Enable automatic update search at startup");

    //register listener for the selection event
    button.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            System.out.println("Yes!");
            setUpdate_enabled(true);
        }
    });
    return area;
  }

  public static boolean isUpdate_enabled() {
    return updateEnabled;
  }

  public static void setUpdate_enabled(boolean updateEnabled) {
    PreferencesDialog.updateEnabled = updateEnabled;
  }

}

