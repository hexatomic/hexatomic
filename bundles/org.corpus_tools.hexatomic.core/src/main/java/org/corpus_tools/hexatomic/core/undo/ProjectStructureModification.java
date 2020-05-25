package org.corpus_tools.hexatomic.core.undo;

import java.io.File;
import java.io.IOException;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.SaltUtil;
import org.corpus_tools.salt.util.internal.persistence.SaltXML10Writer;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;

public class ProjectStructureModification implements ReversibleOperation {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectStructureModification.class);

  private final File temporaryProjectFile;

  /**
   * Creates a reversible checkpoint for a project structure change event.
   * 
   * @param project The project that was changed.
   * @throws IOException Checkpoints use temporary files on disk. If creating these files or
   *         serializing the document graph fails, an exception is thrown.
   */
  public ProjectStructureModification(SaltProject project) throws IOException {
    this.temporaryProjectFile = File.createTempFile("hexatomic-checkpoint-corpusgraph-", ".salt");
    this.temporaryProjectFile.deleteOnExit();
    SaltXML10Writer writer = new SaltXML10Writer(this.temporaryProjectFile);
    writer.writeSaltProject(project);
  }

  @Override
  public void restore(ProjectManager projectManager, IEventBroker events) {
    
    // Load project from file
    Object object = SaltUtil.load(URI.createFileURI(temporaryProjectFile.getAbsolutePath()));
    if (object instanceof SaltProject) {
      SaltProject project = (SaltProject) object;
      projectManager.setProject(project);

    }

    if (!temporaryProjectFile.delete()) {
      log.warn("Could not delete temporary file {}", temporaryProjectFile.getAbsoluteFile());
    }

  }

}
