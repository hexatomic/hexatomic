package org.corpus_tools.hexatomic.core.undo;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.graph.Label;

/**
 * Operation that represents a modification of a label (like setting the value, name etc.).
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public class LabelModification implements ReversibleOperation {

  private final Label label;

  private final String namespace;
  private final String name;
  private final Object value;

  /**
   * Creates a new label modification event.
   * 
   * @param label The label that changed.
   */
  public LabelModification(Label label) {

    this.label = label;

    this.namespace = label.getNamespace();
    this.name = label.getName();
    this.value = label.getValue();
  }

  @Override
  public void restore(ProjectManager projectManager) {
    label.setNamespace(namespace);
    label.setName(name);
    label.setValue(value);
  }

}
