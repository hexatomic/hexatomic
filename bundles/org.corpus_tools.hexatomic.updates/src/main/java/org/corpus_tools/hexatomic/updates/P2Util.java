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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;

public class P2Util {
  //XXX Check for updates to this application and return a status.
  static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor) 
      throws OperationCanceledException {
    ProvisioningSession session = new ProvisioningSession(agent);
    // the default update operation looks for updates to the currently
    // running profile, using the default profile root marker. To change
    // which installable units are being updated, use the more detailed
    // constructors.
    UpdateOperation operation = new UpdateOperation(session);
    SubMonitor sub = SubMonitor.convert(monitor,
              "Checking for application updates...", 200);
    IStatus status = operation.resolveModal(sub.newChild(100));
    if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
      return status;
    }
    if (status.getSeverity() == IStatus.CANCEL) {
      throw new OperationCanceledException();
    }
    if (status.getSeverity() != IStatus.ERROR) {
      // More complex status handling might include showing the user what updates
      // are available if there are multiples, differentiating patches vs. updates, etc.
      // In this example, we simply update as suggested by the operation.
      ProvisioningJob job = operation.getProvisioningJob(null);
      status = job.runModal(sub.newChild(100));
      if (status.getSeverity() == IStatus.CANCEL) {
        throw new OperationCanceledException();
      }
    }
    return status;
  }

}
