package org.corpus_tools.hexatomic.core.undo;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.graph.Label;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

@Creatable
@Singleton
public class UndoManager {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UndoManager.class);

  private final Stack<List<ReversibleOperation>> changeSets = new Stack<>();
  private final Set<String> inconsistantDocuments = new HashSet<>();
  private final List<ReversibleOperation> uncommittedChanges = new LinkedList<>();
  private boolean projectStructureIsInconsistant = false;

  @Inject
  private ErrorService errors;

  @Inject
  private ProjectManager projectManager;

  @Inject
  private SaltNotificationFactory notificationFactory;

  @Inject
  IEventBroker events;

  /**
   * Adds a checkpoint. A user will be able to undo all changes made after these checkpoint.
   */
  public void addCheckpoint() {
    if (projectStructureIsInconsistant) {
      try {
        // Events without document as container are related to updates of the project structure
        log.debug("Adding project structure state as checkpoint to undo list");
        List<ReversibleOperation> currentChangeSet = new LinkedList<>();
        currentChangeSet.add(new ProjectStructureModification(projectManager.getProject()));
        changeSets.add(currentChangeSet);
      } catch (IOException ex) {
        errors.handleException("Could not add undo savepoint for project", ex, UndoManager.class);
      }
    }
    if (!inconsistantDocuments.isEmpty()) {
      // Go through all inconsistent events and merge them into a new combined event.
      // Currently, the strategy is to collect the documents that are affected by these events and
      // create a single DocumentGraphModifications event.
      try {
        log.debug("Adding {} documents as checkpoint to undo list", inconsistantDocuments.size());
        List<ReversibleOperation> currentChangeSet = new LinkedList<>();
        currentChangeSet.add(new DocumentGraphModifications(inconsistantDocuments, projectManager));
        changeSets.add(currentChangeSet);
      } catch (IOException ex) {
        errors.handleException("Undo checkpoint creation failed", ex, UndoManager.class);
      }
    } else if (!uncommittedChanges.isEmpty()) {
      // All recorded changes are reversable without saving and loading the whole document, create
      // a different kind of checkpoint using a copy of all uncomitted changes
      log.debug("Adding {} changes as checkpoint to undo list", uncommittedChanges.size());
      changeSets.add(new LinkedList<>(uncommittedChanges));
    }

    // All uncommitted changes have been handled: restore internal recored state to default:
    projectStructureIsInconsistant = false;
    inconsistantDocuments.clear();
    uncommittedChanges.clear();
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void documentLoaded(@UIEventTopic(Topics.DOCUMENT_LOADED) String changedDocumentId) {
    Optional<SDocument> loadedDocument = projectManager.getDocument(changedDocumentId);
    if (loadedDocument.isPresent()) {
      this.inconsistantDocuments.add(loadedDocument.get().getId());
    }
  }


  /**
   * Checks whether it is possible to perform an undo.
   * 
   * @return True if undo is possible.
   */
  public boolean canUndo() {
    return !changeSets.isEmpty();
  }

  /**
   * Undoes all changes made since the last checkpoint was added.
   */
  public void undo() {
    if (canUndo()) {
      notificationFactory.setSuppressingEvents(true);
      // Restore all documents from the last checkpoint
      List<ReversibleOperation> lastChangeSet = this.changeSets.pop();
      // Iterate over the elements in reversed order
      ListIterator<ReversibleOperation> itLastChangeSet =
          lastChangeSet.listIterator(lastChangeSet.size());
      while (itLastChangeSet.hasPrevious()) {
        ReversibleOperation op = itLastChangeSet.previous();
        op.restore(projectManager);
      }
      notificationFactory.setSuppressingEvents(false);

      // TODO: how to implement redo?
    }
  }


  private void addGenericChange(Object element) {
    Optional<SGraph> graph = SaltHelper.getGraphForObject(element, SGraph.class);
    if (graph.isPresent()) {
      if (graph.get() instanceof SDocumentGraph) {
        inconsistantDocuments.add(((SDocumentGraph) graph.get()).getId());
      } else {
        projectStructureIsInconsistant = true;
      }
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeBeforeAnnotationModified(
      @UIEventTopic(Topics.ANNOTATION_AFTER_MODIFICATION) Object element) {
    Optional<SGraph> graph = SaltHelper.getGraphForObject(element, SGraph.class);
    // Only record changes for annotations that belong to a graph
    if (graph.isPresent()) {
      if (element instanceof Label) {
        Label label = (Label) element;
        if (label.getValue() != null) {
          uncommittedChanges.add(new LabelModification(label));
          return;
        }
      }
      addGenericChange(element);
    }
  }


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeAnnotationAdded(@UIEventTopic(Topics.ANNOTATION_ADDED) Object element) {
    addGenericChange(element);
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeAnnotationRemoved(@UIEventTopic(Topics.ANNOTATION_REMOVED) Object element) {
    addGenericChange(element);
  }
}
