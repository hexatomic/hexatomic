package org.corpus_tools.hexatomic.formats;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ImporterSelectionPage extends WizardPage implements IWizardPage {

  protected ImporterSelectionPage() {
    super("Select import format");
    setTitle("Select import format");
    setDescription(
        "Corpora are stored in specific formats and you need to select the correct one.");
  }

  @Override
  public void createControl(Composite parent) {

    setPageComplete(false);

    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    Button btnRadioButton = new Button(container, SWT.RADIO);
    btnRadioButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPageComplete(true);
      }
    });
    btnRadioButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnRadioButton.setBounds(0, 0, 112, 17);
    btnRadioButton.setText("EXMARaLDA format (*.exb)");
  }
}
