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
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
  private IEventBroker events;

  @Inject
  private MPart part;

  @Inject
  private Composite parent;

  /**
   * Creates a new text viewer.
   * 
   * @param parent The parent SWT object
   */
  @PostConstruct
  public void postConstruct(Composite parent) {
    parent.setLayout(new FillLayout(SWT.HORIZONTAL));
    updateView();
  }

  private void updateView() {
    // Remove any previously created text fields
    for (Control c : parent.getChildren()) {
      c.dispose();
    }
    
    // Get the document graph for this editor
    Optional<SDocument> document = getDocument(part);
    if (document.isPresent()) {
      SDocumentGraph graph = document.get().getDocumentGraph();
      // Add a gtext field for each textual data source
      for (STextualDS text : graph.getTextualDSs()) {
        Text textField =
            new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
        textField.setText(text.getText());
      }
    }
    parent.requestLayout();
  }

  @PreDestroy
  protected void cleanup(MPart part) {
    events.post(Topics.DOCUMENT_CLOSED,
        part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID));
  }


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void onDataChanged(@UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {
    if (element instanceof ChangeSet) {
      ChangeSet changeSet = (ChangeSet) element;
      if (changeSet.containsDocument(
          part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID))) {
        updateView();
      }
    }
  }

  /**
   * Retrieve the edited document from the global and the internal persisted state.
   * 
   * @return
   */
  private Optional<SDocument> getDocument(MPart part) {
    String documentID = part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
    return projectManager.getDocument(documentID);
  }

}
