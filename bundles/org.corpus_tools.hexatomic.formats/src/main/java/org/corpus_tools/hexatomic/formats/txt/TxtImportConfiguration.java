package org.corpus_tools.hexatomic.formats.txt;

import java.util.Properties;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.corpus_tools.pepper.modules.coreModules.TextImporter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Configuration for the wizard page that lets users configure the {@link TextImporter}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class TxtImportConfiguration extends ConfigurationPage {

  private Button btnTokenize = null;

  /**
   * Constructs a new import configuration for the {@link ImportFormat#TXT} format.
   */
  public TxtImportConfiguration() {
    super("Configure import");
    setTitle("Configure plain text import");
    setDescription("You can leave the default values or customize the import process.");
  }

  @Override
  public void createControl(Composite parent) {
    setPageComplete(false);

    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    btnTokenize = new Button(container, SWT.CHECK);
    btnTokenize.setText("Tokenize after import");
    btnTokenize.setSelection(true);
    btnTokenize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
  }

  @Override
  public Properties getConfiguration() {
    Properties result = new Properties();

    if (btnTokenize == null || btnTokenize.getSelection()) {
      result.setProperty("pepper.after.tokenize", "true");
    }

    return result;
  }

}
