package org.corpus_tools.hexatomic.core.undo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.corpus_tools.salt.core.SGraph;

public abstract class ReversableCommand<G extends SGraph> {
  protected G current;
  protected Optional<File> memento;

  public abstract void execute() throws IOException;

  public abstract void unExecute() throws IOException;
}
