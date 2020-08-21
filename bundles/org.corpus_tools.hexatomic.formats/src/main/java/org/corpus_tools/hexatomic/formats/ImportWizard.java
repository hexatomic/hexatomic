package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.util.Optional;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.Import);
  private final ImporterSelectionPage importerPage = new ImporterSelectionPage();

  public ImportWizard() {
    super();
    setNeedsProgressMonitor(true);
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

    }
    return false;
  }

}
