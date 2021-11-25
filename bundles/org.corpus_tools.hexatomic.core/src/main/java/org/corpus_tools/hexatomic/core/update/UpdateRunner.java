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
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.prefs.BackingStoreException;


public class UpdateRunner {
  //initialisierung des Loggers für Nachrichten
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UpdateRunner.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  //Initialisierung der Location mit Updates
  private static final String HOME_DIR = System.getProperty("user.home");
  private static final String SEP = System.getProperty("file.separator");
  private static final String REPOSITORY_LOC = System.getProperty("UpdateHandler.Repo", 
      "file:" + SEP + SEP + HOME_DIR + SEP + "Schreibtisch" + SEP + "updatetests" + SEP
      + "test1" + SEP + "hexatomic" + SEP + "releng" + SEP 
      + "org.corpus_tools.hexatomic.update" + SEP + "target" + SEP + "repository" + SEP);
  

  /**
  * Search for updates and perform them if wanted.
  * 
  * @param agent OSGi service to create an update operation
  * @param workbench current workbench to restart the application
  * @param sync Helper class to execute code in the UI thread
  * @param monitor interface to show progress of update operation
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
            if (restart && workbench != null) {
              prefs.putBoolean("justUpdated", true);
              try {
                prefs.flush();
              } catch (BackingStoreException ex) {
                ex.printStackTrace();
              }
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


