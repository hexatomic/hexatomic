package org.corpus_tools.hexatomic.core.undo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.emf.common.util.URI;

public class Checkpoint {

  private Map<String, File> temporaryFiles = new HashMap<>();

  /**
   * Creates a new restorable checkpoint for a Salt project.
   * 
   * @param documents The IDs of the documents that have been changed compared to the previous
   *        checkpoint and will be restored.
   * @param projectManager The project manager, used to get the actual documents.
   * @throws IOException Checkpoints use temporary files on disk. If creating these files or
   *         serializing the document graph fails, an exception is thrown.
   */
  public Checkpoint(Collection<String> documents, ProjectManager projectManager)
      throws IOException {
    // Store all changed document in temporary files
    for (String doc : documents) {
      Optional<SDocument> loadedDocument = projectManager.getDocument(doc);
      if (loadedDocument.isPresent()) {
        File temporaryFileForDocument =
            File.createTempFile("hexatomic-checkpoint-documentgraph-", ".salt");
        temporaryFileForDocument.deleteOnExit();
        SaltUtil.saveDocumentGraph(
            loadedDocument.get()
                .getDocumentGraph(),
            URI.createFileURI(temporaryFileForDocument.getAbsolutePath()));
        temporaryFiles.put(doc, temporaryFileForDocument);
      }
    }
    // TODO: store the whole corpus graph including document annotations
  }
}
