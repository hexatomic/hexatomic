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
import org.corpus_tools.salt.common.impl.SCorpusDocumentRelationImpl;
import org.corpus_tools.salt.common.impl.SCorpusGraphImpl;
import org.corpus_tools.salt.common.impl.SCorpusImpl;
import org.corpus_tools.salt.common.impl.SCorpusRelationImpl;
import org.corpus_tools.salt.common.impl.SDocumentGraphImpl;
import org.corpus_tools.salt.common.impl.SDocumentImpl;
import org.corpus_tools.salt.common.impl.SDominanceRelationImpl;
import org.corpus_tools.salt.common.impl.SMedialDSImpl;
import org.corpus_tools.salt.common.impl.SMedialRelationImpl;
import org.corpus_tools.salt.common.impl.SOrderRelationImpl;
import org.corpus_tools.salt.common.impl.SPointingRelationImpl;
import org.corpus_tools.salt.common.impl.SSpanImpl;
import org.corpus_tools.salt.common.impl.SSpanningRelationImpl;
import org.corpus_tools.salt.common.impl.SStructureImpl;
import org.corpus_tools.salt.common.impl.STextualDSImpl;
import org.corpus_tools.salt.common.impl.STextualRelationImpl;
import org.corpus_tools.salt.common.impl.STimelineImpl;
import org.corpus_tools.salt.common.impl.STimelineRelationImpl;
import org.corpus_tools.salt.common.impl.STokenImpl;
import org.corpus_tools.salt.common.impl.SaltProjectImpl;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SProcessingAnnotation;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.core.impl.SAnnotationImpl;
import org.corpus_tools.salt.core.impl.SFeatureImpl;
import org.corpus_tools.salt.core.impl.SGraphImpl;
import org.corpus_tools.salt.core.impl.SLayerImpl;
import org.corpus_tools.salt.core.impl.SMetaAnnotationImpl;
import org.corpus_tools.salt.core.impl.SNodeImpl;
import org.corpus_tools.salt.core.impl.SProcessingAnnotationImpl;
import org.corpus_tools.salt.core.impl.SRelationImpl;
import org.corpus_tools.salt.graph.Graph;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.graph.Identifier;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.Layer;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.Relation;
import org.corpus_tools.salt.graph.impl.IdentifierImpl;
import org.corpus_tools.salt.semantics.SCatAnnotation;
import org.corpus_tools.salt.semantics.SLemmaAnnotation;
import org.corpus_tools.salt.semantics.SPOSAnnotation;
import org.corpus_tools.salt.semantics.SSentenceAnnotation;
import org.corpus_tools.salt.semantics.STypeAnnotation;
import org.corpus_tools.salt.semantics.SWordAnnotation;
import org.corpus_tools.salt.semantics.impl.SCatAnnotationImpl;
import org.corpus_tools.salt.semantics.impl.SLemmaAnnotationImpl;
import org.corpus_tools.salt.semantics.impl.SPOSAnnotationImpl;
import org.corpus_tools.salt.semantics.impl.SSentenceAnnotationImpl;
import org.corpus_tools.salt.semantics.impl.STypeAnnotationImpl;
import org.corpus_tools.salt.semantics.impl.SWordAnnotationImpl;
import org.eclipse.e4.core.services.events.IEventBroker;

public class SaltNotificationFactory implements ISaltFactory {

  private final IEventBroker events;

  public SaltNotificationFactory(IEventBroker events) {
    this.events = events;
  }


  @Override
  public Node createNode() {
    return new NodeNotifierImpl(events);
  }

  @Override
  public Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>> createGraph() {
    return new GraphNotifierImpl<>(events);
  }

  @Override
  public Relation<Node, Node> createRelation() {
    return new RelationNotifierImpl<>(events);
  }

  @Override
  public Label createLabel() {
    return new LabelNotifierImpl(events);
  }

  @Override
  public Identifier createIdentifier(IdentifiableElement container, String id) {
    return new IdentifierImpl(container, id);
  }

  @Override
  public LayerNotifierImpl<Node, Relation<Node, Node>> createLayer() {
    return new LayerNotifierImpl<>(events);
  }

  @Override
  public SGraph createSGraph() {
    return new SGraphImpl(createGraph());
  }

  @Override
  public SNode createSNode() {
    return new SNodeImpl(createNode());
  }

  @Override
  public SRelation<SNode, SNode> createSRelation() {
    return new SRelationImpl<>(createRelation());
  }

  @Override
  public SAnnotation createSAnnotation() {
    return new SAnnotationImpl(createLabel());
  }

  @Override
  public SMetaAnnotation createSMetaAnnotation() {
    return new SMetaAnnotationImpl(createLabel());
  }

  @Override
  public SProcessingAnnotation createSProcessingAnnotation() {
    return new SProcessingAnnotationImpl(createLabel());
  }

  @Override
  public SFeature createSFeature() {
    return new SFeatureImpl(createLabel());
  }

  @Override
  public SLayer createSLayer() {
    return new SLayerImpl(createLayer());
  }

  @Override
  public SaltProject createSaltProject() {
    return new SaltProjectImpl();
  }

  @Override
  public SCorpus createSCorpus() {
    return new SCorpusImpl(createNode());
  }

  @Override
  public SDocument createSDocument() {
    return new SDocumentImpl(createNode());
  }

  @Override
  public SCorpusRelation createSCorpusRelation() {
    return new SCorpusRelationImpl(createRelation());
  }

  @Override
  public SCorpusDocumentRelation createSCorpusDocumentRelation() {
    return new SCorpusDocumentRelationImpl(createRelation());
  }

  @Override
  public SCorpusGraph createSCorpusGraph() {
    return new SCorpusGraphImpl(createGraph());
  }

  @Override
  public SDocumentGraph createSDocumentGraph() {
    return new SDocumentGraphImpl(createGraph());
  }

  @Override
  public SSpanningRelation createSSpanningRelation() {
    return new SSpanningRelationImpl(createRelation());
  }

  @Override
  public SDominanceRelation createSDominanceRelation() {
    return new SDominanceRelationImpl(createRelation());
  }

  @Override
  public SPointingRelation createSPointingRelation() {
    return new SPointingRelationImpl(createRelation());
  }

  @Override
  public SOrderRelation createSOrderRelation() {
    return new SOrderRelationImpl(createRelation());
  }

  @Override
  public STextualRelation createSTextualRelation() {
    return new STextualRelationImpl(createRelation());
  }

  @Override
  public STimelineRelation createSTimelineRelation() {
    return new STimelineRelationImpl(createRelation());
  }

  @Override
  public SMedialRelation createSMedialRelation() {
    return new SMedialRelationImpl(createRelation());
  }

  @Override
  public SSpan createSSpan() {
    return new SSpanImpl(createNode());
  }

  @Override
  public SStructure createSStructure() {
    return new SStructureImpl(createNode());
  }

  @Override
  public STextualDS createSTextualDS() {
    return new STextualDSImpl(createNode());
  }

  @Override
  public SMedialDS createSMedialDS() {
    return new SMedialDSImpl(createNode());
  }

  @Override
  public STimeline createSTimeline() {
    return new STimelineImpl(createNode());
  }

  @Override
  public SToken createSToken() {
    return new STokenImpl(createNode());
  }

  @Override
  public SCatAnnotation createSCatAnnotation() {
    return new SCatAnnotationImpl(createLabel());
  }

  @Override
  public SPOSAnnotation createSPOSAnnotation() {
    return new SPOSAnnotationImpl(createLabel());
  }

  @Override
  public SLemmaAnnotation createSLemmaAnnotation() {
    return new SLemmaAnnotationImpl(createLabel());
  }

  @Override
  public STypeAnnotation createSTypeAnnotation() {
    return new STypeAnnotationImpl(createLabel());
  }

  @Override
  public SWordAnnotation createSWordAnnotation() {
    return new SWordAnnotationImpl(createLabel());
  }

  @Override
  public SSentenceAnnotation createSSentenceAnnotation() {
    return new SSentenceAnnotationImpl(createLabel());
  }

}
