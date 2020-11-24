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
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.PepperJobRunner;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.common.PepperUtil;
import org.corpus_tools.pepper.common.StepDesc;
import org.corpus_tools.pepper.modules.coreModules.DoNothingExporter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;

class ImportRunner extends PepperJobRunner {

  private final ProjectManager projectManager;
  private final ErrorService errorService;
  private final UISynchronize sync;

  ImportRunner(PepperJob job, ProjectManager projectManager, ErrorService errorService,
      UISynchronize sync) {
    super(job);
    this.projectManager = projectManager;
    this.errorService = errorService;
    this.sync = sync;
  }


  @Override
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

    monitor.beginTask("Importing corpus structure", IProgressMonitor.UNKNOWN);

    // Add a dummy export step so we can read the imported result
    getJob().addStepDesc(new StepDesc().setName(DoNothingExporter.MODULE_NAME)
        .setModuleType(MODULE_TYPE.EXPORTER).setCorpusDesc(new CorpusDesc()
            .setCorpusPath(URI.createFileURI(PepperUtil.getTempFile().getAbsolutePath()))));

    try {
      runJob(monitor);
      if (handleConversionResult(monitor)) {

        if (!getJob().getStepDescs().isEmpty()) {
          // Add a feature annotation with the original location to each corpus graph
          URI importLocation = getJob().getStepDescs().get(0).getCorpusDesc().getCorpusPath();
          SaltHelper.setOriginalCorpusLocation(getJob().getSaltProject(),
              importLocation.toFileString(), false);
        }

        // Set the imported project in the project manager
        sync.syncExec(() -> projectManager.setProject(getJob().getSaltProject()));
      }
    } catch (ExecutionException ex) {
      errorService.handleException(ImportWizard.ERRORS_TITLE, ex, ImportWizard.class);
    }
  }


  private boolean handleConversionResult(IProgressMonitor monitor) {
    if (!monitor.isCanceled()) {
      // Check if the whole conversion was marked as error.
      if (getJob().getStatus() == JOB_STATUS.ENDED_WITH_ERRORS) {
        errorService.showError(ImportWizard.ERRORS_TITLE,
            "Import was not successful for unknown reasons. "
                + "Please check the log messages for any issues.",
            ImportWizard.class);
      } else if (!monitor.isCanceled()) {
        // Check for any documents with errors and report them
        // error.
        Set<String> failedDocuments = getFailedDocuments();
        if (!failedDocuments.isEmpty()) {
          errorService.showError(ImportWizard.ERRORS_TITLE,
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
