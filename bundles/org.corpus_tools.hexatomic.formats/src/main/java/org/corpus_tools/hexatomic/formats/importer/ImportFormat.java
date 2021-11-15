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

package org.corpus_tools.hexatomic.formats.importer;

import java.util.Optional;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;

public enum ImportFormat {
  EXB("EXMARaLDAImporter"), PAULA("PAULAImporter"), TXT("TextImporter"), GRAPHANNO(
      "GraphAnnoImporter");

  private final String importerName;

  ImportFormat(String importerName) {
    this.importerName = importerName;
  }

  protected StepDesc createJobSpec() {
    StepDesc result = new StepDesc();

    result.setModuleType(MODULE_TYPE.IMPORTER);
    result.setName(this.importerName);

    return result;
  }

  protected static Optional<ImportFormat> getFormatByName(String name) {
    for (ImportFormat f : ImportFormat.values()) {
      if (f.importerName.equals(name)) {
        return Optional.of(f);
      }
    }
    return Optional.empty();
  }
}

