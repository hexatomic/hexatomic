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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
  //initialisierung des Loggers für Nachrichten
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UpdateRunner.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  
  /**
  * Search for updates and perform them if wanted.
  * 
  * @param agent OSGi service to create an update operation
  * @param workbench current workbench to restart the application
  * @param monitor interface to show progress of update operation
  * 
  */
  public IStatus checkForUpdates(final IProvisioningAgent agent, 
      final IWorkbench workbench,
      IProgressMonitor monitor, final Shell shell, final UISynchronize sync) {
    final UpdateOperation operation = createUpdateOperation(agent);
    log.info("Updateoperation created");
    //Check if there are Updates available
    SubMonitor sub = SubMonitor.convert(monitor,
        "Checking for application updates...", 200);
    final IStatus status = operation.resolveModal(sub.newChild(100));
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      showMessage(shell, sync);
      return Status.CANCEL_STATUS;
    }
    //create update job
    ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
    
    //run update job
    if (provisioningJob != null) {
      configureProvisioningJob(provisioningJob, shell, sync, workbench);
      provisioningJob.schedule();
      return Status.OK_STATUS;
    } else {
      showProvisioningMessage(shell, sync);
      System.err.println("Trying to update from the Eclipse IDE? This won't work !");
      return Status.CANCEL_STATUS;
    }
    
  }

  
  private void configureProvisioningJob(ProvisioningJob provisioningJob, 
      final Shell shell, final UISynchronize sync,
      final IWorkbench workbench) {

    // register a job change listener to track
    // installation progress and notify user upon success
    provisioningJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
          sync.syncExec(new Runnable() {

            @Override
            public void run() {
              boolean restart = MessageDialog.openQuestion(shell, 
                  "Updates installed, restart?",
                  "Updates have been installed. Do you want to restart?");
              if (restart) {
                prefs.putBoolean("justUpdated", true);
                try {
                  prefs.flush();
                } catch (BackingStoreException ex) {
                  ex.printStackTrace();
                }
                workbench.restart();
              }
            }
          });
        } 
        super.done(event);
      }
    });

  }
  
  private void showProvisioningMessage(final Shell parent, final UISynchronize sync) {
    sync.syncExec(new Runnable() {
      @Override
      public void run() {
        MessageDialog.openWarning(parent, 
            "Couldn't find ProvisioningJob",
            "Did you start Update from within eclipse ide?");
      }
    });
  }
  
  static UpdateOperation createUpdateOperation(IProvisioningAgent agent) {
    ProvisioningSession session = new ProvisioningSession(agent);
    log.info("Provisioning session created");
    // update all user-visible installable units
    final UpdateOperation operation = new UpdateOperation(session);
    return operation;
    
  }
  

  
  private void showMessage(final Shell parent, final UISynchronize sync) {
    sync.syncExec(new Runnable() {

      @Override
      public void run() {
        MessageDialog.openWarning(parent, "No update",
                    "No updates for the current installation have been found.");
      }
    });
  }
 
  
}


