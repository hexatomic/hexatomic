/*-
 * #%L
 * org.corpus_tools.hexatomic.formats
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.formats;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.wizard.Wizard;

/**
 * Base class for wizards that use Pepper for conversion.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public abstract class PepperWizard extends Wizard {

  private final ErrorService errorService;
  private final ProjectManager projectManager;
  private final SaltNotificationFactory notificationFactory;
  private final UISynchronize sync;

  protected PepperWizard(ErrorService errorService, ProjectManager projectManager,
      SaltNotificationFactory notificationFactory, UISynchronize sync) {
    super();
    this.errorService = errorService;
    this.projectManager = projectManager;
    this.notificationFactory = notificationFactory;
    this.sync = sync;
    setNeedsProgressMonitor(true);
  }

  public ErrorService getErrorService() {
    return errorService;
  }

  public ProjectManager getProjectManager() {
    return projectManager;
  }

  public SaltNotificationFactory getNotificationFactory() {
    return notificationFactory;
  }

  public UISynchronize getSync() {
    return sync;
  }

}
