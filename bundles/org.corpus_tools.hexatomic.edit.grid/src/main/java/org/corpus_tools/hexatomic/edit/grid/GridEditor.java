
package org.corpus_tools.hexatomic.edit.grid;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class GridEditor {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GridEditor.class);

  @Inject
  public GridEditor() {

  }

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @PostConstruct
  public void postConstruct(Composite parent) {
    log.debug("Starting Grid Editor for document '{}'.", getGraph().getDocument().getName());
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
