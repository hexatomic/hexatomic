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
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;

public enum ExportFormat {
  Exmaralda("EXMARaLDAExporter"), PaulaXML("PAULAExporter");

  private final String exportName;

  ExportFormat(String exportName) {
    this.exportName = exportName;
  }

  protected StepDesc createJobSpec() {
    StepDesc result = new StepDesc();

    result.setModuleType(MODULE_TYPE.EXPORTER);
    result.setName(this.exportName);

    return result;
  }

  protected static Optional<ExportFormat> getFormatByName(String name) {
    for (ExportFormat f : ExportFormat.values()) {
      if (f.exportName.equals(name)) {
        return Optional.of(f);
      }
    }
    return Optional.empty();
  }
}

