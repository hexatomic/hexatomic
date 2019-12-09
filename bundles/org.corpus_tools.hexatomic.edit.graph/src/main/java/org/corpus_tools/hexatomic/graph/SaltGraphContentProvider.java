/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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
package org.corpus_tools.hexatomic.graph;

import java.util.List;
import java.util.stream.Collectors;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SNamedElement;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutItem;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

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

  public static SNamedElement getData(LayoutItem object) {
  
    while(object instanceof InternalNode) {
      object = ((InternalNode) object).getLayoutEntity();
    }
    Object result = object.getGraphData();
    if (result instanceof GraphNode) {
      result = ((GraphNode) result).getData();
    }
    
    if(result instanceof SNamedElement) {
      return (SNamedElement) result;
    } else {
      return null;
    }
  }

}
