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
import org.corpus_tools.hexatomic.formats.PepperWizard;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.StepDesc;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;

/**
 * Wizard to export the Salt project to other formats using Pepper.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public class ExportWizard extends PepperWizard {

  static final String ERRORS_TITLE = "Error(s) during export";

  private final CorpusPathSelectionPage corpusPathPage = new CorpusPathSelectionPage(Type.EXPORT);
  private final ExporterSelectionPage exporterPage = new ExporterSelectionPage();
  private Optional<ConfigurationPage> configPage = Optional.empty();


  protected ExportWizard(ErrorService errorService, ProjectManager projectManager,
      SaltNotificationFactory notificationFactory, UISynchronize sync) {
    super(errorService, projectManager, notificationFactory, sync);
    corpusPathPage.setCorpusPathFromProject(projectManager.getProject());
  }

  @Override
  public String getWindowTitle() {
    return "Export a corpus project to a different file format";
  }

  @Override
  public void addPages() {
    addPage(corpusPathPage);
    addPage(exporterPage);
  }

  @Override
  public boolean performFinish() {
    Optional<File> corpusPath = corpusPathPage.getCorpusPath();
    Optional<ExportFormat> selectedFormat = exporterPage.getSelectedFormat();
    Optional<Pepper> pepper = Activator.getPepper();
    Optional<URI> projectLocation = getProjectManager().getLocation();
    if (corpusPath.isPresent() && selectedFormat.isPresent() && pepper.isPresent()
        && projectLocation.isPresent()) {
      // Add an import step for the Salt corpus (on-disk)
      StepDesc importStep = new StepDesc();
      importStep.setModuleType(MODULE_TYPE.IMPORTER);
      importStep.setName("SaltXMLImporter");
      CorpusDesc importCorpusDesc = new CorpusDesc();
      importCorpusDesc.setCorpusPath(projectLocation.get());
      importStep.setCorpusDesc(importCorpusDesc);

      // Create the export specification
      StepDesc exportStep = selectedFormat.get().createJobSpec();
      // Set the path to the selected directory
      CorpusDesc exportCorpusDesc = new CorpusDesc();
      exportCorpusDesc.setCorpusPath(URI.createFileURI(corpusPath.get().getAbsolutePath()));
      exportStep.setCorpusDesc(exportCorpusDesc);
      if (configPage.isPresent()) {
        // add properties for the exporter
        exportStep.setProps(configPage.get().getConfiguration());
      }

      String jobId = pepper.get().createJob();
      PepperJob job = pepper.get().getJob(jobId);
      job.addStepDesc(importStep);
      job.addStepDesc(exportStep);

      // Conversion is adding a load of events, suppress them first
      getNotificationFactory().setSuppressingEvents(true);

      try {
        // Execute the conversion as task that can be aborted
        getContainer().run(true, true,
            new ExportRunner(job, getProjectManager(), getErrorService(), getSync()));

      } catch (InvocationTargetException ex) {
        getErrorService().handleException(
            "Unexpected error when exporting corpus: " + ex.getMessage(), ex, ExportWizard.class);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } finally {
        getNotificationFactory().setSuppressingEvents(false);
      }
      return true;
    }
    return false;
  }

}
