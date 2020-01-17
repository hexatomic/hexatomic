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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.edit.grid.data.GraphDataProvider;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

  /**
   * Consumes the selection of an {@link STextualDS} from the {@link ESelectionService}.
   * 
   * @param ds The {@link STextualDS} that has been selected via the {@link ESelectionService}.
   */
  @Inject
  void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) STextualDS ds) {
    if (ds != null) {
      log.debug("The textual data source {} has been selected.", ds.getId());
    } else {
      // Do nothing
    }
  }

  @Inject
  private ESelectionService selectionService;

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  private SDocumentGraph graph;

  /**
   * Creates the grid that contains the data of the {@link SDocumentGraph}.
   * 
   * @param parent The parent {@link Composite} widget of the part
   */
  @PostConstruct
  public void postConstruct(Composite parent) {
    this.graph = getGraph();
    log.debug("Starting Grid Editor for document '{}'.", graph.getDocument().getName());

    parent.setLayout(new GridLayout());

    /*
     * FIXME Add dropdown for text selection. Redo grid once text is selected. TODO REMOVE: One grid
     * per text, i.e., grids only ever work on one text!
     */
    addTextSelectionDropdown(parent);

    // Create data provider & layer, data layer needs to be most bottom layer in the stack!
    IDataProvider bodyDataProvider = new GraphDataProvider(graph);
    final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

    // Create and configure NatTable
    final NatTable natTable = new NatTable(parent, SWT.DOUBLE_BUFFERED | SWT.BORDER, bodyDataLayer);

    // Configure grid layout generically
    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
  }

  /**
   * Creates a dropdown presenting the available {@link STextualDS}s from the
   * {@link SDocumentGraph}. When a text is selected, the selection is forwarded to the injectable
   * {@link ESelectionService}, so that other parts can consume the selection. This part also
   * consumes the selection, in {@link #setSelection(STextualDS)}.
   * 
   * <p>
   * The following example shows how other parts can consume the selection.
   * </p>
   * 
   * <pre>
   * {@code
   *   &#64;Inject void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) 
   *       Object o) { 
   *     if (o instanceof STextualDS) { 
   *       STextualDS ds = (STextualDS) o;
   *       log.debug("The text {} has been selected.", ds.getId()); 
   *     } 
   *   }
   * }
   * </pre>
   * 
   * @param parent The parent composite
   */
  private void addTextSelectionDropdown(Composite parent) {
    final ComboViewer viewer = new ComboViewer(parent, SWT.READ_ONLY);
    viewer.setContentProvider(ArrayContentProvider.getInstance());
    viewer.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        if (element instanceof STextualDS) {
          STextualDS ds = (STextualDS) element;
          return ds.getName() + " (" + ds.getId() + ")";
        }
        return super.getText(element);
      }
    });
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if (selection.size() > 0 && selection.getFirstElement() instanceof STextualDS) {
          selectionService.setSelection(
              selection.size() == 1 ? selection.getFirstElement() : selection.toArray());
        }
      }
    });
    viewer.setInput(graph.getTextualDSs());
  }

  private SDocumentGraph getGraph() {
    String documentID = thisPart.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    java.util.Optional<SDocument> doc = projectManager.getDocument(documentID);
    if (doc.isPresent()) {
      return doc.get().getDocumentGraph();
    }
    return null;
  }

}
