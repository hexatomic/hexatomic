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

package org.corpus_tools.hexatomic.formats.exporter;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.core.handlers.SaveAsHandler;
import org.corpus_tools.hexatomic.formats.Activator;
import org.corpus_tools.hexatomic.formats.WizardDialogProvider;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ExportHandler {

  @Inject
  private SaveAsHandler saveAsHandler;

  @Inject
  private WizardDialogProvider wizardDialogProvider;



  /**
   * Shows the export wizard. If there are unsaved changes, the users is prompted to save the
   * project.
   * 
   * @param shell The parent shell used to display the dialogs.
   * @param errorService The service to log and display errors.
   * @param projectManager Used to get the Salt project to export.
   * @param notificationFactory Needed to show notifications.
   * @param sync Since part of the wizard will run in the background, synchronization with the UI
   *        thread is needed.
   */
  @Execute
  public void execute(Shell shell, ErrorService errorService, ProjectManager projectManager,
      SaltNotificationFactory notificationFactory, UISynchronize sync) {

    WizardDialog dialog = wizardDialogProvider.createDialog(shell,
        new ExportWizard(errorService, projectManager, notificationFactory, sync));

    if (projectManager.isDirty()) {
      boolean saveSelected = MessageDialog.openConfirm(shell, "Save project before export?",
          "There are unsaved changes in the project. "
              + "You have to save the project before exporting it. "
              + "Do you want to save the project now?");
      if (saveSelected) {
        if (projectManager.getLocation().isPresent()) {
          // Directly save the project on its current location
          projectManager.save(shell);
          dialog.open();
        } else {
          // Allow the user to select the directory to save the project to
          if (saveAsHandler.execute(shell, null)) {
            dialog.open();
          }
        }
      }
    } else {
      dialog.open();
    }
  }


  @CanExecute
  protected boolean canExecute(ProjectManager projectManager) {
    return Activator.getPepper().isPresent()
        && !projectManager.getProject().getCorpusGraphs().isEmpty();
  }

  public void setSaveAsHandler(SaveAsHandler saveAsHandler) {
    this.saveAsHandler = saveAsHandler;
  }


  public void setWizardDialogProvider(WizardDialogProvider wizardDialogProvider) {
    this.wizardDialogProvider = wizardDialogProvider;
  }

}
