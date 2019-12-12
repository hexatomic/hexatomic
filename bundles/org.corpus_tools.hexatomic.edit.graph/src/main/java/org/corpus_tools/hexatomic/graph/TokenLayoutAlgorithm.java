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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class TokenLayoutAlgorithm extends AbstractLayoutAlgorithm {

  private double averageTokenNodeWidth;
  private double averageTokenNodeHeight;
  private double maxExistingY;

  public TokenLayoutAlgorithm(int styles) {
    super(styles);
  }



  @Override
  public void setLayoutArea(double x, double y, double width, double height) {
    throw new RuntimeException("Operation not implemented");
  }

  @Override
  protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  protected void applyLayoutInternal(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY,
      double boundsWidth, double boundsHeight) {
    double x = boundsX;

    // Sort tokens
    HashMap<SToken, InternalNode> tokens = new HashMap<SToken, InternalNode>();
    for (InternalNode n : entitiesToLayout) {
      IdentifiableElement element = SaltGraphContentProvider.getData(n);
      if (element instanceof SToken) {
        tokens.put((SToken) element, n);
      }
    }
    int progress = 0;
    if (!tokens.isEmpty()) {
      SDocumentGraph docGraph = tokens.keySet().iterator().next().getGraph();
      List<SToken> sortedTokens =
          docGraph.getSortedTokenByText(new LinkedList<SToken>(tokens.keySet()));
      for (SToken t : sortedTokens) {
        InternalNode n = tokens.get(t);
        if (n != null) {
          n.setInternalLocation(x, boundsY + this.maxExistingY + (this.averageTokenNodeHeight));
          x += this.averageTokenNodeWidth / 10.0;
          x += n.getLayoutEntity().getWidthInLayout();

          fireProgressEvent(progress++, entitiesToLayout.length);
        }
      }
    }
    updateLayoutLocations(tokens.values().toArray(new InternalNode[0]));
    fireProgressEvent(entitiesToLayout.length, entitiesToLayout.length);

  }

  @Override
  protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider, double x, double y, double width,
      double height) {

    this.maxExistingY = 0;
    // Calculate the average width and height to get a good distance between the tokens
    double sumWidth = 0.0;
    double sumHeight = 0.0;
    int tokenCount = 0;
    for (int index = 0; index < entitiesToLayout.length; index++) {
      InternalNode n = entitiesToLayout[index++];
      IdentifiableElement element = SaltGraphContentProvider.getData(n);
      if (element instanceof SToken) {
        sumWidth += n.getLayoutEntity().getWidthInLayout();
        sumHeight += n.getLayoutEntity().getHeightInLayout();
        tokenCount++;
      } else {
        // Find the maximum existing Y: all tokens should be located at the bottom of the graph
        this.maxExistingY = Math.max(this.maxExistingY,
            n.getLayoutEntity().getYInLayout() + n.getLayoutEntity().getHeightInLayout());
      }
    }

    this.averageTokenNodeWidth = sumWidth / (double) tokenCount;
    this.averageTokenNodeHeight = sumHeight / (double) tokenCount;

  }

  @Override
  protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider) {
    // TODO Auto-generated method stub

  }

  @Override
  protected int getTotalNumberOfLayoutSteps() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected int getCurrentLayoutStep() {
    // TODO Auto-generated method stub
    return 0;
  }

}
