package org.corpus_tools.hexatomic.core.undo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
@Singleton
public class UndoManager implements Listener {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(UndoManager.class);

  private final Stack<SaltUpdateEvent> inconsistantEvents = new Stack<>();

  private final Stack<Checkpoint> checkpoints = new Stack<>();

  @Inject
  private ErrorService errors;

  @Inject
  private ProjectManager projectManager;

  @PostConstruct
  private void init() {
    // We need to receive all updates on all document graphs
    projectManager.addListener(this);
  }

  /**
   * Adds a checkpoint. A user will be able to undo all changes made after these checkpoint.
   */
  public void addCheckpoint() {
    // Go through all inconsistent events and collect the document graphs we need to save for this
    // checkpoint
    Set<String> documents = new HashSet<>();
    for (SaltUpdateEvent e : this.inconsistantEvents) {
      IdentifiableElement element = null;
      if (e.getContainer() instanceof IdentifiableElement) {
        element = (IdentifiableElement) e.getContainer();
      } else if (e.getContainer() instanceof org.corpus_tools.salt.graph.Label) {
        element = ((org.corpus_tools.salt.graph.Label) e.getContainer()).getContainer();
      }
      if (element != null && element.getId() != null) {
        documents.add(element.getId());
      }
    }
    try {
      checkpoints.add(new Checkpoint(documents, projectManager));
      log.debug("Added new checkpoint to undo list");
    } catch (IOException ex) {
      errors.handleException("Undo checkpoint creation failed", ex, UndoManager.class);
    }

  }

  @Override
  public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
      Object newValue, Object container) {
    // Add this event to our list of events that happened between two checkpoint commands
    inconsistantEvents.add(new SaltUpdateEvent(type, attribute, oldValue, newValue, container));
  }
}
