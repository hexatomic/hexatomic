package org.corpus_tools.hexatomic.graph;

import java.util.List;
import java.util.stream.Collectors;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

public class SaltGraphContentProvider implements IGraphEntityContentProvider {

  @Override
  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof SGraph) {
      SGraph graph = (SGraph) inputElement;
      return graph.getNodes().toArray();
    }
    throw new IllegalArgumentException("Object of type SGraph expectected");
  }

  @Override
  public Object[] getConnectedTo(Object entity) {
    if (entity instanceof Node) {
      SNode node = (SNode) entity;
      List<Node> connected = node.getOutRelations().stream().map((rel) -> rel.getTarget())
          .collect(Collectors.toList());
      return connected.toArray();
    }
    throw new IllegalArgumentException("Object of type SNode expectected");
  }

}
