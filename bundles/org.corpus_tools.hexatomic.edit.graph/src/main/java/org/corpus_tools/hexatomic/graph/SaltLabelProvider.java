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

import com.google.common.base.Joiner;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

public class SaltLabelProvider extends LabelProvider implements ISelfStyleProvider {
  @Override
  public String getText(Object element) {

    if (element instanceof LabelableElement) {
      LabelableElement node = (LabelableElement) element;
      TreeMap<String, String> labelsByQName = new TreeMap<>();
      for (Label l : node.getLabels()) {
        boolean include = true;
        if (element instanceof SRelation<?, ?> && "salt".equals(l.getNamespace())) {
          include = false;
        }
        if ("salt".equals(l.getNamespace()) && "id".equals(l.getName())) {
          include = false;
        }
        if (include) {
          String qname = SaltUtil.createQName(l.getNamespace(), l.getName());
          labelsByQName.put(qname, qname + "=" + l.getValue());
        }
      }
      List<String> labels = new LinkedList<>(labelsByQName.values());

      if (element instanceof SToken) {
        SToken token = (SToken) element;
        String coveredText = token.getGraph().getText(token);
        labels.add(0, coveredText);
      }

      return Joiner.on('\n').join(labels);
    }
    if (element instanceof EntityConnectionData) {
      return "";
    }
    throw new IllegalArgumentException("Object of type LabelableElement expectected, but got "
        + element.getClass().getSimpleName());
  }

  @Override
  public void selfStyleConnection(Object element, GraphConnection connection) {
    if (element instanceof SPointingRelation) {
      connection.changeLineColor(ColorConstants.blue);
    } else if (element instanceof SDominanceRelation) {
      connection.changeLineColor(ColorConstants.red);
    }
  }

  @Override
  public void selfStyleNode(Object element, GraphNode node) {
    if (element instanceof SToken) {
      node.setBackgroundColor(ColorConstants.lightGreen);
    }

  }

}
