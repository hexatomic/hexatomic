package org.corpus_tools.hexatomic.core.undo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

@Creatable
@Singleton
public class UndoManager implements Listener {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UndoManager.class);

  private final Stack<SaltUpdateEvent> inconsistantEvents = new Stack<>();

  private final Stack<Checkpoint> checkpoints = new Stack<>();

  @Inject
  private ErrorService errors;

  @Inject
  private ProjectManager projectManager;

  @Inject
  IEventBroker events;

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
    Set<String> documents = inconsistentDocuments();
    try {
      checkpoints.add(new Checkpoint(documents, projectManager));
      this.inconsistantEvents.clear();
      log.debug("Added new checkpoint to undo list");
      // TODO: if the changes are reversable without saving and loading the whole document, create a
      // different kind of checkpoint
    } catch (IOException ex) {
      errors.handleException("Undo checkpoint creation failed", ex, UndoManager.class);
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void documentLoaded(@UIEventTopic(Topics.DOCUMENT_LOADED) String changedDocumentId) {
    Optional<SDocument> loadedDocument = projectManager.getDocument(changedDocumentId);
    if (loadedDocument.isPresent()) {
      this.inconsistantEvents.add(new SaltUpdateEvent(loadedDocument.get().getId()));
    }
  }

  /**
   * Collects the IDs of all currently inconsistent documents.
   * 
   * @return The document IDs.
   */
  private Set<String> inconsistentDocuments() {
    Set<String> documents = new HashSet<>();
    for (SaltUpdateEvent e : this.inconsistantEvents) {
      if (e.getDocumentID().isPresent()) {
        documents.add(e.getDocumentID().get());
      }
    }
    return documents;
  }

  /**
   * Checks whether it is possible to perform an undo.
   * 
   * @return True if undo is possible.
   */
  public boolean canUndo() {
    return !checkpoints.isEmpty() && !inconsistantEvents.isEmpty();
  }

  /**
   * Undoes all changes made since the last checkpoint was added.
   */
  public void undo() {
    if (canUndo()) {
      // Restore all documents from the last checkpoint
      this.checkpoints.pop().restore(projectManager, events);
      // TODO: how to implement redo?
    }
  }

  @Override
  public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
      Object newValue, Object container) {
    // Add this event to our list of events that happened between two checkpoint commands
    inconsistantEvents.add(new SaltUpdateEvent(type, attribute, oldValue, newValue, container));
  }
}
