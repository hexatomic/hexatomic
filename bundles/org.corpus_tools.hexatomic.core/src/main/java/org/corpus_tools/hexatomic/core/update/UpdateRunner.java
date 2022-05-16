/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.core.update;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;


public class UpdateRunner {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UpdateRunner.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  @Inject
  ErrorService errorService;

  /**
   * Search for updates and perform them if wanted.
   * 
   * @param agent OSGi service to create an update operation.
   * @param workbench current workbench to restart the application.
   * @param monitor interface to show progress of update operation.
   * @param shell The user interface shell.
   * @param sync Helper class to execute code in the UI thread.
   * @param events Allows to send events.
   * 
   */
  public IStatus checkForUpdates(final IProvisioningAgent agent, final IWorkbench workbench,
      IProgressMonitor monitor, final Shell shell, final UISynchronize sync, IEventBroker events) {
    final UpdateOperation operation = createUpdateOperation(agent);
    log.debug("Update operation created");
    // Check if there are Updates available
    SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
    final IStatus status = operation.resolveModal(sub.newChild(100));
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      events.send(Topics.TOOLBAR_STATUS_MESSAGE, "Hexatomic is up to date");
      return Status.CANCEL_STATUS;
    }
    log.debug("Update status: {} ({})", status.getMessage(), status.getCode());

    // create update job
    ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
    log.debug("provisioningJob={}", provisioningJob);

    // run update job
    if (provisioningJob != null) {
      log.info("Update available!");
      final AtomicBoolean performUpdate = new AtomicBoolean(false);
      sync.syncExec(() -> performUpdate.set(MessageDialog.openQuestion(shell, "Update available.",
          "Do you want to install the available update?")));
      if (performUpdate.get()) {
        configureProvisioningJob(provisioningJob, shell, sync, workbench);
        provisioningJob.schedule();
        return Status.OK_STATUS;
      } else {
        return Status.CANCEL_STATUS;
      }
    } else {
      showProvisioningMessage(shell, sync);
      log.warn("Couldn't find ProvisioningJob.");
      return Status.CANCEL_STATUS;
    }

  }


  private void configureProvisioningJob(ProvisioningJob provisioningJob, final Shell shell,
      final UISynchronize sync, final IWorkbench workbench) {

    // register a job change listener to track
    // installation progress and notify user upon success
    provisioningJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
          sync.syncExec(() -> {
            boolean restart = MessageDialog.openQuestion(shell, "Updates installed, restart?",
                "Updates have been installed. Do you want to restart?");
            if (restart) {
              prefs.putBoolean("justUpdated", true);
              try {
                prefs.flush();
              } catch (BackingStoreException ex) {
                errorService.handleException("Couldn't update preferences", ex, UpdateRunner.class);
              }
              workbench.restart();
            }
          });
        }
        super.done(event);
      }
    });
  }



  private void showProvisioningMessage(final Shell parent, final UISynchronize sync) {
    sync.syncExec(() -> MessageDialog.openWarning(parent, "Couldn't find ProvisioningJob",
        "Did you start Update from within the Eclipse IDE?"));
  }

  static UpdateOperation createUpdateOperation(IProvisioningAgent agent) {
    ProvisioningSession session = new ProvisioningSession(agent);
    log.info("Provisioning session created");
    // update all user-visible installable units
    return new UpdateOperation(session);
  }

}


