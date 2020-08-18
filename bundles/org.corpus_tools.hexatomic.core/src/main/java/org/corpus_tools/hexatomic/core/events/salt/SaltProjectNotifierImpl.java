package org.corpus_tools.hexatomic.core.events.salt;

import static org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory.sendEvent;

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.undo.operations.AddCorpusGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveCorpusGraphOperation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.impl.SaltProjectImpl;

public class SaltProjectNotifierImpl extends SaltProjectImpl {

  private static final long serialVersionUID = -5393546437140062872L;

  @Override
  public void basic_addCorpusGraph(SCorpusGraph corpusGraph) {
    super.basic_addCorpusGraph(corpusGraph);
    sendEvent(Topics.ANNOTATION_OPERATION_ADDED, new AddCorpusGraphOperation(this, corpusGraph));
  }

  @Override
  public void basic_removeCorpusGraph(SCorpusGraph corpusGraph) {
    if (getCorpusGraphs().contains(corpusGraph)) {
      super.basic_removeCorpusGraph(corpusGraph);
      sendEvent(Topics.ANNOTATION_OPERATION_ADDED,
          new RemoveCorpusGraphOperation(this, corpusGraph));
    }
  }

}
