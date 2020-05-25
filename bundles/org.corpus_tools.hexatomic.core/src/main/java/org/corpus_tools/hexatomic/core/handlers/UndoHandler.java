package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.undo.UndoManager;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class UndoHandler {

  @Inject
  private UndoManager undoManager;

  @Execute
  public void execute() {
    undoManager.undo();
  }

  @CanExecute
  public boolean canExecute() {
    return undoManager.canUndo();
  }

}
