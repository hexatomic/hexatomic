package org.corpus_tools.hexatomic.core.undo;

import java.io.File;
import java.io.IOException;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;

public class CorpusGraphModification implements ReversibleOperation {

  private final File temporaryCorpusGraph;

  /**
   * Creates a reversible checkpoint for a corpus graph change event.
   * 
   * @param graph The corpus graph that was changed.
   * @throws IOException Checkpoints use temporary files on disk. If creating these files or
   *         serializing the document graph fails, an exception is thrown.
   */
  public CorpusGraphModification(SCorpusGraph graph) throws IOException {
    this.temporaryCorpusGraph = File.createTempFile("hexatomic-checkpoint-corpusgraph-", ".salt");
    this.temporaryCorpusGraph.deleteOnExit();
    SaltUtil.saveCorpusGraph(graph, URI.createFileURI(this.temporaryCorpusGraph.getAbsolutePath()));
  }

  @Override
  public void restore(ProjectManager projectManager, IEventBroker events) {
    // TODO Auto-generated method stub

  }

}
