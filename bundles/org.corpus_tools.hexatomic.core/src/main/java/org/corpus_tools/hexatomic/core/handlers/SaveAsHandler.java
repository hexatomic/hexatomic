/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

public class SaveAsHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;


  private String lastPath;


  /**
   * Saves the Salt project and all opened documents.
   * 
   * @param shell The user interface shell
   * @param location If non-null, save the project to this location. If null, use the original
   *        location from where the project was loaded.
   */
  @Execute
  public void execute(Shell shell,
      @Optional @Named(CommandParams.LOCATION) String location) {

    String resultPath;
    if (location == null) {
      DirectoryDialog dialog = new DirectoryDialog(shell);

      if (lastPath == null && projectManager.getLocation().isPresent()) {
        // The user did not specifically selected a path to save yet, but we can use the original
        // path from where the corpus was loaded.
        lastPath = projectManager.getLocation().get().toFileString();
      }

      if (lastPath != null) {
        dialog.setFilterPath(lastPath);
      }
      // Ask the user to choose a path
      resultPath = dialog.open();
    } else {
      // Use the given argument as location
      resultPath = location;
    }

    if (resultPath != null) {
      projectManager.saveTo(URI.createFileURI(resultPath), shell);
      lastPath = resultPath;
    }
  }

  @CanExecute
  public boolean canExecute() {
    return projectManager.isDirty() || projectManager.getLocation().isPresent();
  }
}
