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

package org.corpus_tools.hexatomic.formats.exporter;

import java.io.File;
import java.util.Optional;
import org.corpus_tools.hexatomic.formats.importer.ImportFormat;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExporterSelectionPage extends WizardPage implements IWizardPage {


  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ExporterSelectionPage.class);

  private Button btnExb;

  private Button btnPaulaXml;

  protected ExporterSelectionPage() {
    super("Select export format");
    setTitle("Select export format");
    setDescription(
        "Corpora are stored in specific formats and you need to select the requested one.");
  }

  protected void updateRecommendFormats(File corpusPath) {

    // Initialize selection to default
    btnExb.setSelection(false);
    btnPaulaXml.setSelection(false);
  }


  @Override
  public void createControl(Composite parent) {

    setPageComplete(false);

    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    SelectionAdapter checkboxSelectionAdapter = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setPageComplete(getSelectedFormat().isPresent());
      }
    };

    btnExb = new Button(container, SWT.RADIO);
    btnExb.addSelectionListener(checkboxSelectionAdapter);
    btnExb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnExb.setBounds(0, 0, 112, 17);
    btnExb.setText("EXMARaLDA format (*.exb)");

    btnPaulaXml = new Button(container, SWT.RADIO);
    btnPaulaXml.addSelectionListener(checkboxSelectionAdapter);
    btnPaulaXml.setText("PAULA format");
  }

  protected Optional<ImportFormat> getSelectedFormat() {
    if (btnExb.getSelection()) {
      return Optional.of(ImportFormat.EXB);
    } else if (btnPaulaXml.getSelection()) {
      return Optional.of(ImportFormat.PAULA);
    }
    return Optional.empty();
  }
}
