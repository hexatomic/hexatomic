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
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
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
   * @param forceCloseRaw Whether to force closing as raw string (e.g. "true")
   */
  @Execute
  public void execute(Shell shell, @Optional @Named(CommandParams.LOCATION) String location,
      @Optional @Named(CommandParams.FORCE_CLOSE) String forceCloseRaw) {

    boolean forceClose = Boolean.parseBoolean(forceCloseRaw);
    if (!forceClose && projectManager.isDirty()) {
      // Ask user if project should be closed even with unsaved changes
      boolean confirmed = MessageDialog.openConfirm(shell, "Discard unsaved changes?",
          "There are unsaved changes in the project that will be lost if you close it. "
              + "Do you really want to close the project and open a new one?");
      if (!confirmed) {
        return;
      }
    }

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
    }
  }
}
