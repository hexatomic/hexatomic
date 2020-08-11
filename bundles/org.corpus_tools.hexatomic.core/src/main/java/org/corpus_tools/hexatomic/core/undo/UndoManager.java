package org.corpus_tools.hexatomic.core.undo;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Stack;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
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
  private final List<ReversibleOperation> uncommittedChanges = new LinkedList<>();

  @Inject
  private ErrorService errors;

  @Inject
  private ProjectManager projectManager;

  @Inject
  private SaltNotificationFactory notificationFactory;

  @Inject
  IEventBroker events;

  /**
   * Adds a checkpoint. A user will be able to undo all changes made between checkpointss.
   */
  public void addCheckpoint() {
    if (!uncommittedChanges.isEmpty()) {
      // All recorded changes are reversable without saving and loading the whole document, create
      // a different kind of checkpoint using a copy of all uncomitted changes
      log.debug("Adding {} changes as checkpoint to undo list", uncommittedChanges.size());
      changeSets.add(new LinkedList<>(uncommittedChanges));
    }

    // All uncommitted changes have been handled: restore internal recored state to default:
    uncommittedChanges.clear();

    events.send(Topics.ANNOTATION_CHECKPOINT_CREATED, null);
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
      events.send(Topics.ANNOTATION_CHECKPOINT_RESTORED, null);

      // TODO: how to implement redo?
    }
  }


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeBeforeAnnotationModified(
      @UIEventTopic(Topics.ANNOTATION_BEFORE_MODIFICATION) Object element) {
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
    }
  }
}
