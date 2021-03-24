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

// import java.lang.reflect.InvocationTargetException;
// import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
// import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
// import org.eclipse.core.runtime.SubMonitor;
// import org.eclipse.core.runtime.preferences.IEclipsePreferences;
// import org.eclipse.core.runtime.preferences.InstanceScope;
// import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
// import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
// import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
// import org.eclipse.equinox.p2.core.ProvisionException;
// import org.eclipse.equinox.p2.engine.IProfile;
// import org.eclipse.equinox.p2.
// import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
// import org.eclipse.equinox.p2.repository.IRunnableWithProgress;
import org.eclipse.jface.dialogs.MessageDialog;
// import org.eclipse.jface.dialogs.ProgressMonitorDialog;
// import org.eclipse.ui.PlatformUI;
// import org.osgi.framework.ServiceReference;
// import org.osgi.service.prefs.BackingStoreException;

public class P2Util {
  // @Inject
  // @Preference(nodePath = "org.corpus_tools.hexatomic.core")
  // private IEclipsePreferences pref;

  // private static final String JUSTUPDATED = "justUpdated";
  // private static final String PLUGIN_ID = "org.corpus_tools.hexatomic.core";
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectManager.class);
  private IProvisioningAgent agent;
  // private IProvisioningAgentProvider agentProvider = null;
  private UISynchronize sync;
  // private Boolean justUpdated;
  private IWorkbench workbench;

  /**
   * blablablba.
   * 
   * @param agent bla
   * @param sync bla
   */
  public P2Util(IProvisioningAgent agent, UISynchronize sync, IWorkbench workbench) {
    this.agent = agent;
    this.workbench = workbench;
  }

  static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor,
      UISynchronize sync) throws OperationCanceledException {
    ProvisioningSession session = new ProvisioningSession(agent);
    UpdateOperation operation = new UpdateOperation(session);

    // SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
    // IStatus status = operation.resolveModal(sub.newChild(100));
    IStatus status = operation.resolveModal(null);

    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      MessageDialog.openInformation(null, "Information", "Nothing to update.");
      return status;
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



      // status = job.runModal(sub.newChild(100));
      if (status.getSeverity() == IStatus.CANCEL) {
        throw new OperationCanceledException();
      }
    }
    return status;
  }

  /**
   * performs auto update.
   * 
   * @return boolean if AutoUpdate will be performed
   */
  public boolean performAutoUpdate() {
    log.debug("Starting P2 auto-update process");
    // if (pref.getBoolean(JUSTUPDATED, false)) {
    // setJustUpdated(false);
    // log.debug("Restarting after auto update, skipping auto update");
    // return false;
    // }
    // try {
    // agent = this.agentProvider.createAgent(null);

    // } catch (ProvisionException e) {
    // log.debug("Error creating provisioning agent - " + e.getMessage());
    // return false;
    // }
    if (agent != null) {
      log.debug("Provisioning agent created");
    }
    /*
     * IProfileRegistry profileRegistry = (IProfileRegistry)
     * agent.getService(IProfileRegistry.SERVICE_NAME); if (profileRegistry == null) {
     * log.debug("Could not locate the Profile Registry, ending auto update process"); return false;
     * } IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF); if (profile == null)
     * { log.debug("No profile found for this installation, ending auto update process"); return
     * false; }
     */

    ProvisioningSession session = new ProvisioningSession(agent);
    if (session != null) {
      log.debug("Provisioning session created");
    }

    /*
     * Create the jobs to update and install features.
     */
    UpdateOperation operation = new UpdateOperation(session);

    // final ProvisioningJob updateJob = getUpdateJob(session, profile);

    /*
     * Create a runnable to execute the update. We'll show a dialog during the process and then
     * return when the runnable is complete.
     */
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
              workbench.restart();
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



      // status = job.runModal(sub.newChild(100));
      if (status.getSeverity() == IStatus.CANCEL) {
        throw new OperationCanceledException();
      }
    }
    return true;
  }

  public void platzhalter() {
    System.out.println("test");
  }

  /*
   * IRunnableWithProgress runnable = new IRunnableWithProgress() {
   * 
   * 
   * 
   * public void run(IProgressMonitor monitor) throws InvocationTargetException { SubMonitor subMon
   * = SubMonitor.convert(monitor, 200); IStatus status = null;
   * 
   * if (updateJob != null) { status = updateJob.runModal(subMon.newChild(100));
   * 
   * dumpStatus(status); } else { subMon.worked(100); }
   * 
   * if (updateJob != null) { if (status.getSeverity() != IStatus.ERROR) { setJustUpdated(true);
   * restartRequired[0] = true; log.debug("Updates installed, restart required"); } else {
   * log.debug("Update failed, skipping installation step");
   * log.debug(status.getException().getMessage()); return; } }
   * 
   * } }; /* Execute the runnable and wait for it to complete.
   */
  /*
   * try { new ProgressMonitorDialog(null).run(true, false, runnable); return restartRequired[0]; }
   * catch (InvocationTargetException e) { e.printStackTrace(); } catch (InterruptedException e) { }
   * finally { agent.stop(); }
   */


  /*
   * private void setJustUpdated(boolean justUpdated) { pref.putBoolean(JUSTUPDATED, justUpdated);
   * try { pref.flush(); } catch (BackingStoreException e) {
   * log.debug("Error setting just updated flag - " + e.getMessage()); } }
   * 
   * 
   * private static IProvisioningAgent getProvisioningAgent() throws ProvisionException {
   * ServiceReference<?> reference =
   * Activator.getContext().getServiceReference(IProvisioningAgent.SERVICE_NAME); IProvisioningAgent
   * agent = (IProvisioningAgent) Activator.getContext().getService(reference); return agent; }
   * 
   * private void dumpStatus(IStatus status) { log.debug("Status severity=" + status.getSeverity() +
   * ", message=" + status.getMessage() + ", code=" + status.getCode()); if (status.isMultiStatus())
   * for (IStatus child : status.getChildren()) dumpStatus(child); }
   * 
   * 
   * private ProvisioningJob getUpdateJob(ProvisioningSession provisioningSession, IProfile profile)
   * { IStatus loadStatus;
   * 
   * if ((loadStatus = loadP2Repository(provisioningSession.getProvisioningAgent())) !=
   * Status.OK_STATUS) { dumpStatus(loadStatus); return null; }
   * 
   * UpdateOperation updateOperation = new UpdateOperation(provisioningSession); IStatus
   * updateOperationStatus = updateOperation.resolveModal(new NullProgressMonitor());
   * 
   * if (updateOperationStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
   * log.debug("Nothing to update"); return null; }
   * 
   * if (updateOperation.hasResolved()) { log.debug("Update operation resolved successfully");
   * 
   * Update[] possibleUpdates = updateOperation.getPossibleUpdates();
   * 
   * log.debug("Possible updates:");
   * 
   * for (Update update : possibleUpdates) { log.debug(" - " + update); }
   * 
   * return updateOperation.getProvisioningJob(null); }
   * 
   * log.debug("Update operation not resolved."); dumpStatus(updateOperationStatus);
   * 
   * return null; }
   */
  /*
   * private IStatus loadP2Repository(IProvisioningAgent provisioningAgent) { if (metadataRepository
   * == null) { String repositoryUri = getRepositoryUri(); if (repositoryUri == null ||
   * repositoryUri.isEmpty()) return new Status(Status.ERROR, PLUGIN_ID,
   * "No repository specified. Add -DrepositoryUrl= to your INI file.");
   * 
   * IMetadataRepositoryManager manager = (IMetadataRepositoryManager) provisioningAgent
   * .getService(IMetadataRepositoryManager.SERVICE_NAME);
   * 
   * try { metadataRepository = manager.loadRepository(new URI(repositoryUri), new
   * NullProgressMonitor()); } catch (Exception e) {
   * this.log("Failed to load metadata repository at location " + repositoryUri); return new
   * Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e); }
   * 
   * this.log("Metadata repository loaded: " + repositoryUri);
   * 
   * IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) provisioningAgent
   * .getService(IArtifactRepositoryManager.SERVICE_NAME); try { /* Loading repository but not
   * saving a reference
   */
  /*
   * artifactManager.loadRepository(new URI(repositoryUri), new NullProgressMonitor()); } catch
   * (Exception e) { this.log("Failed to load artifact repository at location " + repositoryUri);
   * return new Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e); }
   * 
   * this.log("Artifact repository loaded: " + repositoryUri); }
   * 
   * return Status.OK_STATUS;
   */
}


