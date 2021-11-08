/*-
 * #%L
 * org.corpus_tools.hexatomic.updates
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
import javax.annotation.PostConstruct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Activator.class);
  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.updates");
  
  @PostConstruct
  @Override
  public void start(BundleContext context) throws Exception {
    log.info("Starting Updates-Plugin");
    // TODO Auto-generated method stub
    if (prefs.getBoolean("autoUpdate", false)) {
      ServiceReference<?> reference = context
          .getServiceReference(IProvisioningAgent.SERVICE_NAME);
      Object service = context.getService(reference);
      if (service != null) {
        context.ungetService(reference);
      }
      IProvisioningAgent agent = (IProvisioningAgent) service;
      checkForUpdates(agent);
      MessageDialog.openInformation(
          null, 
          "Updates installed", 
          "Updates have been installed successfully." 
          + "Restart Hexatomic to implement the changes.");
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * sdfsfda.
   * @param agent bsadlfs
   */
  public static void checkForUpdates(IProvisioningAgent agent) { 
    try { 
      ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(null); 
      progressDialog.run(true, true, (IRunnableWithProgress) new IRunnableWithProgress() {     
        public void run(IProgressMonitor monitor) 
            throws InvocationTargetException, InterruptedException { 
          doCheckForUpdates(monitor, agent);
        } 
      }); 
    } catch (InvocationTargetException e) { 
      log.info("Fehlera " + e); 
    } catch (InterruptedException e) { 
      log.info("Fehlerb " + e); 
    } 
   
  }
  
  /**
   * llafds.
   * @param monitor dsafdsf
   * @param agent asfdsf
   */
  
  private static void doCheckForUpdates(IProgressMonitor monitor, IProvisioningAgent agent) { 
    if (agent == null) {
      log.info("No provisioning agent found.  This application is not set up for updates."); 
      return; 
    } 

    try { 
      IStatus updateStatus = P2Util.performUpdates(agent, monitor); 
      log.info("Status" + updateStatus); 
      if (updateStatus.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) { 
        return; 
      } 

    } finally { 
      System.out.println("All done");
    } 
  }
  

}
