package org.corpus_tools.hexatomic.core.undo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.emf.common.util.URI;

public class GenericDocumentGraphCommand extends ReversableCommand<SDocumentGraph> {

  @Override
  public void execute() throws IOException {
    // Save the current state as temporary file and remember its location
    File tempFile = File.createTempFile("hexatomic-document-graph-state-", ".salt");
    tempFile.deleteOnExit();
    SaltUtil.saveDocumentGraph(this.current, URI.createFileURI(tempFile.getAbsolutePath()));
    this.memento = Optional.of(tempFile);

  }

  @Override
  public void unExecute() throws IOException {
    // Restore state from Memento
    if (this.memento.isPresent()) {
      // Load from file
      SDocumentGraph graph =
          SaltUtil.loadDocumentGraph(URI.createFileURI(this.memento.get().getAbsolutePath()));
      this.current = graph;
    }
  }

}
