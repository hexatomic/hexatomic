package org.corpus_tools.hexatomic.formats;

import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.StepDesc;

public enum ImportFormat {
  Exmaralda("EXMARaLDAImporter");

  private final String importerName;

  ImportFormat(String importerName) {
    this.importerName = importerName;
  }

  public StepDesc createJobSpec() {
    StepDesc result = new StepDesc();
    
    result.setModuleType(MODULE_TYPE.IMPORTER);
    result.setName(this.importerName);
    
    return result;
  }
}

