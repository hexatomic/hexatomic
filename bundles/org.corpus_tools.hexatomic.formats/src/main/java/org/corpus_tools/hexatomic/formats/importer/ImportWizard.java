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

import com.google.common.base.Joiner;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.formats.Activator;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.corpus_tools.hexatomic.formats.exb.ExbImportConfiguration;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.core.PepperJobImpl;
import org.corpus_tools.pepper.modules.DocumentController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  private final class ImportRunner implements IRunnableWithProgress {
    private final PepperJob job;

    private ImportRunner(PepperJob job) {
      this.job = job;
    }

    @Override
    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException {

      monitor.beginTask("Importing corpus structure", IProgressMonitor.UNKNOWN);

      // Create a set of already finished documents
      Set<String> completedDocuments = new HashSet<>();

      // Run conversion in a background thread so we can add regular status reports
      ExecutorService serviceExec = Executors.newSingleThreadExecutor();
      Future<?> background = serviceExec.submit(job::convertFrom);

      Optional<Integer> numberOfJobs = Optional.empty();

      while (!background.isDone() && !background.isCancelled()) {
        if (monitor.isCanceled()) {
          // Cancel the Pepper job
          job.cancelConversion();
          return;
        }

        if (job instanceof PepperJobImpl) {
          PepperJobImpl pepperJobImpl = (PepperJobImpl) job;
          JOB_STATUS jobStatus = pepperJobImpl.getStatus();
          // Check if we can get the number of documents after the corpus structure has been
          // imported
          if (!numberOfJobs.isPresent() && jobStatus == JOB_STATUS.IMPORTING_DOCUMENT_STRUCTURE) {
            // We don't know how many documents are present previously but since importing the
            // documents has started, we can get this number
            numberOfJobs = Optional.of(pepperJobImpl.getDocumentControllers().size());
            monitor.beginTask("Importing " + numberOfJobs.get() + " documents", numberOfJobs.get());
          }

          if (numberOfJobs.isPresent()) {
            // Report detailed status of the conversion progress of the documents
            reportDocumentConversionProgress(pepperJobImpl, monitor, completedDocuments);
          }

        }
        Thread.sleep(1000);
      }
      monitor.done();

      try {
        background.get();
        if (handleConversionResult(job, monitor)) {
          sync.syncExec(() -> projectManager.setProject(job.getSaltProject()));
        }
      } catch (ExecutionException ex) {
        errorService.handleException(ERRORS_TITLE, ex, ImportWizard.class);
      }
    }

    private void reportDocumentConversionProgress(PepperJobImpl pepperJobImpl,
        IProgressMonitor monitor, Set<String> completedDocuments) {

      Set<DocumentController> activeDocuments =
          new LinkedHashSet<>(pepperJobImpl.getActiveDocuments());
      if (!activeDocuments.isEmpty()) {
        monitor.subTask(Joiner.on(", ")
            .join(activeDocuments.stream().map(d -> d.getDocument().getName()).iterator()));
      }

      for (DocumentController controller : pepperJobImpl.getDocumentControllers()) {
        DOCUMENT_STATUS docStatus = controller.getGlobalStatus();

        if ((docStatus == DOCUMENT_STATUS.COMPLETED || docStatus == DOCUMENT_STATUS.DELETED
            || docStatus == DOCUMENT_STATUS.FAILED)
            && completedDocuments.add(controller.getGlobalId())) {
          monitor.worked(1);
        }
      }
    }
  }

  private static final String ERRORS_TITLE = "Error(s) during import";
  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.IMPORT);
  private final ImporterSelectionPage importerPage = new ImporterSelectionPage();
  private Optional<ConfigurationPage> configPage = Optional.empty();

  private final ErrorService errorService;
  private final ProjectManager projectManager;
  private final SaltNotificationFactory notificationFactory;
  private final UISynchronize sync;

  protected ImportWizard(ErrorService errorService, ProjectManager projectManager,
      SaltNotificationFactory notificationFactory, UISynchronize sync) {
    super();
    this.errorService = errorService;
    this.projectManager = projectManager;
    this.notificationFactory = notificationFactory;
    this.sync = sync;
    setNeedsProgressMonitor(true);
  }

  @Override
  public String getWindowTitle() {
    return "IMPORT a corpus project from a different file format";
  }

  @Override
  public void addPages() {
    addPage(corpusPathPage);
    addPage(importerPage);
  }

  @Override
  public IWizardPage getNextPage(IWizardPage page) {

    if (page == corpusPathPage) {
      Optional<File> corpusPath = corpusPathPage.getCorpusPath();
      if (corpusPath.isPresent()) {
        // Update the importer page with the new current path
        importerPage.updateRecommendFormats(corpusPath.get());
      }
    }

    if (page == importerPage) {
      Optional<ImportFormat> selectedFormat = importerPage.getSelectedFormat();
      if (selectedFormat.isPresent()) {
        // Add a configuration page based on the selected importer
        if (selectedFormat.get() == ImportFormat.EXB) {
          ExbImportConfiguration exbConfigPage = new ExbImportConfiguration();
          exbConfigPage.setWizard(this);
          this.configPage = Optional.of(exbConfigPage);
          return exbConfigPage;
        } else {
          return null;
        }
      }
    }
    return super.getNextPage(page);
  }

  @Override
  public boolean performFinish() {
    Optional<File> corpusPath = corpusPathPage.getCorpusPath();
    Optional<ImportFormat> selectedFormat = importerPage.getSelectedFormat();
    Optional<Pepper> pepper = Activator.getPepper();
    if (corpusPath.isPresent() && selectedFormat.isPresent() && pepper.isPresent()) {
      // Limit the maximum number of parallel processed documents
      pepper.get().getConfiguration().setProperty(PepperConfiguration.PROP_MAX_AMOUNT_OF_SDOCUMENTS,
          "2");

      // Create the import specification
      StepDesc importStep = selectedFormat.get().createJobSpec();
      // Set the path to the selected directory
      CorpusDesc corpusDesc = new CorpusDesc();
      corpusDesc.setCorpusPath(URI.createFileURI(corpusPath.get().getAbsolutePath()));
      importStep.setCorpusDesc(corpusDesc);
      if (configPage.isPresent()) {
        // add properties for the importer
        importStep.setProps(configPage.get().getConfiguration());
      }

      String jobId = pepper.get().createJob();
      PepperJob job = pepper.get().getJob(jobId);
      job.addStepDesc(importStep);

      // Conversion is adding a load of events, suppress them first
      notificationFactory.setSuppressingEvents(true);

      try {
        // Execute the conversion as task that can be aborted
        getContainer().run(true, true, new ImportRunner(job));

      } catch (InvocationTargetException ex) {
        errorService.handleException("Unexpected error when importing corpus: " + ex.getMessage(),
            ex, ImportWizard.class);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } finally {
        notificationFactory.setSuppressingEvents(false);
      }

      return true;
    }
    return false;
  }

  private boolean handleConversionResult(PepperJob job, IProgressMonitor monitor) {
    if (!monitor.isCanceled()) {
      // Check if the whole conversion was marked as error.
      if (job.getStatus() == JOB_STATUS.ENDED_WITH_ERRORS) {
        errorService.showError(ERRORS_TITLE, "IMPORT was not successful for unknown reasons. "
            + "Please check the log messages for any issues.", ImportWizard.class);
      } else if (!monitor.isCanceled()) {
        // Check for any documents with errors and report them
        // error.
        Set<String> failedDocuments = new TreeSet<>();
        if (job instanceof PepperJobImpl) {
          PepperJobImpl pepperJobImpl = (PepperJobImpl) job;
          for (DocumentController docController : pepperJobImpl.getDocumentControllers()) {

            DOCUMENT_STATUS status = docController.getGlobalStatus();
            if (status != DOCUMENT_STATUS.COMPLETED) {
              failedDocuments.add(docController.getGlobalId());
            }
          }
        }
        if (!failedDocuments.isEmpty()) {
          errorService.showError(ERRORS_TITLE,
              "Could not import the following documents:\n\n"
                  + Joiner.on("\n").join(failedDocuments)
                  + "\n\nPlease check the log messages for any issues.",
              ImportWizard.class);
        }

        // No global error was reported and the process was not cancelled: set the corpus as
        // current project
        return true;
      }
    }
    return false;
  }

}
