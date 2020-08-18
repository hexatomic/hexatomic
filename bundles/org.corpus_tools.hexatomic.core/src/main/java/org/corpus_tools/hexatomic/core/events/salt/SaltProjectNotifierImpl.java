/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
