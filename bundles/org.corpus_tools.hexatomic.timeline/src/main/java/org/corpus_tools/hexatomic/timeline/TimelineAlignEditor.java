package org.corpus_tools.hexatomic.timeline;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.timeline.internal.data.GraphDataProvider;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TimelineAlignEditor {

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @Inject
  private GraphDataProvider bodyDataProvider;

  private SDocumentGraph graph;


  @PostConstruct
  public void postConstruct(Composite parent, MPart part, ProjectManager projectManager) {
    parent.setLayout(new GridLayout());

    this.graph = getGraph();
    bodyDataProvider.setGraph(graph);

    DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

    NatTable natTable = new NatTable(parent,
        SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER, bodyDataLayer);

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
