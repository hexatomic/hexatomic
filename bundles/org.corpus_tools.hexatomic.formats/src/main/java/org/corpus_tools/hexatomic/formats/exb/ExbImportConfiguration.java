/*-
 * #%L
 * org.corpus_tools.hexatomic.formats
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.formats.exb;

import java.util.Properties;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.corpus_tools.hexatomic.formats.importer.ImportFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExbImportConfiguration extends ConfigurationPage {

  private Button btnAddSpaces;

  /**
   * Constructs a new import configuration for the {@link ImportFormat#Exmaralda} format.
   */
  public ExbImportConfiguration() {
    super("Configure import");
    setTitle("Configure EXMARaLDA import");
    setDescription("You can leave the default values or customize the import process.");
  }

  @Override
  public void createControl(Composite parent) {
    setPageComplete(false);

    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    btnAddSpaces = new Button(container, SWT.CHECK);
    btnAddSpaces.setText("Add spaces between token");
    btnAddSpaces.setSelection(true);
    btnAddSpaces.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
  }

  @Override
  public Properties getConfiguration() {
    Properties result = new Properties();

    if (btnAddSpaces == null || btnAddSpaces.getSelection()) {
      result.setProperty("salt.tokenSeparator", "\" \"");
    }

    return result;
  }


}
