/*-
 * #%L
 * org.corpus_tools.hexatomic.edit.grid
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

package org.corpus_tools.hexatomic.edit.grid;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.edit.grid.data.GraphDataProvider;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Part providing a grid editor for {@link SDocument}s.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GridEditor {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GridEditor.class);

  @Inject
  public GridEditor() {

  }

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  /**
   * Creates the grid that contains the data of the {@link SDocumentGraph}.
   * 
   * @param parent The parent {@link Composite} widget of the part
   */
  @PostConstruct
  public void postConstruct(Composite parent) {
    log.debug("Starting Grid Editor for document '{}'.", getGraph().getDocument().getName());

    parent.setLayout(new GridLayout());

    // Create data provider & layer, data layer needs to be most bottom layer in the stack!
    IDataProvider bodyDataProvider = new GraphDataProvider(getGraph());
    final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

    // Create and configure NatTable
    final NatTable natTable = new NatTable(parent, SWT.DOUBLE_BUFFERED | SWT.BORDER, bodyDataLayer);

    // Configure grid layout generically
    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
  }

  private SDocumentGraph getGraph() {
    String documentID = thisPart.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    Optional<SDocument> doc = projectManager.getDocument(documentID);
    if (doc.isPresent()) {
      return doc.get().getDocumentGraph();
    }
    return null;
  }

}
