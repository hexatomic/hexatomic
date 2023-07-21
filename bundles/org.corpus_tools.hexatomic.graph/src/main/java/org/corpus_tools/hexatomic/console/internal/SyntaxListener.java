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

package org.corpus_tools.hexatomic.console.internal;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.Token;
import org.corpus_tools.hexatomic.console.ConsoleCommandBaseListener;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.AnnotateContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.ClearContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.DeleteContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.EmptyAttributeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.ExistingDominanceEdgeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.ExistingPointingEdgeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.LayerReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NamedNodeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewDominanceEdgeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewEdgeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewNodeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewPointingEdgeReferenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NewSpanContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.Node_referenceContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.NonEmptyAttributeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.PunctuationContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.QuotedStringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.RawStringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.StringContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.TokenChangeTextContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.TokenizeAfterContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.TokenizeBeforeContext;
import org.corpus_tools.hexatomic.console.ConsoleCommandParser.TokenizeContext;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.util.DataSourceSequence;

/**
 * An ANTLR listener to modify an annotation graph.
 * 
 * @author Thomas Krause
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class SyntaxListener extends ConsoleCommandBaseListener {

  public static final String REFERENCED_NODE_NO_TOKEN = "Referenced node is not a token.";
  private final SDocumentGraph graph;
  private final STextualDS selectedText;
  private final Set<SStructuredNode> referencedNodes = new LinkedHashSet<>();
  private final Set<SRelation<?, ?>> referencedEdges = new LinkedHashSet<>();
  private final Set<SAnnotation> attributes = new LinkedHashSet<>();
  private Optional<String> layer = Optional.empty();
  private final List<String> outputLines = new LinkedList<>();

  /**
   * Creates a new ANTLR listener.
   * 
   * @param graph The document graph to manipulate.
   * @param selectedText The currently selected textual data source.
   */
  public SyntaxListener(SDocumentGraph graph, STextualDS selectedText) {
    this.graph = graph;
    this.selectedText = selectedText;
  }

  public List<String> getOutputLines() {
    return outputLines;
  }


  private String getString(StringContext ctx) {
    if (ctx instanceof RawStringContext) {
      RawStringContext rawStringContext = (RawStringContext) ctx;
      return rawStringContext.getText();
    } else if (ctx instanceof PunctuationContext) {
      PunctuationContext rawStringContext = (PunctuationContext) ctx;
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
      List<SNode> matchedNodes = this.graph.getNodesByName(nodeName);
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

  private Set<SStructuredNode> getReferencedNodes(Token t) {

    Set<SStructuredNode> result = new LinkedHashSet<>();
    String nodeName = t.getText().substring(1);
    List<SNode> matchedNodes = this.graph.getNodesByName(nodeName);
    if (matchedNodes != null) {
      for (SNode n : matchedNodes) {
        if (n instanceof SStructuredNode) {
          result.add((SStructuredNode) n);
        }
      }
    }


    return result;
  }

  @Override
  public void enterClear(ClearContext ctx) {

    for (SRelation<?, ?> r : new LinkedList<>(graph.getRelations())) {
      graph.removeRelation(r);
    }
    for (SNode n : new LinkedList<>(graph.getNodes())) {
      graph.removeNode(n);
    }
    for (SLayer currLayer : new LinkedList<>(graph.getLayers())) {
      graph.removeLayer(currLayer);
    }
  }

  @Override
  public void enterNamedNodeReference(NamedNodeReferenceContext ctx) {
    for (SStructuredNode ref : getReferencedNodes(ctx)) {
      referencedNodes.add(ref);
    }
  }

  @Override
  public void enterExistingPointingEdgeReference(ExistingPointingEdgeReferenceContext ctx) {
    Set<SStructuredNode> sources = getReferencedNodes(ctx.source);
    Set<SStructuredNode> targets = getReferencedNodes(ctx.target);

    for (SStructuredNode s : sources) {
      for (SStructuredNode t : targets) {
        List<SPointingRelation> existing = new LinkedList<>();
        for (SRelation<?, ?> rel : this.graph.getRelations(s.getId(), t.getId())) {
          if (rel instanceof SPointingRelation) {
            existing.add((SPointingRelation) rel);
          }
        }
        this.referencedEdges.addAll(existing);
      }
    }
  }

  @Override
  public void enterExistingDominanceEdgeReference(ExistingDominanceEdgeReferenceContext ctx) {
    Set<SStructuredNode> sources = getReferencedNodes(ctx.source);
    Set<SStructuredNode> targets = getReferencedNodes(ctx.target);

    for (SStructuredNode sourceNodeRaw : sources) {
      if (sourceNodeRaw instanceof SStructure) {
        SStructure s = (SStructure) sourceNodeRaw;
        for (SStructuredNode t : targets) {
          List<SDominanceRelation> existing = new LinkedList<>();
          for (SRelation<?, ?> rel : this.graph.getRelations(s.getId(), t.getId())) {
            if (rel instanceof SDominanceRelation) {
              existing.add((SDominanceRelation) rel);
            }
          }
          this.referencedEdges.addAll(existing);
        }
      }
    }
  }

  @Override
  public void enterNewPointingEdgeReference(NewPointingEdgeReferenceContext ctx) {
    Set<SStructuredNode> sources = getReferencedNodes(ctx.source);
    Set<SStructuredNode> targets = getReferencedNodes(ctx.target);

    for (SStructuredNode s : sources) {
      for (SStructuredNode t : targets) {
        SPointingRelation rel = SaltFactory.createSPointingRelation();
        rel.setSource(s);
        rel.setTarget(t);
        this.referencedEdges.add(rel);
      }
    }
  }

  @Override
  public void enterNewDominanceEdgeReference(NewDominanceEdgeReferenceContext ctx) {
    Set<SStructuredNode> sources = getReferencedNodes(ctx.source);
    Set<SStructuredNode> targets = getReferencedNodes(ctx.target);

    for (SStructuredNode sourceNodeRaw : sources) {
      if (sourceNodeRaw instanceof SStructure) {
        SStructure s = (SStructure) sourceNodeRaw;
        for (SStructuredNode t : targets) {
          SDominanceRelation rel = SaltFactory.createSDominanceRelation();
          rel.setSource(s);
          rel.setTarget(t);
          this.referencedEdges.add(rel);
        }
      }
    }
  }

  @Override
  public void enterNonEmptyAttribute(NonEmptyAttributeContext ctx) {
    SAnnotation anno = SaltFactory.createSAnnotation();
    anno.setName(getString(ctx.name));
    anno.setValue(getString(ctx.value));
    if (ctx.namespace != null) {
      anno.setNamespace(ctx.namespace.getText());
    }
    attributes.add(anno);
  }

  @Override
  public void enterEmptyAttribute(EmptyAttributeContext ctx) {
    SAnnotation anno = SaltFactory.createSAnnotation();
    anno.setName(getString(ctx.name));
    anno.setValue(null);
    if (ctx.namespace != null) {
      anno.setNamespace(ctx.namespace.getText());
    }
    attributes.add(anno);
  }

  @Override
  public void enterLayerReference(LayerReferenceContext ctx) {
    layer = Optional.of(ctx.getText());
  }

  private String getUnusedName(String prefix, int start) {
    int idx = start;
    List<SNode> existing = this.graph.getNodesByName(prefix + idx);
    while (existing != null && !existing.isEmpty()) {
      idx++;
      existing = this.graph.getNodesByName(prefix + idx);
    }

    return prefix + idx;
  }


  @Override
  public void exitNewNode(NewNodeContext ctx) {

    // Create the node itself
    SStructure newNode = this.graph
        .createStructure(referencedNodes.toArray(new SStructuredNode[referencedNodes.size()]));

    if (newNode == null) {
      this.outputLines.add("Error: could not create the new node.");
    } else {
      newNode.setName(getUnusedName("n", this.graph.getStructures().size()));

      // Add all annotations
      for (SAnnotation anno : attributes) {
        newNode.addAnnotation(anno);
      }

      // Add or create a layer if given as argument
      if (layer.isPresent()) {
        List<SLayer> matchingLayers = this.graph.getLayerByName(layer.get());
        if (matchingLayers == null || matchingLayers.isEmpty()) {
          matchingLayers = new LinkedList<>();
          matchingLayers.add(SaltFactory.createSLayer());
          matchingLayers.get(0).setName(layer.get());
          this.graph.addLayer(matchingLayers.get(0));
        }

        for (SLayer l : matchingLayers) {
          l.addNode(newNode);
        }
      }

      this.outputLines.add("Created new structure node #" + newNode.getName() + ".");
      for (SAnnotation anno : newNode.getAnnotations()) {
        this.outputLines.add(anno.toString());
      }
    }
  }

  private boolean checkReferencesOnlyTokens() {

    for (SStructuredNode node : referencedNodes) {
      if (!(node instanceof SToken)) {
        this.outputLines
            .add("Error: could not create the new span - " + node.getName() + " is not a token.");
        return false;
      }
    }
    return true;
  }

  @Override
  public void exitNewSpan(NewSpanContext ctx) {

    if (checkReferencesOnlyTokens()) {

      // Create the span
      SSpan newSpan = this.graph.createSpan(
          referencedNodes.parallelStream().map(SToken.class::cast).collect(Collectors.toList()));
      if (newSpan == null) {
        this.outputLines.add("Error: could not create the new span.");
      } else {
        newSpan.setName(getUnusedName("s", this.graph.getSpans().size()));

        // Add all annotations
        for (SAnnotation anno : attributes) {
          newSpan.addAnnotation(anno);
        }

        // Add or create a layer if given as argument
        if (layer.isPresent()) {
          List<SLayer> matchingLayers = this.graph.getLayerByName(layer.get());
          if (matchingLayers == null || matchingLayers.isEmpty()) {
            matchingLayers = new LinkedList<>();
            matchingLayers.add(SaltFactory.createSLayer());
            matchingLayers.get(0).setName(layer.get());
            this.graph.addLayer(matchingLayers.get(0));
          }

          for (SLayer l : matchingLayers) {
            l.addNode(newSpan);
          }
        }

        this.outputLines.add("Created new span node #" + newSpan.getName() + ".");
        for (SAnnotation anno : newSpan.getAnnotations()) {
          this.outputLines.add(anno.toString());
        }
      }
    }
  }

  @Override
  public void exitDelete(DeleteContext ctx) {
    for (SStructuredNode n : referencedNodes) {
      this.graph.removeNode(n);
    }
    for (SRelation<?, ?> rel : referencedEdges) {
      this.graph.removeRelation(rel);
    }
  }

  @Override
  public void exitTokenize(TokenizeContext ctx) {
    SDocumentGraph currGraph = this.graph;

    STextualDS ds;
    StringBuilder sb;
    if (currGraph.getTextualDSs() == null || currGraph.getTextualDSs().isEmpty()) {
      // Create a new textual data source
      ds = SaltFactory.createSTextualDS();
      currGraph.addNode(ds);
      sb = new StringBuilder();

    } else {
      // append to the first existing data source
      ds = this.selectedText;
      String originalText = ds.getText();
      sb = new StringBuilder(originalText);
      if (originalText.length() > 0 && originalText.charAt(originalText.length() - 1) != ' ') {
        // Add a space to separate the new tokens from the original text
        sb.append(' ');
      }
    }
    int numberOfTokens = currGraph.getTokens().size();
    ListIterator<StringContext> itWords = ctx.string().listIterator();
    while (itWords.hasNext()) {
      String tokenValue = getString(itWords.next());
      int start = sb.length();
      sb.append(tokenValue);
      SToken t = currGraph.createToken(ds, start, sb.length());
      t.setName(getUnusedName("t", ++numberOfTokens));

      if (itWords.hasNext()) {
        sb.append(' ');
      }
    }

    ds.setText(sb.toString());

  }

  private List<String> newTokensTextFromContext(ListIterator<StringContext> itWords) {
    List<String> newTokenTexts = new LinkedList<>();
    while (itWords.hasNext()) {
      String tokenValue = getString(itWords.next());
      newTokenTexts.add(tokenValue);
    }
    return newTokenTexts;
  }

  private void addTokensToText(STextualDS ds, int offset, List<String> newTokenTexts) {
    int numberOfTokens = graph.getTokens().size();
    List<SToken> newTokens = graph.insertTokensAt(ds, offset, newTokenTexts, true);
    for (SToken t : newTokens) {
      t.setName(getUnusedName("t", ++numberOfTokens));
    }
  }

  @Override
  public void exitTokenizeAfter(TokenizeAfterContext ctx) {
    SDocumentGraph currGraph = this.graph;
    SStructuredNode n = referencedNodes.iterator().next();
    if (n instanceof SToken) {
      SToken referencedToken = (SToken) n;

      @SuppressWarnings("rawtypes")
      List<DataSourceSequence> allSequences = currGraph
          .getOverlappedDataSourceSequence(referencedToken, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
      if (allSequences != null && !allSequences.isEmpty()) {
        DataSourceSequence<?> seq = allSequences.get(0);
        int offset = seq.getEnd().intValue();
        STextualDS textDS = (STextualDS) seq.getDataSource();
        if (offset < textDS.getText().length() - 1 && textDS.getText().charAt(offset) == ' ') {
          // Insert after the space character and not directly after the token
          offset += 1;
        }
        if (seq.getDataSource() instanceof STextualDS) {
          STextualDS ds = (STextualDS) seq.getDataSource();
          List<String> newTokenTexts = newTokensTextFromContext(ctx.string().listIterator());
          addTokensToText(ds, offset, newTokenTexts);
        }
      }
    } else {
      this.outputLines.add(REFERENCED_NODE_NO_TOKEN);
    }
  }

  @Override
  public void exitTokenizeBefore(TokenizeBeforeContext ctx) {
    SDocumentGraph currGraph = this.graph;
    SStructuredNode n = referencedNodes.iterator().next();
    if (n instanceof SToken) {
      SToken referencedToken = (SToken) n;

      @SuppressWarnings("rawtypes")
      List<DataSourceSequence> allSequences = currGraph
          .getOverlappedDataSourceSequence(referencedToken, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
      if (allSequences != null && !allSequences.isEmpty()) {
        DataSourceSequence<?> seq = allSequences.get(0);
        int offset = seq.getStart().intValue();
        if (seq.getDataSource() instanceof STextualDS) {
          STextualDS ds = (STextualDS) seq.getDataSource();
          List<String> newTokenTexts = newTokensTextFromContext(ctx.string().listIterator());
          addTokensToText(ds, offset, newTokenTexts);
        }
      }
    } else {
      this.outputLines.add(REFERENCED_NODE_NO_TOKEN);
    }
  }

  @Override
  public void exitTokenChangeText(TokenChangeTextContext ctx) {
    final SStructuredNode n = referencedNodes.iterator().next();
    final String newTokenText = getString(ctx.string());
    if (n instanceof SToken && newTokenText != null) {
      SaltHelper.changeTokenText((SToken) n, newTokenText);
    } else {
      this.outputLines.add(REFERENCED_NODE_NO_TOKEN);
    }
  }

  private void updateAnnotationForElement(SAnnotationContainer element, SAnnotation anno) {
    SAnnotation existingAnnotation = element.getAnnotation(anno.getNamespace(), anno.getName());
    if (existingAnnotation == null) {
      if (anno.getValue() != null) {
        // Create a new one
        element.createAnnotation(anno.getNamespace(), anno.getName(), anno.getValue());
      }
    } else if (anno.getValue() == null) {
      // Remove the existing annotation
      element.removeLabel(anno.getNamespace(), anno.getName());
    } else if (anno.getValue() != null) {
      // Update the value of the annotation: this causes fever update events than deleting and (re-)
      // adding it
      existingAnnotation.setValue(anno.getValue());
    }
  }

  @Override
  public void exitAnnotate(AnnotateContext ctx) {
    for (SAnnotation anno : this.attributes) {

      for (SStructuredNode n : this.referencedNodes) {
        updateAnnotationForElement(n, anno);
      }
      for (SRelation<?, ?> rel : this.referencedEdges) {
        updateAnnotationForElement(rel, anno);
      }
    }
  }

  @Override
  public void exitNewEdge(NewEdgeContext ctx) {
    for (SRelation<?, ?> rel : this.referencedEdges) {
      graph.addRelation(rel);
      for (SAnnotation anno : this.attributes) {
        rel.createAnnotation(anno.getNamespace(), anno.getName(), anno.getValue());
      }

      if (layer.isPresent()) {
        List<SLayer> matchingLayers = this.graph.getLayerByName(layer.get());
        if (matchingLayers == null || matchingLayers.isEmpty()) {
          matchingLayers = new LinkedList<>();
          matchingLayers.add(SaltFactory.createSLayer());
          matchingLayers.get(0).setName(layer.get());
          this.graph.addLayer(matchingLayers.get(0));
        }

        for (SLayer l : matchingLayers) {
          l.addRelation(rel);
        }
      }
    }
  }

}
