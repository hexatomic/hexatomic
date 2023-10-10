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

package org.corpus_tools.hexatomic.formats.importer;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.formats.Activator;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ImportHandler {

  @Execute
  protected void execute(Shell shell, ErrorService errorService, ProjectManager projectManager,
      SaltNotificationFactory notificationFactory, UISynchronize sync) {
    if (projectManager.isDirty()) {
      // Ask user if project should be closed even with unsaved changes
      boolean confirmed = MessageDialog.openConfirm(shell, "Discard unsaved changes?",
          "There are unsaved changes in the project that will be lost if you close it. "
              + "Do you really want to close the project and import a new one?");
      if (!confirmed) {
        return;
      }
    }
    WizardDialog dialog = new WizardDialog(shell,
        new ImportWizard(errorService, projectManager, notificationFactory, sync));
    dialog.open();
  }

  @CanExecute
  protected boolean canExecute() {
    return Activator.getPepper().isPresent();
  }
}
