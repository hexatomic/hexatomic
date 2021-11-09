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
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;

public class UpdateHandler {
  /**
   * blabla.
   * 
   * @param agent blabla
   * @param workbench blabla
   * 
   */
  @Execute
  public void execute(final IProvisioningAgent agent, 
      IWorkbench workbench,  
      UISynchronize sync,
      IProgressMonitor monitor) {
    UpdateRunner p2helper = new UpdateRunner();
    p2helper.performUpdates(agent, workbench, sync, monitor);
  }
  
}


