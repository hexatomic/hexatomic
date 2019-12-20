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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Range;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class SaltGraphLayout extends AbstractLayoutAlgorithm {

  private double averageTokenNodeWidth;
  private double averageTokenNodeHeight;
  private double maxNodeHeight;

  private BiMap<InternalNode, SNode> nodes;
  private BiMap<InternalRelationship, SRelation<?, ?>> relations;


  public SaltGraphLayout(int styles) {
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

    List<SToken> tokens = new LinkedList<>();

    // Assign an initial rank to each non-token
    Map<InternalNode, Integer> ranks = new HashMap<>();

    // Get all nodes that are root nodes when only including the considered relations
    int maxRank = 0;
    for (SNode n : this.nodes.values()) {
      if (n instanceof SToken) {
        tokens.add((SToken) n);
      } else {
        boolean isRoot = true;
        for (SRelation<?, ?> rel : n.getInRelations()) {
          if (this.relations.containsValue(rel)) {
            isRoot = false;
            continue;
          }
        }
        if (isRoot) {
          maxRank = assignRankRecursivly(this.nodes.inverse().get(n), ranks, maxRank);
        }
      }

    }

    layoutTokenOrder(tokens, boundsX, boundsY, maxRank);

    // TODO: Check if we can merge nodes to the same rank if they don't cover the same token


    // TODO: assign position based on rank and the covered tokens
    for (Map.Entry<InternalNode, Integer> e : ranks.entrySet()) {
      int rank = e.getValue();
      InternalNode node = e.getKey();
      SNode saltNode = this.nodes.get(node);
      // Get covered tokens to get the x position
      if (saltNode.getGraph() instanceof SDocumentGraph) {
        SDocumentGraph graph = (SDocumentGraph) saltNode.getGraph();
        List<SToken> overlappedTokens = graph.getOverlappedTokens(saltNode);
        Range<Double> range = getTokenRange(overlappedTokens);

        double x = 0.0;
        if (range.hasLowerBound()) {
          if (range.hasUpperBound()) {
            // position at the center
            x = range.lowerEndpoint() + (range.upperEndpoint() - range.lowerEndpoint()) / 2.0;
          } else {
            // position left-aligned
            x = range.lowerEndpoint();
          }
        }
        node.setInternalLocation(x, boundsY + (rank * this.maxNodeHeight));

      }


    }

    updateLayoutLocations(entitiesToLayout);
    fireProgressEvent(entitiesToLayout.length, entitiesToLayout.length);

  }

  private Range<Double> getTokenRange(List<SToken> tokens) {

    Optional<Double> minX = Optional.empty();
    Optional<Double> maxX = Optional.empty();

    for (SToken t : tokens) {
      InternalNode internalTokenNode = this.nodes.inverse().get(t);
      if (internalTokenNode != null) {
        if (minX.isPresent()) {
          minX = Optional.of(Math.min(internalTokenNode.getInternalX(), minX.get()));
        } else {
          minX = Optional.of(internalTokenNode.getInternalX());
        }

        if (maxX.isPresent()) {
          maxX = Optional.of(Math.max(
              internalTokenNode.getInternalX() + internalTokenNode.getInternalWidth(), maxX.get()));
        } else {
          maxX =
              Optional.of(internalTokenNode.getInternalX() + internalTokenNode.getInternalWidth());
        }
      }
    }


    if (!minX.isPresent()) {
      minX = Optional.of(0.0);
    }
    if (maxX.isPresent()) {
      return Range.closed(minX.get(), maxX.get());
    } else {
      return Range.atLeast(minX.get());
    }
  }

  private int assignRankRecursivly(InternalNode node, Map<InternalNode, Integer> ranks,
      int maxRank) {
    if (node == null || ranks.containsKey(node)) {
      return maxRank;
    }

    SNode saltNode = this.nodes.get(node);
    if (saltNode == null || saltNode instanceof SToken) {
      return maxRank;
    }

    int newRank = maxRank + 1;
    ranks.putIfAbsent(node, newRank);

    for (SRelation<?, ?> rel : saltNode.getOutRelations()) {
      if (this.relations.values().contains(rel)) {
        InternalNode outNode = this.nodes.inverse().get(rel.getTarget());
        if (outNode != null) {
          newRank = assignRankRecursivly(outNode, ranks, newRank);
        }
      }
    }


    return newRank;

  }


  private void layoutTokenOrder(Collection<SToken> tokens, double boundsX, double boundsY,
      int tokenRank) {
    double x = boundsX;

    // Sort tokens
    int progress = 0;
    if (!tokens.isEmpty()) {
      SDocumentGraph docGraph = tokens.iterator().next().getGraph();
      List<SToken> sortedTokens = docGraph.getSortedTokenByText(new LinkedList<SToken>(tokens));
      for (SToken t : sortedTokens) {
        InternalNode n = this.nodes.inverse().get(t);
        if (n != null) {
          n.setInternalLocation(x,
              boundsY + (tokenRank * this.maxNodeHeight) + (this.averageTokenNodeHeight));
          x += this.averageTokenNodeWidth / 10.0;
          x += n.getLayoutEntity().getWidthInLayout();

          fireProgressEvent(progress++, tokens.size());
        }
      }
    }
  }

  @Override
  protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider, double x, double y, double width,
      double height) {

    // map all internal nodes to their Salt entity
    this.nodes = HashBiMap.create();
    for (InternalNode n : entitiesToLayout) {
      IdentifiableElement saltElement = SaltGraphContentProvider.getData(n);
      if (saltElement instanceof SNode) {
        this.nodes.put(n, (SNode) saltElement);
      }
    }
    this.relations = HashBiMap.create();
    for (InternalRelationship rel : relationshipsToConsider) {
      IdentifiableElement saltElement = SaltGraphContentProvider.getData(rel);
      if (saltElement instanceof SRelation<?, ?>) {
        this.relations.put(rel, (SRelation<?, ?>) saltElement);
      }
    }

    this.maxNodeHeight = 0;
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
        this.maxNodeHeight = Math.max(this.maxNodeHeight, n.getLayoutEntity().getHeightInLayout());
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
