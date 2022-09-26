package org.corpus_tools.hexatomic.timeline;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.timeline.internal.data.TextualDsHeaderDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TimelineTokenDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TliCornerDataProvider;
import org.corpus_tools.hexatomic.timeline.internal.data.TliRowHeaderDataProvider;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimelineAlignEditor {

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

    NatTable natTable = new NatTable(parent,
        SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL,
        gridLayer);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
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
