/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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

import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenSaltProjectHandler {

  @Inject
  private ProjectManager projectManager;

  private String lastPath;

  /**
   * Show a file choose to open Salt project.
   * 
   * @param shell The user interface shell
   * @param location An optional predefined location. If null, the use is asked to select a location
   *        with a file chooser.
   */
  @Execute
  public void execute(Shell shell, EModelService modelService, MApplication app,
      @Optional @Named(CommandParams.LOCATION) String location) {
    String resultPath;
    if (location == null) {
      DirectoryDialog dialog = new DirectoryDialog(shell);
      if (lastPath != null) {
        dialog.setFilterPath(lastPath);
      }
      // Ask the user to choose a path
      resultPath = dialog.open();
    } else {
      // Use the command argument as location
      resultPath = location;
    }

    if (resultPath != null) {
      projectManager.open(URI.createFileURI(resultPath));
      lastPath = resultPath;

      // Change the title of the application
      MUIElement mainWindow = modelService.find("org.eclipse.e4.window.main", app);
      if (mainWindow instanceof MWindow) {
        ((MWindow) mainWindow).setLabel("Hexatomic - " + resultPath);
      }
    }
  }
}
