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

package org.corpus_tools.hexatomic.core.handlers;

import org.corpus_tools.hexatomic.core.update.UpdateRunner;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;

public class UpdateHandler {

  //private static final String REPOSITORY_LOC = System.getProperty("UpdateHandler.Repo", "http://localhost/repository");
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UpdateHandler.class);
  private IWorkbench workbench;
  
  /**
   * khk.
   * @param agent khhk
   * @param workbench hjoi
   */
  @Execute
  public void execute(final IProvisioningAgent agent, IWorkbench workbench) {
    this.workbench = workbench;
    Job updateJob = Job.create("Update Job", monitor -> {
      UpdateRunner ur = new UpdateRunner();
      ur.performUpdates(agent, workbench, monitor);
    });
    configureUpdateJob(updateJob);
    updateJob.schedule();

  }

  /*
  private IStatus performUpdates(final IProvisioningAgent agent, IProgressMonitor monitor) {
    // configure update operation
    final ProvisioningSession session = new ProvisioningSession(agent);
    log.info("Provisioning session created");
    final UpdateOperation operation = new UpdateOperation(session);
    log.info("Updateoperation created");
    SubMonitor sub = SubMonitor.convert(monitor,
        "Checking for application updates...", 200);
    final IStatus status = operation.resolveModal(sub);
    log.info("got status");
    // failed to find updates (inform user and exit)

    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      log.info("Nothing to update");
      return Status.CANCEL_STATUS;
    }

    // run installation
    ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);

    // updates cannot run from within Eclipse IDE!!!

    if (provisioningJob == null) {
      log.info("Couldn't find provisioning Job");
      MessageDialog.openInformation(null, "Information", "Nothing to update");
      return Status.CANCEL_STATUS;

    }

    configureProvisioningJob(provisioningJob);
    
    provisioningJob.schedule();
    
    return Status.OK_STATUS;
    

  }

  */
  private void configureProvisioningJob(ProvisioningJob provisioningJob) {

    // register a job change listener to track

    // installation progress and restart application in case of updates

    provisioningJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
          log.info("ProvisioningJob done");
        }
        super.done(event);
      }
    });
  }
  
  private void configureUpdateJob(Job updateJob) {

    // register a job change listener to track

    // installation progress and restart application in case of updates

    updateJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
          log.info("Second job done");
          workbench.restart();
        }
        super.done(event);
      }
    });
  }
  
}
