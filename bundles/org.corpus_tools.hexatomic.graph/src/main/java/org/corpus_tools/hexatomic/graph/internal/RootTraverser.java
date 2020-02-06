/*-
 * #%L

 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

package org.corpus_tools.hexatomic.graph.internal;

import java.util.Arrays;

import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Traverses the graph and finds the root node.
 * @author Thomas Krause
 *
 */
public class RootTraverser implements GraphTraverseHandler {

  private SNode root = null;

  private final ViewerFilter filter;

  private RootTraverser(ViewerFilter filter) {
    this.filter = filter;
  }

  @Override
  public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
      SRelation<SNode, SNode> relation, SNode fromNode, long order) {
    if (!(currNode instanceof SToken)) {
      root = currNode;
    }
  }

  @Override
  public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
      SRelation<SNode, SNode> relation, SNode fromNode, long order) {

  }

  @Override
  public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
      @SuppressWarnings("rawtypes") SRelation relation, SNode currNode, long order) {
    return filter.select(null, null, currNode) && (relation == null
        || relation instanceof SDominanceRelation || relation instanceof SSpanningRelation);
  }

  /**
   * Return a root node for the given token.
   * 
   * @param tok The token to get the root node for
   * @param filter A filter which is used to select the subgraph
   * @return The root node or null if not found
   */
  public static SNode getRoot(SToken tok, ViewerFilter filter) {
    RootTraverser traverser = new RootTraverser(filter);
    tok.getGraph().traverse(Arrays.asList(tok), GRAPH_TRAVERSE_TYPE.BOTTOM_UP_DEPTH_FIRST,
        "getRootNode", traverser);
    return traverser.root;
  }

}
