package org.corpus_tools.hexatomic.formats;

import java.util.Properties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

public abstract class ConfigurationPage extends WizardPage {

  protected ConfigurationPage(String pageName) {
    super(pageName);
  }

  protected ConfigurationPage(String pageName, String title, ImageDescriptor titleImage) {
    super(pageName, title, titleImage);
  }

  public abstract Properties getConfiguration();

}
