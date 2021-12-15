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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AppStartupCompleteEventHandler implements EventHandler {
  private IEventBroker eventBroker;
  private IEclipseContext context;
  IProvisioningAgent agent;
  UISynchronize sync;
  IProgressMonitor monitor;
  IWorkbench workbench; 
  Shell shell;

  /**
   * Create instance of AppStartupCompleteEventHandler.
   * @param eventBroker Event broker service to unsubscribe from subscribed event
   * @param context Context of application to recieve workbench
   * @param agent OSGi service to create an update operation
   * @param sync Helper class to execute code in the UI thread
   * @param monitor interface to show progress of update operation
   */
  public AppStartupCompleteEventHandler(
      IEventBroker eventBroker, 
      IEclipseContext context,
      IProvisioningAgent agent,
      UISynchronize sync,
      IProgressMonitor monitor,
      Shell shell) {
    this.eventBroker = eventBroker;
    this.context = context;
    this.agent = agent;
    this.sync = sync;
    this.monitor = monitor; 
    this.shell = shell;
  }
 
  
  @Override 
  public void handleEvent(Event event) {
    eventBroker.unsubscribe(this);
    this.workbench = context.get(IWorkbench.class);
    Job updateJob = new Job("Update Job") {
      @Override
      protected IStatus run(final IProgressMonitor monitor) {
        UpdateRunner ur = new UpdateRunner();
        return ur.checkForUpdates(agent,workbench, monitor, shell, sync);
      }
    };
    updateJob.schedule();
  }  
}
