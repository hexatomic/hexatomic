package org.corpus_tools.hexatomic.core.undo;

import java.util.Collection;
import java.util.Collections;
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
   * Return a set of all containers directly affected by this change.
   * 
   * @return The container objects.
   */
  public Set<Object> getChangedContainers() {
    Set<Object> result = new LinkedHashSet<>();

    for (ReversibleOperation op : this.changes) {
      Object container = op.getChangedContainer();
      if (container != null) {
        result.add(container);
      }
    }

    return result;
  }

  /**
   * Return a set of all elements directly affected by this change.
   * 
   * @return The elements.
   */
  public Set<Object> getChangedElements() {
    Set<Object> result = new LinkedHashSet<>();

    for (ReversibleOperation op : this.changes) {
      Object container = op.getChangedElement();
      if (container != null) {
        result.add(container);
      }
    }

    return result;
  }

  public List<ReversibleOperation> getChanges() {
    return Collections.unmodifiableList(changes);
  }

}
