package org.corpus_tools.hexatomic.core.undo;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.services.events.IEventBroker;

public interface ReversableOperation {
  public void restore(ProjectManager projectManager, IEventBroker events);
}
