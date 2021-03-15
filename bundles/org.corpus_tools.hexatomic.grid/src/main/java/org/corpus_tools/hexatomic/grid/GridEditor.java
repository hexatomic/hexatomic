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
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.hexatomic.grid.internal.bindings.FreezeGridBindings;
import org.corpus_tools.hexatomic.grid.internal.configuration.BodyMenuConfiguration;
import org.corpus_tools.hexatomic.grid.internal.configuration.ColumnHeaderMenuConfiguration;
import org.corpus_tools.hexatomic.grid.internal.configuration.EditConfiguration;
import org.corpus_tools.hexatomic.grid.internal.configuration.GridLayerConfiguration;
import org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider;
import org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider;
import org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator;
import org.corpus_tools.hexatomic.grid.internal.data.NodeSpanningDataProvider;
import org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider;
import org.corpus_tools.hexatomic.grid.internal.layers.GridColumnHeaderLayer;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.corpus_tools.hexatomic.grid.internal.style.SelectionStyleConfiguration;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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

  private static final String NO_TOKENS_MESSAGE =
      "The data source does not contain any tokens, and cannot be displayed.";

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GridEditor.class);

  public static final String DELETE_CELLS_POPUP_MENU_LABEL = "Delete cell(s)";

  public static final String CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL = "Change annotation name";

  public static final String CREATE_SPAN_POPUP_MENU_LABEL = "Create span";

  @Inject
  ErrorService errors;

  @Inject
  private IEventBroker events;

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

  private STextualDS activeDs = null;

  /**
   * Creates the grid that contains the data of the {@link SDocumentGraph}.
   * 
   * @param parent The parent {@link Composite} widget of the part
   */
  @PostConstruct
  public void postConstruct(Composite parent) {
    this.graph = getGraph();
    bodyDataProvider.setGraph(graph);
    if (graph != null && graph.getDocument() != null) {
      log.debug("Starting Grid Editor for document '{}'.", graph.getDocument().getName());
    }

    parent.setLayout(new GridLayout());

    // Add dropdown for text selection.
    addTextSelectionDropdown(parent);

    // Create data provider & layer, data layer needs to be most bottom layer in the stack!
    NodeSpanningDataProvider spanningDataProvider = new NodeSpanningDataProvider(bodyDataProvider);
    final SpanningDataLayer bodyDataLayer = new SpanningDataLayer(spanningDataProvider);

    // Body
    final DefaultBodyLayerStack bodyLayer = new DefaultBodyLayerStack(bodyDataLayer);

    // Set accumulator for config labels for cells
    final LabelAccumulator labelAccumulator = new LabelAccumulator(bodyDataLayer, bodyDataProvider);
    bodyDataLayer.setConfigLabelAccumulator(labelAccumulator);

    // Selection
    final SelectionLayer selectionLayer = bodyLayer.getSelectionLayer();
    selectionLayer.addConfiguration(new SelectionStyleConfiguration());
    final FreezeLayer freezeLayer = new FreezeLayer(selectionLayer);
    final GridFreezeLayer compositeFreezeLayer = new GridFreezeLayer(freezeLayer,
        bodyLayer.getViewportLayer(), selectionLayer, bodyDataProvider);

    // Column header
    final IDataProvider columnHeaderDataProvider =
        new ColumnHeaderDataProvider(bodyDataProvider, projectManager);
    final GridColumnHeaderLayer columnHeaderLayer =
        new GridColumnHeaderLayer(new DefaultColumnHeaderDataLayer(columnHeaderDataProvider),
            compositeFreezeLayer, selectionLayer);

    // Row header
    final IDataProvider rowHeaderDataProvider = new RowHeaderDataProvider(bodyDataProvider);
    final ILayer rowHeaderLayer = new RowHeaderLayer(
        new DefaultRowHeaderDataLayer(rowHeaderDataProvider), compositeFreezeLayer, selectionLayer);

    // Corner
    final DefaultCornerDataProvider cornerDataProvider =
        new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
    final CornerLayer cornerLayer =
        new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

    // Grid
    final GridLayer gridLayer =
        new GridLayer(compositeFreezeLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, false);
    gridLayer.addConfiguration(new GridLayerConfiguration(gridLayer));

    table = new NatTable(parent, gridLayer, false);

    // Configuration
    table.addConfiguration(new StyleConfiguration());
    table.addConfiguration(new ColumnHeaderMenuConfiguration(table));
    table.addConfiguration(new FreezeGridBindings());
    table.addConfiguration(
        new EditConfiguration(bodyDataProvider, labelAccumulator, selectionLayer));
    table.addConfiguration(new BodyMenuConfiguration(table, selectionLayer));

    table.configure();

    // Configure grid layout generically
    GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
  }

  @PreDestroy
  public void cleanup(MPart part) {
    events.post(Topics.DOCUMENT_CLOSED,
        part.getPersistedState().get("org.corpus_tools.hexatomic.document-id"));
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void onDataChanged(@UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {
    if (element instanceof ChangeSet) {
      ChangeSet changeSet = (ChangeSet) element;
      if (changeSet.containsDocument(
          thisPart.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID))) {
        setSelection(getActiveDs());
      }
    }
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
      setActiveDS(ds);
      bodyDataProvider.setDsAndResolveGraph(ds);
      // Refresh all layers
      table.refresh();
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
    final Label messageLabel = new Label(parent, SWT.NONE);
    Composite dropdownGroup = new Composite(parent, SWT.NONE);
    dropdownGroup.setLayout(new GridLayout(2, false));
    GridDataFactory.fillDefaults().grab(true, false).applyTo(dropdownGroup);

    Label label = new Label(dropdownGroup, SWT.NONE);
    label.setText("Data source:");

    final ComboViewer viewer = new ComboViewer(dropdownGroup, SWT.READ_ONLY);

    final ControlDecoration deco = new ControlDecoration(viewer.getControl(), SWT.TOP | SWT.RIGHT);
    deco.setShowOnlyOnFocus(false);

    viewer.setContentProvider(ArrayContentProvider.getInstance());
    viewer.setLabelProvider(createLabelProvider());
    viewer.addSelectionChangedListener(createSelectionChangeListener(messageLabel, parent, deco));
    viewer.setInput(graph.getTextualDSs());
    if (graph.getTextualDSs().size() == 1) {
      viewer.setSelection(new StructuredSelection(graph.getTextualDSs().get(0)));
    } else {
      messageLabel.setText("Please select a data source!");
      messageLabel.setVisible(true);
      parent.layout();
    }
  }

  private void changeTableVisibility(NatTable currTable, Composite currParent, Boolean visible) {
    if (currTable != null) {
      currTable.setVisible(visible);
      currParent.layout();
    }

  }

  private ISelectionChangedListener createSelectionChangeListener(Label messageLabel,
      Composite parent, ControlDecoration deco) {
    return event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.size() > 0 && selection.getFirstElement() instanceof STextualDS) {
        // Dispose select message, once a selection is made, selection can never be null or empty
        // again.
        messageLabel.dispose();
        parent.layout();
        // Set decoration depending on whether there are tokens in the data source.
        // This is found out by checking whether the data source has incoming relations of type
        // STextualRelation.
        if (((STextualDS) selection.getFirstElement()).getInRelations().stream()
            .noneMatch(STextualRelation.class::isInstance)) {
          deco.setDescriptionText(NO_TOKENS_MESSAGE);
          Image errorImage = FieldDecorationRegistry.getDefault()
              .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
          deco.setImage(errorImage);
          changeTableVisibility(table, parent, false);
        } else {
          deco.setDescriptionText(null);
          deco.setImage(null);
          selectionService.setSelection(
              selection.size() == 1 ? selection.getFirstElement() : selection.toArray());
          changeTableVisibility(table, parent, true);
        }
      }
    };
  }

  private LabelProvider createLabelProvider() {
    return new LabelProvider() {
      @Override
      public String getText(Object element) {
        if (element instanceof STextualDS) {
          STextualDS ds = (STextualDS) element;
          return ds.getName();
        }
        return super.getText(element);
      }
    };
  }

  /**
   * Sets the active textual data source the table displays.
   * 
   * @param ds The active data source.
   */
  private void setActiveDS(STextualDS ds) {
    this.activeDs = ds;

  }

  /**
   * Returns the currently active textual data source.
   * 
   * @return the currently active textual data source
   */
  private STextualDS getActiveDs() {
    return this.activeDs;
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
