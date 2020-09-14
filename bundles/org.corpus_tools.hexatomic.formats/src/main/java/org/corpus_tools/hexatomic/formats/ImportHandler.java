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
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class ImportHandler {

  @Execute
  protected static void execute(Shell shell, ErrorService errorService,
      ProjectManager projectManager) {
    try {
      WizardDialog dialog = new WizardDialog(shell, new ImportWizard(errorService, projectManager));
      dialog.open();
    } catch (Exception e) {
      errorService.handleException("Could not initialize Pepper modules", e, ImportHandler.class);
    }
  }
  
  @CanExecute
  protected static boolean canExecute() {
    return Activator.getPepper().isPresent();
  }
}
