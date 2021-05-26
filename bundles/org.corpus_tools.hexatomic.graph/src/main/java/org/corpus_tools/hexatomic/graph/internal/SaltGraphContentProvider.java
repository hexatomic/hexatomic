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

package org.corpus_tools.hexatomic.graph.internal;

import java.util.List;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutItem;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * Provides the Salt nodes and relations to the Zest API.
 * 
 * @author Thomas Krause
 *
 */
public class SaltGraphContentProvider implements IGraphEntityRelationshipContentProvider {

  @Override
  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof SGraph) {
      SGraph graph = (SGraph) inputElement;
      return graph.getNodes().toArray();
    }
    throw new IllegalArgumentException("Object of type SGraph expected but got "
        + (inputElement == null ? "null" : inputElement.getClass().getName()));
  }


  @Override
  public Object[] getRelationships(Object source, Object dest) {
    if (source instanceof SNode && dest instanceof SNode) {
      SNode sourceNode = (SNode) source;
      SNode targetNode = (SNode) dest;

      if (sourceNode.getGraph() instanceof SDocumentGraph
          && sourceNode.getGraph() == targetNode.getGraph()) {
        // get all outgoing relations between the source and destination node
        SDocumentGraph docGraph = (SDocumentGraph) sourceNode.getGraph();
        List<SRelation<SNode, SNode>> allRelations =
            docGraph.getRelations(sourceNode.getId(), targetNode.getId());
        if (allRelations != null) {
          return allRelations.toArray();
        }
      }

    }
    return new Object[0];
  }


  /**
   * Get the actual data which is connected to the layout item.
   * 
   * @param object The object which.
   * @return Null if no data can be found or has the wrong type.s
   */
  public static IdentifiableElement getData(LayoutItem object) {

    // unwrap nested internal layout node and relationship classes
    while (object instanceof InternalNode) {
      object = ((InternalNode) object).getLayoutEntity();
    }
    while (object instanceof InternalRelationship) {
      object = ((InternalRelationship) object).getLayoutRelationship();
    }
    // unwrap the last layer of the Zest node/relationship class
    Object result = object.getGraphData();
    if (result instanceof GraphNode) {
      result = ((GraphNode) result).getData();
    } else if (result instanceof GraphConnection) {
      result = ((GraphConnection) result).getData();
    }

    // check for delegates
    if (result instanceof IdentifiableElement) {
      return (IdentifiableElement) result;

    } else {
      return null;
    }

  }
}
