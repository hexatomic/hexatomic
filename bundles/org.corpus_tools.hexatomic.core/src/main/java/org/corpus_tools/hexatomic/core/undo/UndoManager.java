package org.corpus_tools.hexatomic.core.undo;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

@Creatable
@Singleton
public class UndoManager {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UndoManager.class);

  private final Stack<ChangeSet> changeSets = new Stack<>();
  private final List<ReversibleOperation> uncommittedChanges = new LinkedList<>();

  @Inject
  private SaltNotificationFactory notificationFactory;

  @Inject
  IEventBroker events;

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
      ChangeSet lastChangeSet = this.changeSets.pop();
      // Iterate over the elements in reversed order
      ListIterator<ReversibleOperation> itLastChangeSet =
          lastChangeSet.getChanges().listIterator(lastChangeSet.getChanges().size());
      while (itLastChangeSet.hasPrevious()) {
        ReversibleOperation op = itLastChangeSet.previous();
        op.restore();
      }
      notificationFactory.setSuppressingEvents(false);
      events.send(Topics.ANNOTATION_CHECKPOINT_RESTORED, lastChangeSet);

      // TODO: how to implement redo?
    }
  }

  /**
   * Commit all recorded changes into a new {@link ChangeSet} that can be reversed.
   * 
   * @return The created {@link ChangeSet} or null of none was created.
   */
  public ChangeSet commitCanges() {
    ChangeSet changes = null;
    if (!uncommittedChanges.isEmpty()) {
      // All recorded changes are reversable without saving and loading the whole document, create
      // a different kind of checkpoint using a copy of all uncomitted changes
      log.debug("Adding {} changes as checkpoint to undo list", uncommittedChanges.size());
      changes = new ChangeSet(uncommittedChanges);
      changeSets.add(changes);
    }

    // All uncommitted changes have been handled: restore internal recored state to default:
    uncommittedChanges.clear();

    return changes;
  }


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeUndoOperationAdded(
      @UIEventTopic(Topics.UNDO_OPERATION_ADDED) Object element) {
    if (element instanceof ReversibleOperation) {
      uncommittedChanges.add((ReversibleOperation) element);
    }
  }
}
