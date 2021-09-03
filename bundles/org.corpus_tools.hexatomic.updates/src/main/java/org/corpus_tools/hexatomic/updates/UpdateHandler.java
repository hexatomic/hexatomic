package org.corpus_tools.hexatomic.updates;

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


import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;


public class UpdateHandler {
  @Inject
  @Preference(nodePath = "org.corpus_tools.hexatomic.core")
  private IEclipsePreferences pref;
  @Inject
  IProvisioningAgent agent;
  
  @Inject
  IProgressMonitor monitor;
  
  @Inject
  UISynchronize sync;
  private static final String JUSTUPDATED = "justUpdated";
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectManager.class);
  
  /**
   * performs auto update.
   */
 
  public void performAutoUpdate() {
    log.debug("Starting P2 auto-update process");
    if (pref.getBoolean(JUSTUPDATED, false)) {
      setJustUpdated(false, pref);
      log.debug("Restarting after auto update, skipping auto update");
    }
    if (agent != null) {
      log.debug("Provisioning agent created");
    }
    ProvisioningSession session = new ProvisioningSession(agent);
    if (session != null) {
      log.debug("Provisioning session created");
    }
    UpdateOperation operation = new UpdateOperation(session);
    IStatus status = operation.resolveModal(null);

    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      log.debug("Nothing to update.");
    }
    if (status.getSeverity() == IStatus.CANCEL) {
      throw new OperationCanceledException();
    }
    if (status.getSeverity() != IStatus.ERROR) {
      ProvisioningJob job = operation.getProvisioningJob(null);
      if (job != null) {
        sync.syncExec(new Runnable() {

          @Override
          public void run() {
            boolean performUpdate = MessageDialog.openQuestion(null, "Updates available",
                "There are updates available. Do you want to install them now?");
            if (performUpdate) {
              job.schedule();
            }
            boolean restart = MessageDialog.openQuestion(null, "Updates installed, restart?",
                "Updates have been installed successfully, do you want to restart?");
            if (restart) {
              setJustUpdated(true, pref);
              PlatformUI.getWorkbench().restart();
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
      if (status.getSeverity() == IStatus.CANCEL) {
        throw new OperationCanceledException();
      }
    }
  }


  private void setJustUpdated(boolean justUpdated, IEclipsePreferences pref) {
    pref.putBoolean(JUSTUPDATED, justUpdated);
    try {
      pref.flush();
    } catch (BackingStoreException e) {
      log.debug("Error setting just updated flag - " + e.getMessage());
    }
  }

  public void platzhalter() {
    System.out.println("test");
  }
}

