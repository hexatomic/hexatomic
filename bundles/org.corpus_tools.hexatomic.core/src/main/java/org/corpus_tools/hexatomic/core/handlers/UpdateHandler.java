/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause, Clara Lachenmaier
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

package org.corpus_tools.hexatomic.core.handlers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;

public class UpdateHandler {
  /**
   * Search for updates and update if wanted.
   * 
   * @param agent OSGi service needed for creating an update operation
   * @param sync Helper class for executing code in the UI thread
   * @param workbench The current workbench. Will be needed to restart the application.
   */

  @Execute
  public void execute(IProvisioningAgent agent, UISynchronize sync, IWorkbench workbench) {

    ProvisioningSession session = new ProvisioningSession(agent);
    // update all user-visible installable units
    UpdateOperation operation = new UpdateOperation(session);
    IStatus status = operation.resolveModal(null);
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      MessageDialog.openInformation(null, "Information", "Nothing to update");
    }
    ProvisioningJob provisioningJob = operation.getProvisioningJob(null);
    if (provisioningJob != null) {
      sync.syncExec(new Runnable() {
        @Override
        public void run() {
          boolean performUpdate = MessageDialog.openQuestion(null, "Updates available",
              "There are updates available. Do you want to install them now?");
          if (performUpdate) {
            provisioningJob.schedule();

            boolean restart = MessageDialog.openQuestion(null, "Updates installed, restart?",
                "Updates have been installed successfully, do you want to restart?");
            if (restart) {
              workbench.restart();
            }
          }
        }
      });
    } else {
      if (operation.hasResolved()) {
        MessageDialog.openError(null, "Error",
            "Couldn't get provisioning job: " + operation.getResolutionResult());
      } else {
        MessageDialog.openError(null, "Error", "Couldn't resolve provisioning job");
      }
    }
  }
}
