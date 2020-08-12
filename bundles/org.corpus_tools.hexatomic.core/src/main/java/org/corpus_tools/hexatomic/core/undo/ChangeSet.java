package org.corpus_tools.hexatomic.core.undo;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ChangeSet {

  private final List<ReversibleOperation> changes = new LinkedList<>();

  public ChangeSet(Collection<ReversibleOperation> changes) {
    this.changes.addAll(changes);
  }

  /**
   * Return a set of all containers affected directly by this change.
   * 
   * @return The container objects.
   */
  public Set<Object> getChangedContainers() {
    Set<Object> result = new LinkedHashSet<>();

    for (ReversibleOperation op : this.changes) {
      Object container = op.getContainer();
      if (container != null) {
        result.add(container);
      }
    }

    return result;
  }

  List<ReversibleOperation> getChanges() {
    return changes;
  }

}
