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
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class CloseSaltProjectHandler {

  @Inject
  private ProjectManager projectManager;

  @Inject
  private EPartService partService;

  /**
   * Close the current project.
   * 
   */
  @Execute
  public void execute() {
    // TODO: when save is implemented, check here for any modifications of the graph

    // Close all editors
    for (MPart part : partService.getParts()) {
      String docID = part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
      if (docID != null && !docID.isEmpty()) {
        partService.hidePart(part);
      }
    }

    projectManager.close();

  }
}
