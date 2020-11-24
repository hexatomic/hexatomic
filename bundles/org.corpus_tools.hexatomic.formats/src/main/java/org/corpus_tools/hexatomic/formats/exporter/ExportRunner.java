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

import com.google.common.base.Joiner;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.PepperJobRunner;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.PepperJob;
import org.eclipse.core.runtime.IProgressMonitor;

class ExportRunner extends PepperJobRunner {

  private final ErrorService errorService;


  ExportRunner(PepperJob job, ErrorService errorService) {
    super(job);
    this.errorService = errorService;
  }

  @Override
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

    monitor.beginTask("Exporting corpus structure", IProgressMonitor.UNKNOWN);

    try {
      runJob(monitor);
      handleConversionResult(monitor);

    } catch (ExecutionException ex) {
      errorService.handleException(ExportWizard.ERRORS_TITLE, ex, ExportWizard.class);
    }
  }


  private void handleConversionResult(IProgressMonitor monitor) {
    if (!monitor.isCanceled()) {
      // Check if the whole conversion was marked as error.
      if (getJob().getStatus() == JOB_STATUS.ENDED_WITH_ERRORS) {
        errorService.showError(ExportWizard.ERRORS_TITLE,
            "Export was not successful for unknown reasons. "
                + "Please check the log messages for any issues.",
            ExportWizard.class);
      } else if (!monitor.isCanceled()) {
        // Check for any documents with errors and report them
        // error.
        Set<String> failedDocuments = getFailedDocuments();
        if (!failedDocuments.isEmpty()) {
          errorService.showError(ExportWizard.ERRORS_TITLE,
              "Could not export the following documents:\n\n"
                  + Joiner.on("\n").join(failedDocuments)
                  + "\n\nPlease check the log messages for any issues.",
              ExportWizard.class);
        }
      }
    }
  }
}
