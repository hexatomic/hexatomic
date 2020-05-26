package org.corpus_tools.hexatomic.core.salt_notifications;

import org.corpus_tools.salt.ISaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusDocumentRelation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SCorpusRelation;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SMedialDS;
import org.corpus_tools.salt.common.SMedialRelation;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.STimeline;
import org.corpus_tools.salt.common.STimelineRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SProcessingAnnotation;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.core.impl.SNodeImpl;
import org.corpus_tools.salt.core.impl.SRelationImpl;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.graph.Identifier;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.semantics.SCatAnnotation;
import org.corpus_tools.salt.semantics.SLemmaAnnotation;
import org.corpus_tools.salt.semantics.SPOSAnnotation;
import org.corpus_tools.salt.semantics.SSentenceAnnotation;
import org.corpus_tools.salt.semantics.STypeAnnotation;
import org.corpus_tools.salt.semantics.SWordAnnotation;
import org.eclipse.e4.core.services.events.IEventBroker;

public class SaltNotificationFactory implements ISaltFactory {

  private final IEventBroker events;

  public SaltNotificationFactory(IEventBroker events) {
    this.events = events;
  }


  @Override
  public NodeNotifierImpl createNode() {
    return new NodeNotifierImpl(events);
  }

  @Override
  public Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>> createGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RelationNotifierImpl<Node, Node> createRelation() {
    return new RelationNotifierImpl<Node, Node>(events);
  }

  @Override
  public Label createLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Identifier createIdentifier(IdentifiableElement container, String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Layer<Node, Relation<Node, Node>> createLayer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SGraph createSGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SNode createSNode() {
    NodeNotifierImpl delegate = createNode();
    SNode node = new SNodeImpl(delegate);
    delegate.setWrapper(node);
    return node;
  }

  @Override
  public SRelation<SNode, SNode> createSRelation() {
    RelationNotifierImpl<Node, Node> delegate = createRelation();
    SRelation<SNode, SNode> relation = new SRelationImpl<SNode, SNode>(delegate);
    delegate.setWrapper(relation);
    return relation;
  }

  @Override
  public SAnnotation createSAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SMetaAnnotation createSMetaAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SProcessingAnnotation createSProcessingAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SFeature createSFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SLayer createSLayer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SaltProject createSaltProject() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SCorpus createSCorpus() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SDocument createSDocument() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SCorpusRelation createSCorpusRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SCorpusDocumentRelation createSCorpusDocumentRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SCorpusGraph createSCorpusGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SDocumentGraph createSDocumentGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SSpanningRelation createSSpanningRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SDominanceRelation createSDominanceRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SPointingRelation createSPointingRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SOrderRelation createSOrderRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public STextualRelation createSTextualRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public STimelineRelation createSTimelineRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SMedialRelation createSMedialRelation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SSpan createSSpan() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SStructure createSStructure() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public STextualDS createSTextualDS() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SMedialDS createSMedialDS() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public STimeline createSTimeline() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SToken createSToken() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SCatAnnotation createSCatAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SPOSAnnotation createSPOSAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SLemmaAnnotation createSLemmaAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public STypeAnnotation createSTypeAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SWordAnnotation createSWordAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SSentenceAnnotation createSSentenceAnnotation() {
    // TODO Auto-generated method stub
    return null;
  }

}
