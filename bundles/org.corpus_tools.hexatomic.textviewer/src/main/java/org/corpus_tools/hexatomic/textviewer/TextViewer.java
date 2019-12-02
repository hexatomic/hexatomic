/*-
 * #%L
 * org.corpus_tools.hexatomic.textviewer
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

package org.corpus_tools.hexatomic.textviewer;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a simple viewer for textual datasources of a Salt document graph.
 * 
 * @author Thomas Krause
 *
 */
public class TextViewer {


  @Inject
  private ProjectManager projectManager;

  @Inject
  public TextViewer() {

  }


  /**
   * Creates a new text viewer.
   * 
   * @param parent The parent SWT object
   * @param part Part this text viewer is part of
   */
  @PostConstruct
  public void postConstruct(Composite parent, MPart part) {
    parent.setLayout(new FillLayout(SWT.HORIZONTAL));

    // Get the document graph for this editor
    Optional<SDocument> document = getDocument(part);
    if (document.isPresent()) {
      SDocumentGraph graph = document.get().getDocumentGraph();
      for (STextualDS text : graph.getTextualDSs()) {
        Text textField =
            new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
        textField.setText(text.getText());
      }
    }

  }

  /**
   * Unloads the document graph.
   */
  @PreDestroy
  public void cleanup(MPart part) {
    Optional<SDocument> document = getDocument(part);
    if (document.isPresent()) {
      document.get().setDocumentGraph(null);
    }
  }

  /**
   * Retrieve the edited document from the global and the internal persisted state.
   * 
   * @return
   */
  private Optional<SDocument> getDocument(MPart part) {
    String documentID = part.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    return projectManager.getDocument(documentID);
  }

}
