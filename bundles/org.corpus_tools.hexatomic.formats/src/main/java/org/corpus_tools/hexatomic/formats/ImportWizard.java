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

package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.corpus_tools.hexatomic.formats.exb.ExbImportConfiguration;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.core.PepperJobImpl;
import org.corpus_tools.pepper.modules.DocumentController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.Import);
  private final ImporterSelectionPage importerPage = new ImporterSelectionPage();
  private Optional<ConfigurationPage> configPage = Optional.empty();

  private final ErrorService errorService;
  private final ProjectManager projectManager;

  protected ImportWizard(ErrorService errorService, ProjectManager projectManager) {
    super();
    this.errorService = errorService;
    this.projectManager = projectManager;
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
  public IWizardPage getNextPage(IWizardPage page) {

    if (page == corpusPathPage) {
      Optional<File> corpusPath = corpusPathPage.getCorpusPath();
      if (corpusPath.isPresent()) {
        // Update the importer page with the new current path
        importerPage.updateRecommendFormats(corpusPath.get());
      }
    }

    if (page == importerPage && importerPage.getSelectedFormat().isPresent()) {
      // Add a configuration page based on the selected importer
      switch(importerPage.getSelectedFormat().get()) {
        case Exmaralda:
          ExbImportConfiguration configPage = new ExbImportConfiguration();
          configPage.setWizard(this);
          this.configPage = Optional.of(configPage);
          return configPage;
        default:
          return null;
      }
    } else {
      return super.getNextPage(page);
    }
  }

  @Override
  public boolean performFinish() {
    Optional<File> corpusPath = corpusPathPage.getCorpusPath();
    Optional<Format> selectedFormat = importerPage.getSelectedFormat();
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
      if (configPage.isPresent()) {
        // add properties for the importer
        importStep.setProps(configPage.get().getConfiguration());
      }
      job.addStepDesc(importStep);

      try {
        // Execute the conversion as task that can be aborted
        getContainer().run(true, true, (monitor) -> {

          monitor.beginTask("Importing corpus structure", IProgressMonitor.UNKNOWN);

          // Create a set of already finished documents
          Set<String> completedDocuments = new HashSet<>();

          // Run conversion in a background thread so we can add regular status reports
          Thread background = new Thread(job::convertFrom);
          background.start();

          Optional<Integer> numberOfJobs = Optional.empty();

          while (background.isAlive()) {
            if (monitor.isCanceled()) {
              background.interrupt();
            }
            // Check if any new document has been finished
            if (job instanceof PepperJobImpl) {
              PepperJobImpl pepperJobImpl = (PepperJobImpl) job;

              JOB_STATUS jobStatus = pepperJobImpl.getStatus();
              if (!numberOfJobs.isPresent()
                  && jobStatus == JOB_STATUS.IMPORTING_DOCUMENT_STRUCTURE) {
                // We don't know how many documents are present yet but since importing the
                // documents has started, we can get this number
                numberOfJobs = Optional.of(pepperJobImpl.getDocumentControllers().size());
                monitor.beginTask("Importing " + numberOfJobs.get() + " documents",
                    numberOfJobs.get());
              }

              if (numberOfJobs.isPresent()) {
                for (DocumentController controller : pepperJobImpl.getDocumentControllers()) {
                  DOCUMENT_STATUS docStatus = controller.getGlobalStatus();
                  if (docStatus == DOCUMENT_STATUS.COMPLETED || docStatus == DOCUMENT_STATUS.DELETED
                      || docStatus == DOCUMENT_STATUS.FAILED) {
                    if (completedDocuments.add(controller.getGlobalId())) {
                      monitor.worked(1);
                    }
                  }
                }
              }
            }
            Thread.sleep(500);
          }
          // set the corpus as current project
          projectManager.setProject(job.getSaltProject());
          monitor.done();

        });

      } catch (InvocationTargetException ex) {
        errorService.handleException("Unexpected error when importing corpus: " + ex.getMessage(),
            ex, ImportWizard.class);
      } catch (InterruptedException ex) {
        Thread.interrupted();
      }

      return true;
    }
    return false;
  }

}
