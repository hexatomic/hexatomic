package org.corpus_tools.hexatomic.formats.exb;

import java.util.Properties;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExbImportConfiguration extends ConfigurationPage {

  private Button btnCheckButton;

  public ExbImportConfiguration() {
    super("Configure import");
    setTitle("Configure EXMARaLDA import");
    setDescription(
        "You can leave the default values or customize the import process.");
  }

  @Override
  public void createControl(Composite parent) {
    setPageComplete(false);

    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    btnCheckButton = new Button(container, SWT.CHECK);
    btnCheckButton.setText("Add spaces between token");
    btnCheckButton.setSelection(true);
    btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
  }

  @Override
  public Properties getConfiguration() {
    Properties result = new Properties();
    
    if (btnCheckButton.getSelection()) {
      result.setProperty("salt.tokenSeparator", "\" \"");
    }
    
    return result;
  }


}
