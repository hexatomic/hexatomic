package org.corpus_tools.hexatomic.timeline;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.hexatomic.timeline.internal.data.TextualDsHeaderDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TimelineTokenDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TliCornerDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TliRowHeaderDataProvider;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class TimelineTokenAligner {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(TimelineTokenAligner.class);


  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @Inject
  private TimelineTokenDataProvider bodyDataProvider;

  @Inject
  private TextualDsHeaderDataProvider columnHeaderDataProvider;

  @Inject
  private TliRowHeaderDataProvider tliRowDataProvider;


  private SDocumentGraph graph;


  private NatTable natTable;


  @PostConstruct
  public void postConstruct(Composite parent, MPart part, ProjectManager projectManager) {
    parent.setLayout(new GridLayout());

    this.graph = getGraph();
    bodyDataProvider.setGraph(graph);
    columnHeaderDataProvider.setGraph(graph);
    tliRowDataProvider.setGraph(graph);

    // Define scrollable body layer
    DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
    SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
    selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());
    ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
    viewportLayer.setRegionName(GridRegion.BODY);

    // Header layer
    DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);
    ILayer columnHeaderLayer =
        new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

    // Row layer
    DataLayer rowHeaderDataLayer = new DataLayer(tliRowDataProvider);
    ILayer rowHeaderLayer =
        new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

    // Corner
    final TliCornerDataProvider cornerDataProvider =
        new TliCornerDataProvider(columnHeaderDataProvider, tliRowDataProvider);
    final CornerLayer cornerLayer =
        new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);


    // Create the grid layer with header and body
    final GridLayer gridLayer =
        new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, false);
    gridLayer.addConfiguration(new DefaultGridLayerConfiguration(gridLayer));

    Button btnAddText = new Button(parent, SWT.NONE);
    btnAddText.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        graph.createTextualDS("");
        projectManager.addCheckpoint();
      }
    });
    btnAddText.setText("Add textual data source");

    natTable = new NatTable(parent,
        SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL,
        gridLayer, false);
    natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
    natTable.addConfiguration(new AbstractRegistryConfiguration() {
      @Override
      public void configureRegistry(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
            IEditableRule.ALWAYS_EDITABLE);

      }
    });

    natTable.configure();
    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

  }

  
  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void onCheckpointCreated(
      @UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {

    if (element instanceof ChangeSet) {
      ChangeSet changeSet = (ChangeSet) element;
      log.debug("Received ANNOTATION_CHANGED event for changeset {}", changeSet);

      // check graph updates contain changes for this graph
      if (changeSet.containsDocument(
          thisPart.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID))) {

        natTable.refresh();
      }
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
