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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.formats.Activator;
import org.corpus_tools.hexatomic.formats.ConfigurationPage;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage;
import org.corpus_tools.hexatomic.formats.CorpusPathSelectionPage.Type;
import org.corpus_tools.hexatomic.formats.exb.ExbImportConfiguration;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ImportWizard extends Wizard {

  static final String ERRORS_TITLE = "Error(s) during import";
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

    corpusPathPage.setCorpusPathFromProject(projectManager.getProject());
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
        getContainer().run(true, true, new ImportRunner(job, projectManager, errorService, sync));

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
}
