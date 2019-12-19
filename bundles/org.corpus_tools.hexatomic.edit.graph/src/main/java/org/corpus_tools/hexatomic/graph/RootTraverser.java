package org.corpus_tools.hexatomic.graph;

import java.util.Arrays;

import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.jface.viewers.ViewerFilter;

public class RootTraverser implements GraphTraverseHandler {

  private SNode root = null;

  private final ViewerFilter filter;

  private RootTraverser(ViewerFilter filter) {
    this.filter = filter;
  }

  @Override
  public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
      SRelation<SNode, SNode> relation, SNode fromNode, long order) {
    if(!(currNode instanceof SToken)) {
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

  public static SNode getRoot(SToken tok, ViewerFilter filter) {
    RootTraverser traverser = new RootTraverser(filter);
    tok.getGraph().traverse(Arrays.asList(tok), GRAPH_TRAVERSE_TYPE.BOTTOM_UP_DEPTH_FIRST,
        "getRootNode", traverser);
    return traverser.root;
  }

}
