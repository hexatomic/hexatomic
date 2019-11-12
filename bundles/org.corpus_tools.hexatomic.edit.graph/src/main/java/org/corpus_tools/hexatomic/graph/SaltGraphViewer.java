package org.corpus_tools.hexatomic.graph;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class SaltGraphViewer {


  @Inject
  private ProjectManager projectManager;

  @Inject
  public SaltGraphViewer() {}


  private GraphViewer viewer;
  
  private ZoomManager zoomManager;


  /**
   * Retrieve the edited document from the global and the internal persisted state.
   * 
   * @return
   */
  private Optional<SDocument> getDocument(MPart part) {
    String documentID = part.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    return projectManager.getDocument(documentID);
  }



  @PostConstruct
  public void postConstruct(Composite parent, MPart part) {
    
    viewer = new GraphViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new SaltGraphContentProvider());
    viewer.setLabelProvider(new SaltLabelProvider());
    viewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
    
    zoomManager = new ZoomManager(viewer.getGraphControl().getRootLayer(), viewer.getGraphControl().getViewport());
    
    viewer.getGraphControl().addMouseWheelListener(new MouseWheelListener() {
      
      @Override
      public void mouseScrolled(MouseEvent e) {
        if(e.count < 0 ) {
          zoomManager.zoomOut();
        } else {
          zoomManager.zoomIn();
        }
        
      }
    });
    
    
    Optional<SDocument> doc = getDocument(part);
    if(doc.isPresent()) {
      viewer.setInput(doc.get().getDocumentGraph());
    }
    

    viewer.getControl().forceFocus();

  }
}
