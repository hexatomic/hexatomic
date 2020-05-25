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
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;

public class DocumentGraphModifications implements ReversableOperation {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(DocumentGraphModifications.class);

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
  public DocumentGraphModifications(Collection<String> documents, ProjectManager projectManager)
      throws IOException {
    // Store all changed document in temporary files
    for (String doc : documents) {
      Optional<SDocument> loadedDocument = projectManager.getDocument(doc);
      if (loadedDocument.isPresent()) {
        File temporaryFileForDocument =
            File.createTempFile("hexatomic-checkpoint-documentgraph-", ".salt");
        temporaryFileForDocument.deleteOnExit();
        SaltUtil.saveDocumentGraph(loadedDocument.get()
            .getDocumentGraph(),
            URI.createFileURI(temporaryFileForDocument.getAbsolutePath()));
        temporaryFiles.put(doc, temporaryFileForDocument);
        log.debug("Adding document {} with temporary file {} to checkpoint", doc,
            temporaryFileForDocument.getAbsoluteFile());

      }
    }
  }

  /**
   * Restores all document that belong to this checkpoint.
   * 
   * @param projectManager The project manager, used to get and set the actual documents.
   * @param events An event broker used to send load events.
   */
  public void restore(ProjectManager projectManager, IEventBroker events) {
    for (Map.Entry<String, File> entry : temporaryFiles.entrySet()) {
      String documentID = entry.getKey();
      File temporaryFileForDocument = entry.getValue();

      // Load document graph from file
      projectManager.loadDocumentFrom(documentID, temporaryFileForDocument);

      // Delete the temporary file
      if (!temporaryFileForDocument.delete()) {
        log.warn("Could not delete temporary file {}", temporaryFileForDocument.getAbsoluteFile());
      }
    }

    temporaryFiles.clear();
  }
}
