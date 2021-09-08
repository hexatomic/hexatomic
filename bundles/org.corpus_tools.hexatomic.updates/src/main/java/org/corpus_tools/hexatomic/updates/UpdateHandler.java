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

package org.corpus_tools.hexatomic.updates;

import java.net.URI;
import java.net.URISyntaxException;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class UpdateHandler {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectManager.class);
  private static final String HOME_DIR = System.getProperty("user.home");
  private static final String SEP = System.getProperty("file.separator");
  private static final String REPOSITORY_LOC = System.getProperty("UpdateHandler.Repo", 
      "file:" + SEP + SEP + HOME_DIR + SEP + "Schreibtisch" + SEP + "hexatomic" + SEP + "releng" 
      + SEP + "org.corpus_tools.hexatomic.update" + SEP + "target" + SEP + "repository" 
      + SEP);
  

  private IWorkbench workbench;

  /**
   * blabla.
   * 
   * @param agent blabla
   * @param workbench blabla
   */
  @Execute
  public void execute(final IProvisioningAgent agent, IWorkbench workbench, Shell shell) {
    System.out.println(REPOSITORY_LOC);
    if (agent != null) {
      log.debug("Provisioning agent created");
    }
    this.workbench = workbench;
    Job updateJob = Job.create("Update Job", monitor -> {
      performUpdates(agent, monitor, shell);
    });
    
    
    boolean restart = MessageDialog.openConfirm(shell, 
        "Updates installed, restart?",
        "Updates have been installed successfully, do you want to restart?");
    if (restart) {
      log.debug("Restarting workbench");
      workbench.restart();
    }
    
    updateJob.schedule();

  }

  private IStatus performUpdates(final IProvisioningAgent agent, 
      IProgressMonitor monitor, Shell shell) {
    // configure update operation
    final ProvisioningSession session = new ProvisioningSession(agent);
    if (session != null) {
      log.debug("Provisioning session created");
    }
    final UpdateOperation operation = new UpdateOperation(session);
    // create uri and check for validity

    URI uri = null;

    try {
      uri = new URI(REPOSITORY_LOC);
    } catch (URISyntaxException e) {
      //errorService.handleException("Invalid repository location " + path.toString(), e);
      throw new OperationCanceledException("Invalid repository location");

    }

    operation.getProvisioningContext().setArtifactRepositories(uri);
    operation.getProvisioningContext().setMetadataRepositories(uri);

    // check for updates, this causes I/O

    final IStatus status = operation.resolveModal(monitor);
   
    // failed to find updates (inform user and exit)
    
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      log.debug("Nothing to Update");
      return Status.CANCEL_STATUS;
    }
    // run installation
    ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
   
    if (provisioningJob == null) {
      return Status.CANCEL_STATUS;

    }
    
    //configureProvisioningJob(provisioningJob);
    
    log.debug("Installing updates");
    provisioningJob.schedule();
   
    
    //workbench.restart();
    System.out.println("Workbench restarted");

    return Status.OK_STATUS;

  }
  /*
  private void configureProvisioningJob(ProvisioningJob provisioningJob, Shell shell) {

    // register a job change listener to track

    // installation progress and restart application in case of updates

    provisioningJob.addJobChangeListener(new JobChangeAdapter() {
      @Override
      public void done(IJobChangeEvent event) {
        if (event.getResult().isOK()) {
          boolean restart = MessageDialog.openQuestion(shell, "Updates installed, restart?",
              "Updates have been installed successfully, do you want to restart?");
          if (restart) {
            log.debug("Restarting workbench");
            workbench.restart();
          }
        }
        super.done(event);
      }
    });
  }*/
}


