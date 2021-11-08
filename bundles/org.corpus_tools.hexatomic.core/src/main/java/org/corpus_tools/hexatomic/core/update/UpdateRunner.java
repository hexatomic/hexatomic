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

import java.net.URI;
import java.net.URISyntaxException;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;


public class UpdateRunner {
  //initialisierung des Loggers für Nachrichten
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectManager.class);
  //Initialisierung der Location mit Updates
  private static final String HOME_DIR = System.getProperty("user.home");
  private static final String SEP = System.getProperty("file.separator");
  private static final String REPOSITORY_LOC = System.getProperty("UpdateHandler.Repo", 
      "file:" + SEP + SEP + HOME_DIR + SEP + "Schreibtisch" + SEP
      + "hexatomic" + SEP + "releng" + SEP + "org.corpus_tools.hexatomic.update" + SEP 
      + "target" + SEP + "repository" + SEP);
  

  /**
  * blabla.
  * 
  * @param agent blabla
  * @param workbench blabla
  * @param sync blabla
  * @param monitor zeigt Fortschritt an
  */
  
  public void performUpdates(final IProvisioningAgent agent, 
      IWorkbench workbench,
      UISynchronize sync,
      IProgressMonitor monitor) {
    UpdateOperation operation = createUpdateOperation(agent);
    
    //Check if there are Updates available
    IStatus status = operation.resolveModal(monitor);
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      MessageDialog.openInformation(
              null, 
              "Information", 
              "Nothing to update");
    }
    
    //create update job
    ProvisioningJob provisioningJob = operation.getProvisioningJob(null);
    
    //run update job
    if (provisioningJob != null) {
      sync.syncExec(new Runnable() {
 
        @Override
        public void run() {
          boolean performUpdate = MessageDialog.openQuestion(
                  null,
                  "Updates available",
                  "There are updates available. Do you want to install them now?");
          if (performUpdate) {
            provisioningJob.schedule();
            
            //restart Application if wanted
            boolean restart = MessageDialog.openQuestion(null,
                "Updates installed, restart?",
                "Updates have been installed successfully, do you want to restart?");
            if (restart) {
              workbench.restart();
            }
          }
        }
        }); 
    } else {
      if (operation.hasResolved()) {
        MessageDialog.openError(
            null, 
            "Error", 
            "Couldn't get provisioning job: " + operation.getResolutionResult());
      } else {
        MessageDialog.openError(
            null, 
            "Error", 
            "Couldn't resolve provisioning job");
      }
    }

    
  }
  
  
  /**
  * ojfsdasof.
  * @param agent blabla
  * @param monitor blabal
  * @return blalna
  * @throws OperationCanceledException blabla
  */
     
  static IStatus performUpdates(IProvisioningAgent agent, IProgressMonitor monitor) 
      throws OperationCanceledException { 
    
    UpdateOperation operation = createUpdateOperation(agent);
    SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200); 
    IStatus status = operation.resolveModal(sub.newChild(100)); 
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) { 
      return status; 
    } 
    if (status.getSeverity() == IStatus.CANCEL) {
      throw new OperationCanceledException(); 
    }
       
    if (status.getSeverity() != IStatus.ERROR) { 
      // More complex status handling might include showing the user what 
      // updates are available if there are multiples, differentiating 
      // patches vs. updates, etc. In this example, we simply update as 
      // suggested by the operation. 
      ProvisioningJob job = operation.getProvisioningJob(monitor); 
      if (job == null) { 
        log.info("ProvisioningJob could not be created - " 
            + "does this application support p2 software installation?");
        return status;
      } 
      status = job.runModal(sub.newChild(100)); 
      if (status.getSeverity() == IStatus.CANCEL) { 
        throw new OperationCanceledException(); 
      }
    }  
    return status; 
  }
  

  static boolean checkComponents(IProvisioningAgent agent, 
      IWorkbench workbench,
      UISynchronize sync,
      IProgressMonitor monitor) {
    return ((agent != null && workbench != null && sync != null && monitor != null));
  }
  
  static UpdateOperation createUpdateOperation(IProvisioningAgent agent) {
    ProvisioningSession session = new ProvisioningSession(agent);
    if (session != null) {
      log.info("Provisioning session created");
    }
    // update all user-visible installable units
    UpdateOperation operation = new UpdateOperation(session);
    URI uri = null;

    try {
      uri = new URI(REPOSITORY_LOC);
    } catch (URISyntaxException e) {
      throw new OperationCanceledException("Invalid repository location");

    }
    
    //Define location of p2-repository
    operation.getProvisioningContext().setArtifactRepositories(uri);
    operation.getProvisioningContext().setMetadataRepositories(uri);
    return operation;
    
  }
  
  
  
}


