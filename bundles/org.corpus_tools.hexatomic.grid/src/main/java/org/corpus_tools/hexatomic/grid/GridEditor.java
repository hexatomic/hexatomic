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

package org.corpus_tools.hexatomic.grid;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.data.ColumnHeaderDataProvider;
import org.corpus_tools.hexatomic.grid.data.GraphDataProvider;
import org.corpus_tools.hexatomic.grid.data.RowHeaderDataProvider;
import org.corpus_tools.hexatomic.grid.style.SelectionStyleConfiguration;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Part providing a grid editor for {@link SDocument}s.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GridEditor {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GridEditor.class);

  @Inject
  ErrorService errors;

  @Inject
  private ESelectionService selectionService;

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @Inject
  private GraphDataProvider bodyDataProvider;

  private SDocumentGraph graph;

  private NatTable table;

  /**
   * Creates the grid that contains the data of the {@link SDocumentGraph}.
   * 
   * @param parent The parent {@link Composite} widget of the part
   */
  @PostConstruct
  public void postConstruct(Composite parent) {
    this.graph = getGraph();
    bodyDataProvider.setGraph(graph);
    log.debug("Starting Grid Editor for document '{}'.", graph.getDocument().getName());

    parent.setLayout(new GridLayout());

    // Add dropdown for text selection.
    addTextSelectionDropdown(parent);

    // Create data provider & layer, data layer needs to be most bottom layer in the stack!
    AutomaticSpanningDataProvider spanningDataProvider =
        new AutomaticSpanningDataProvider(bodyDataProvider, false, true);
    final SpanningDataLayer bodyDataLayer = new SpanningDataLayer(spanningDataProvider);

    // Create selection and viewport layers
    final SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
    selectionLayer.addConfiguration(new SelectionStyleConfiguration());
    final ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

    // Create column headers
    IDataProvider columnHeaderDataProvider = new ColumnHeaderDataProvider(bodyDataProvider);
    DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);
    final ILayer columnHeaderLayer =
        new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

    // Create row headers
    IDataProvider rowHeaderDataProvider = new RowHeaderDataProvider(bodyDataProvider);
    DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
    final ILayer rowHeaderLayer =
        new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

    // Create corner layer
    final ILayer cornerLayer = new CornerLayer(
        new DataLayer(
            new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)),
        rowHeaderLayer, columnHeaderLayer);

    // Combine layers in composite layer
    CompositeLayer compositeLayer = new CompositeLayer(2, 2);
    compositeLayer.setChildLayer(GridRegion.CORNER, cornerLayer, 0, 0);
    compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 1, 0);
    compositeLayer.setChildLayer(GridRegion.ROW_HEADER, rowHeaderLayer, 0, 1);
    compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 1, 1);

    // Create and configure NatTable
    table = new NatTable(parent, SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL,
        compositeLayer);

    // Configure grid layout generically
    GridDataFactory.fillDefaults().grab(true, true).applyTo(table);

  }

  /**
   * Consumes the selection of an {@link STextualDS} from the {@link ESelectionService}.
   * 
   * @param ds The {@link STextualDS} that has been selected via the {@link ESelectionService}.
   */
  @Inject
  void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) STextualDS ds) {
    if (ds != null) {
      log.debug("The textual data source {} has been selected.", ds.getId());
      bodyDataProvider.setDsAndResolveGraph(ds);
    } else {
      // Do nothing
    }
  }

  /**
   * Creates a dropdown (with label) presenting the available {@link STextualDS}s from the
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
    Composite dropdownGroup = new Composite(parent, SWT.NONE);
    dropdownGroup.setLayout(new GridLayout(2, false));
    GridDataFactory.fillDefaults().grab(true, false).applyTo(dropdownGroup);

    Label label = new Label(dropdownGroup, SWT.NONE);
    label.setText("Data source:");

    final ComboViewer viewer = new ComboViewer(dropdownGroup, SWT.READ_ONLY);
    viewer.setContentProvider(ArrayContentProvider.getInstance());
    viewer.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        if (element instanceof STextualDS) {
          STextualDS ds = (STextualDS) element;
          return ds.getName();
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
    if (graph.getTextualDSs().size() == 1) {
      viewer.setSelection(new StructuredSelection(graph.getTextualDSs().get(0)));
    }
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
