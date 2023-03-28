/*-
 * #%L
 * [bundle] Corpus file formats
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.formats.txt;

import java.util.Properties;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.corpus_tools.hexatomic.formats.importer.ImportFormat;
import org.corpus_tools.pepper.modules.coreModules.TextImporter;
import org.eclipse.swt.SWT;
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
