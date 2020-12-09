package org.corpus_tools.hexatomic.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

  private Font headerFont;
  private Font boldFont;


  /**
   * Create the dialog.
   * 
   * @param parentShell The parent
   */
  public AboutDialog(Shell parentShell) {
    super(parentShell);
  }

  /**
   * Create contents of the dialog.
   * 
   * @param parent The parent
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);

    Label lblHeader = new Label(container, SWT.NONE);
    lblHeader.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
    lblHeader.setText("Hexatomic");
    FontDescriptor headerFontDescriptor =
        FontDescriptor.createFrom(lblHeader.getFont()).setHeight(32);
    headerFont = headerFontDescriptor.createFont(lblHeader.getDisplay());
    lblHeader.setFont(headerFont);

    Label lblVersion = new Label(container, SWT.NONE);
    lblVersion.setText("Version: <unknown>");
    FontDescriptor boldFontDescriptor =
        FontDescriptor.createFrom(lblVersion.getFont()).setStyle(SWT.BOLD);
    boldFont = boldFontDescriptor.createFont(lblVersion.getDisplay());
    lblVersion.setFont(boldFont);

    Link lnkAuthors = new Link(container, SWT.NONE);
    lnkAuthors.setText(
        "2018ff. <a>Hexatomic project team</a>");
    lnkAuthors.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch("https://github.com/orgs/hexatomic/teams/project/members");
      }
    });

    Link lnkHomepage = new Link(container, SWT.NONE);
    lnkHomepage.setText("Homepage: <a>https://hexatomic.github.io/</a>");
    lnkHomepage.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Program.launch("https://hexatomic.github.io/");
      }
    });


    return container;
  }

  /**
   * Create contents of the button bar.
   * 
   * @param parent The parent
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize() {
    return new Point(450, 300);
  }


}
