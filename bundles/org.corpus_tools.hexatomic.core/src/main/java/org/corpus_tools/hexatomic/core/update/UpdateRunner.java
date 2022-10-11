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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
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


@Creatable
public class UpdateRunner {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UpdateRunner.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");

  @Inject
  private IProvisioningAgent agent;

  @Inject
  private IEclipseContext context;

  @Inject
  private IProgressMonitor monitor;

  @Inject
  private UISynchronize sync;

  @Inject
  private IEventBroker events;

  @Inject
  private ErrorService errorService;

  /**
   * Schedules a {@link Job} that searches for updates and perform them if wanted.
   * 
   * @param triggeredManually Indicates whether the update processes was started by a manual action
   *        of the user.
   * @param shell The user interface shell.
   */
  public void scheduleUpdateJob(boolean triggeredManually, Shell shell) {
    Job updateJob = new Job("Update Job") {
      @Override
      protected IStatus run(final IProgressMonitor monitor) {
        return checkForUpdates(triggeredManually, shell);
      }
    };
    updateJob.schedule();
  }


  /**
   * Search for updates and perform them if wanted.
   * 
   * @param triggeredManually Indicates whether the update processes was started by a manual action
   *        of the user.
   * @param shell The user interface shell.
   */
  private IStatus checkForUpdates(boolean triggeredManually, Shell shell) {

    final UpdateOperation operation = createUpdateOperation(agent);
    log.debug("Update operation created");
    // Check if there are Updates available
    SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
    final IStatus status = operation.resolveModal(sub.newChild(100));
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      events.send(Topics.TOOLBAR_STATUS_MESSAGE, "Hexatomic is up to date");
      return Status.CANCEL_STATUS;
    }
    log.debug("triggeredManually=" + triggeredManually);
    log.debug("Update status: {} ({})", status.getMessage(), status.getCode());

    // create update job
    ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);

    // run update job
    if (provisioningJob != null) {
      log.info("Update available!");
      final AtomicBoolean performUpdate = new AtomicBoolean(false);
      sync.syncExec(() -> performUpdate.set(MessageDialog.openQuestion(shell, "Update available",
          "Do you want to install the available update?")));
      if (performUpdate.get()) {
        IWorkbench workbench = context.get(IWorkbench.class);
        configureProvisioningJob(provisioningJob, shell, sync, workbench);
        provisioningJob.schedule();
        return Status.OK_STATUS;
      } else {
        return Status.CANCEL_STATUS;
      }
    } else {
      log.warn("Couldn't find ProvisioningJob.");
      showProvisioningErrorMessage(triggeredManually);
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



  private void showProvisioningErrorMessage(boolean triggeredManually) {

    if (triggeredManually) {
      // Give more feedback that needs to be acknowledged
      final StringBuffer message = new StringBuffer();
      message.append("Update check failed for unknown reasons. "
          + "This can happen e.g. if the network is unreachable "
          + "or if some firewalls block the connection to the update server.\n\n"
          + "Please retry later or check the debug logs if the problem persists.");

      // Check if a special environment variable set in the Eclipse run configuration is set
      if ("true".equalsIgnoreCase(System.getenv("runInEclipse"))) {
        // Hexatomic is started/debugged from inside Eclipse and the developer might need some
        // additional hints that
        // starting the update from Eclipse itself won't work.
        message.append("\n\n");
        message.append(
            "In case of a developer build, "
                + "this error can also occur when you start Hexatomic "
                + "from inside the Eclipse development environment.");
      }
      
      errorService.showError("Update check failed", message.toString(), UpdateRunner.class);

    } else {
      // Since this was a background task, also inform about this less prominent
      events.send(Topics.TOOLBAR_STATUS_MESSAGE, "Update check failed.");
    }


  }

  static UpdateOperation createUpdateOperation(IProvisioningAgent agent) {
    ProvisioningSession session = new ProvisioningSession(agent);
    log.info("Provisioning session created");
    // update all user-visible installable units
    return new UpdateOperation(session);
  }

}


