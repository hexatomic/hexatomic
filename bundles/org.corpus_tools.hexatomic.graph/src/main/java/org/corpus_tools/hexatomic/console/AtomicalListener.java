/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat,
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

package org.corpus_tools.hexatomic.console;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.ClearContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.DeleteContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.DeleteNodeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.Edge_referenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NamedNodeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewNodeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewNodeLayerContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewNodeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.Node_referenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.QuotedStringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.RawStringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.StringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.TokenizeContext;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;

final class AtomicalListener extends ConsoleCommandBaseListener {

  private final AtomicalConsole graphAnnoConsole;
  private final Set<SStructuredNode> referencedNodes = new LinkedHashSet<>();
  private final Set<SRelation<?, ?>> referencedEdges = new LinkedHashSet<>();
  private final Set<SAnnotation> attributes = new LinkedHashSet<>();
  private Optional<String> layer = Optional.empty();

  AtomicalListener(AtomicalConsole graphAnnoConsole) {
    this.graphAnnoConsole = graphAnnoConsole;
  }


  private String getString(StringContext ctx) {
    if (ctx instanceof RawStringContext) {
      RawStringContext rawStringContext = (RawStringContext) ctx;
      return rawStringContext.getText();

    } else if (ctx instanceof QuotedStringContext) {
      QuotedStringContext escapedStringContext = (QuotedStringContext) ctx;
      String rawText = escapedStringContext.getText();
      return rawText.substring(1, rawText.length() - 1).replace("\\\"", "");
    }
    return null;
  }

  private Set<SStructuredNode> getReferencedNodes(Node_referenceContext ctx) {

    Set<SStructuredNode> result = new LinkedHashSet<>();
    if (ctx instanceof NamedNodeReferenceContext) {
      NamedNodeReferenceContext generalCtx = (NamedNodeReferenceContext) ctx;
      String nodeName = generalCtx.name.getText().substring(1);
      List<SNode> matchedNodes =
          this.graphAnnoConsole.graph.getNodesByName(nodeName);
      if (matchedNodes != null) {
        for (SNode n : matchedNodes) {
          if (n instanceof SStructuredNode) {
            result.add((SStructuredNode) n);
          }
        }
      }

    }
    return result;
  }

  @Override
  public void enterClear(ClearContext ctx) {

    for (SRelation<?, ?> r : new LinkedList<>(graphAnnoConsole.graph.getRelations())) {
      graphAnnoConsole.graph.removeRelation(r);
    }
    for (SNode n : new LinkedList<>(graphAnnoConsole.graph.getNodes())) {
      graphAnnoConsole.graph.removeNode(n);
    }
    for (SLayer layer : new LinkedList<>(graphAnnoConsole.graph.getLayers())) {
      graphAnnoConsole.graph.removeLayer(layer);
    }
  }

  @Override
  public void enterNewNodeReference(NewNodeReferenceContext ctx) {
    for (SStructuredNode ref : getReferencedNodes(ctx.node_reference())) {
      referencedNodes.add(ref);
    }
  }

  @Override
  public void enterDeleteNodeReference(DeleteNodeReferenceContext ctx) {
    for (SStructuredNode ref : getReferencedNodes(ctx.node_reference())) {
      referencedNodes.add(ref);
    }
  }


  @Override
  public void enterEdge_reference(Edge_referenceContext ctx) {
    Set<SStructuredNode> sources = getReferencedNodes(ctx.source);
    Set<SStructuredNode> targets = getReferencedNodes(ctx.target);
    for (SStructuredNode s : sources) {
      for (SStructuredNode t : targets) {
        for (SRelation<SNode, SNode> rel : this.graphAnnoConsole.graph.getRelations(s.getId(),
            t.getId())) {
          referencedEdges.add(rel);
        }
      }
    }
  }

  @Override
  public void enterAttribute(
      org.corpus_tools.hexatomic.console.ConsoleCommandParser.AttributeContext ctx) {

    SAnnotation anno = SaltFactory.createSAnnotation();
    anno.setName(getString(ctx.name));
    anno.setValue(getString(ctx.value));
    if (ctx.namespace != null) {
      anno.setNamespace(ctx.namespace.getText());
    }
    attributes.add(anno);
  }

  @Override
  public void enterNewNodeLayer(NewNodeLayerContext ctx) {
    layer = Optional.of(ctx.getText());
  }



  @Override
  public void exitNewNode(NewNodeContext ctx) {

    // Create the node itself
    SStructure newNode = this.graphAnnoConsole.graph
        .createStructure(referencedNodes.toArray(new SStructuredNode[referencedNodes.size()]));

    if (newNode == null) {
      this.graphAnnoConsole.writeLine("Error: could not create the new node.");
    } else {
      // Add all annotations
      for (SAnnotation anno : attributes) {
        newNode.addAnnotation(anno);
      }

      // Add or create a layer if given as argument
      if (layer.isPresent()) {
        List<SLayer> matchingLayers = this.graphAnnoConsole.graph.getLayerByName(layer.get());
        if (matchingLayers == null || matchingLayers.isEmpty()) {
          matchingLayers = new LinkedList<SLayer>();
          matchingLayers.add(SaltFactory.createSLayer());
          matchingLayers.get(0).setName(layer.get());
          this.graphAnnoConsole.graph.addLayer(matchingLayers.get(0));
        }

        for (SLayer l : matchingLayers) {
          l.addNode(newNode);
        }
      }

      this.graphAnnoConsole.writeLine("Created new structure node #" + newNode.getName() + ".");
      for (SAnnotation anno : newNode.getAnnotations()) {
        this.graphAnnoConsole.writeLine(anno.toString());
      }
    }
  }

  @Override
  public void exitDelete(DeleteContext ctx) {
    for (SStructuredNode n : referencedNodes) {
      this.graphAnnoConsole.graph.removeNode(n);
    }
    for (SRelation<?, ?> rel : referencedEdges) {
      this.graphAnnoConsole.graph.removeRelation(rel);
    }
  }

  @Override
  public void exitTokenize(TokenizeContext ctx) {
    SDocumentGraph graph = this.graphAnnoConsole.graph;

    STextualDS ds;
    StringBuilder sb;
    if (graph.getTextualDSs() == null || graph.getTextualDSs().isEmpty()) {
      // Create a new textual data source
      ds = SaltFactory.createSTextualDS();
      graph.addNode(ds);
      sb = new StringBuilder();

    } else {
      // append to the first existing data source
      ds = graph.getTextualDSs().iterator().next();
      sb = new StringBuilder(ds.getText());
      if (sb.length() > 0) {
        sb.append(' ');
      }
    }
    ListIterator<StringContext> itWords = ctx.string().listIterator();
    while (itWords.hasNext()) {
      String tokenValue = getString(itWords.next());
      int start = sb.length();
      sb.append(tokenValue);
      graph.createToken(ds, start, sb.length());

      if (itWords.hasNext()) {
        sb.append(' ');
      }
    }

    ds.setText(sb.toString());

  }
}
