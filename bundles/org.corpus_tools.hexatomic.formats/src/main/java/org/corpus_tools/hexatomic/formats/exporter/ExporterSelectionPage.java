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

import java.util.Optional;
import org.corpus_tools.hexatomic.formats.CorpusFormatSelectionPage;
import org.eclipse.jface.wizard.IWizardPage;

public class ExporterSelectionPage extends CorpusFormatSelectionPage<ExportFormat>
    implements IWizardPage {

  protected ExporterSelectionPage() {
    super("Select export format", true);
    setTitle("Select export format");
    setDescription(
        "Corpora are stored in specific formats and you need to select the requested one.");
  }

  @Override
  public Optional<ExportFormat> getSelectedFormat() {
    if (btnExb.getSelection()) {
      return Optional.of(ExportFormat.EXB);
    } else if (btnPaulaXml.getSelection()) {
      return Optional.of(ExportFormat.PAULA);
    }
    return Optional.empty();
  }
}
