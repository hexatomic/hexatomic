package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.core.PepperImpl;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.Import);
  private final ImporterSelectionPage importerPage = new ImporterSelectionPage();

  public ImportWizard(ErrorService errorService) {
    super();
    setNeedsProgressMonitor(true);

    PepperConfiguration pepperConf = new PepperConfiguration();
    Pepper pepper = new PepperImpl();
    pepper.setConfiguration(pepperConf);

  }

  @Override
  public String getWindowTitle() {
    return "Import a corpus project from a different file format";
  }

  @Override
  public void addPages() {
    addPage(corpusPathPage);
    addPage(importerPage);

  }

  @Override
  public boolean performFinish() {
    Optional<File> corpusPath = corpusPathPage.getCorpusPath();
    Optional<Format> selectedFormat = importerPage.getSelectedFormat();
    if (corpusPath.isPresent() && selectedFormat.isPresent()) {
      PepperImportWorkflow workflow = new PepperImportWorkflow();
      workflow.convert();
      return true;
    }
    return false;
  }

}
