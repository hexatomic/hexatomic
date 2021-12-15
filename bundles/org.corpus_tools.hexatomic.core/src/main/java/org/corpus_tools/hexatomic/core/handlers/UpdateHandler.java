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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.widgets.Shell;

public class UpdateHandler {
  
  /**
   * sdadfs.
   * @param agent sdfdsf
   * @param shell dsfsdf
   * @param sync sdfsd
   * @param workbench sdfsd
   */
  @Execute
  public void execute(final IProvisioningAgent agent, final Shell shell, final UISynchronize sync,
      final IWorkbench workbench) {
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
