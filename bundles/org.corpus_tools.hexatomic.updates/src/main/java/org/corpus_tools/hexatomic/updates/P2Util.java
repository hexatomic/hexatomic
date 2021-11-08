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

import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;


public class P2Util {
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
    //Überprüfen, ob alle argumente valide sind
    if (agent != null) {
      log.info("Provisioning agent created");
    } else {
      log.info("Agent not valid");
    }
    if (workbench != null) {
      log.info("Workbench created");
    } else {
      log.info("Workbench not valid");
    }
    if (monitor != null) {
      log.info("Monitor created");
    } else {
      log.info("Monitor not valid");
    }
    if (sync != null) {
      log.info("sync created");
    } else {
      log.info("sync not valid");
    }
    System.out.println(checkComponents(agent, workbench, sync, monitor));
    
    
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
      //errorService.handleException("Invalid repository location " + path.toString(), e);
      throw new OperationCanceledException("Invalid repository location");

    }
    
    operation.getProvisioningContext().setArtifactRepositories(uri);
    operation.getProvisioningContext().setMetadataRepositories(uri);
    IStatus status = operation.resolveModal(monitor);
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      MessageDialog.openInformation(
              null, 
              "Information", 
              "Nothing to update");
    }
    
    ProvisioningJob provisioningJob = operation.getProvisioningJob(null);
    
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
  * shfiaosf.
  * @param agent bla
  * @param workbench bla
  * @param sync bla
  * @param monitor bla
  * @return nlala
  */
  public boolean checkComponents(IProvisioningAgent agent, 
      IWorkbench workbench,
      UISynchronize sync,
      IProgressMonitor monitor) {
    return ((agent != null && workbench != null && sync != null && monitor != null));
  }  
}


