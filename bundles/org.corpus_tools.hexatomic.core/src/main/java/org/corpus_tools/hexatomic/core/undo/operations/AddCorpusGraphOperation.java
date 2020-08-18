package org.corpus_tools.hexatomic.core.undo.operations;

import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SaltProject;

public class AddCorpusGraphOperation implements ReversibleOperation {

  private final SaltProject project;
  private final SCorpusGraph corpusGraph;

  /**
   * Create a undo operation for adding a corpus graph to a Salt project.
   * 
   * @param project The Salt project the corpus graph is added to.
   * @param corpusGraph The corpus graph added to the Salt project.
   */
  public AddCorpusGraphOperation(SaltProject project, SCorpusGraph corpusGraph) {
    super();
    this.project = project;
    this.corpusGraph = corpusGraph;
  }

  @Override
  public void restore() {
    if (project != null && corpusGraph != null) {
      project.removeCorpusGraph(corpusGraph);
    }
  }

  @Override
  public Object getChangedContainer() {
    return project;
  }

  @Override
  public Object getChangedElement() {
    return corpusGraph;
  }

}
