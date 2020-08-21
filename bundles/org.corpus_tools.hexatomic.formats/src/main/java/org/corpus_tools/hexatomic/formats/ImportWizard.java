package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.Import);
  private final ImporterSelectionPage importerPage = new ImporterSelectionPage();

  private final ErrorService errorService;

  public ImportWizard(ErrorService errorService) {
    super();
    this.errorService = errorService;
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
    Optional<ImportFormat> selectedFormat = importerPage.getSelectedFormat();
    Optional<Pepper> pepper = Activator.getPepper();
    if (corpusPath.isPresent() && selectedFormat.isPresent() && pepper.isPresent()) {
      String jobId = pepper.get().createJob();
      PepperJob job = pepper.get().getJob(jobId);

      // Create the import specification
      StepDesc importStep = selectedFormat.get().createJobSpec();
      // Set the path to the selected directory
      CorpusDesc corpusDesc = new CorpusDesc();
      corpusDesc.setCorpusPath(URI.createFileURI(corpusPath.get().getAbsolutePath()));
      importStep.setCorpusDesc(corpusDesc);
      // TODO: add properties for the importer
      job.addStepDesc(importStep);

      // Create the output step
      StepDesc exportStep = new StepDesc();
      exportStep.setModuleType(MODULE_TYPE.EXPORTER);
      exportStep.setName("SaltXMLExporter");
      CorpusDesc exportCorpusDesc = new CorpusDesc();
      Path exportTmpPath;
      try {
        exportTmpPath = Files.createTempDirectory("hexatomic-imported-corpus");
        exportCorpusDesc.setCorpusPath(URI.createFileURI(exportTmpPath.toString()));
        exportStep.setCorpusDesc(exportCorpusDesc);
        job.addStepDesc(exportStep);

        // Execute the conversion as task that can be aborted
        getContainer().run(true, true, (monitor) -> {
          job.convert();

          // TODO: set the corpus as current project
        });

      } catch (IOException ex) {
        errorService.handleException("Could not create temporary directory for imported corpus", ex,
            ImportWizard.class);
      } catch (InvocationTargetException ex) {
        errorService.handleException("Unexpected error when importing corpus: " + ex.getMessage(), ex,
            ImportWizard.class);
      } catch (InterruptedException ex) {
        Thread.interrupted();
      }

      return true;
    }
    return false;
  }

}
