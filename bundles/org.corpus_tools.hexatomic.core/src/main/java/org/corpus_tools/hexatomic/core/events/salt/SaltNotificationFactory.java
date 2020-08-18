/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
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

package org.corpus_tools.hexatomic.core.events.salt;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.ISaltFactory;
import org.corpus_tools.salt.SaltFactory;
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
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;

/**
 * Implements at {@link SaltFactory} where the created objects will use the {@link IEventBroker} to
 * send events when the objects are updated.
 * 
 * <p>
 * The event will have the ID of the updated element as argument.
 * </p>
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
public class SaltNotificationFactory implements ISaltFactory {


  @Inject
  private UISynchronize sync;

  @Inject
  private IEventBroker events;

  private boolean suppressingEvents;


  /**
   * If true, events for project updates should not be active. This property will be set if bulk
   * changes like loading are made to a document.
   * 
   * @return True if project change events should be suppressed.
   */
  public boolean isSuppressingEvents() {
    return suppressingEvents;
  }

  /**
   * If set to true, events for project updates should not be active. This property should be set if
   * bulk changes like loading are made to a document.
   * 
   * @param suppressingEvents Set to true if project change events should be suppressed.
   */
  public void setSuppressingEvents(boolean suppressingEvents) {
    this.suppressingEvents = suppressingEvents;
  }

  /**
   * Sets the Eclipse event broker.
   * 
   * @param events The event broker.
   */
  public void setEvents(IEventBroker events) {
    this.events = events;
  }

  /**
   * Sets the Eclipse UI synchronization instance.
   * 
   * @param sync The UI synchronization instance.
   */
  public void setSync(UISynchronize sync) {
    this.sync = sync;
  }

  /**
   * Sends the topic over the Eclipse event bus. This only works if the {@link SaltFactory}
   * implementation is set to be an instance of this class. It will throw a runtime exception
   * otherwise.
   * 
   * @param topic The topic to send.
   * @param element The element which is used as argument to the topic.
   * 
   * @throws IllegalStateException Thrown if the current {@link SaltFactory} implementation is not
   *         of this type.
   */
  static void sendEvent(String topic, Object element) {

    // Get an instance to the factory singleton and check if this has the correct class
    ISaltFactory factory = SaltFactory.getFactory();
    if (factory instanceof SaltNotificationFactory) {
      SaltNotificationFactory notificationFactory = (SaltNotificationFactory) factory;
      // We have to assume that other threads can modify the Salt graph.
      // Since we are sending the events synchronously and receivers are likely using the UI thread
      // for receiving the event, synchronizing here and not in the event bus makes sure the event
      // handler is always executed before this function returns.
      notificationFactory.sync.syncExec(() -> {
        if (!notificationFactory.isSuppressingEvents()) {
          Object resolvedElement = SaltHelper.resolveDelegation(element);
          notificationFactory.events.send(topic, resolvedElement);
        }
      });
    } else {
      throw new IllegalStateException(
          "Current Salt factory is not of type SaltNotificationFactory but "
              + factory.getClass().getName());
    }
  }


  private LabelNotifierImpl createNotifierLabel() {
    return new LabelNotifierImpl();
  }


  private NodeNotifierImpl createNotifierNode() {
    return new NodeNotifierImpl();
  }


  private RelationNotifierImpl<Node, Node> createNotifierRelation() {
    return new RelationNotifierImpl<>();
  }


  @Override
  public Node createNode() {
    return createNotifierNode();
  }

  @Override
  public Graph<Node, Relation<Node, Node>, Layer<Node, Relation<Node, Node>>> createGraph() {
    return new GraphNotifierImpl<>();
  }


  @Override
  public Relation<Node, Node> createRelation() {
    return createNotifierRelation();
  }

  @Override
  public Label createLabel() {
    return createNotifierLabel();
  }

  @Override
  public Identifier createIdentifier(IdentifiableElement container, String id) {
    return new IdentifierImpl(container, id);
  }

  @Override
  public Layer<Node, Relation<Node, Node>> createLayer() {
    return new LayerNotifierImpl<>();
  }

  @Override
  public SGraph createSGraph() {
    GraphNotifierImpl<SNode, SRelation<SNode, SNode>> delegate = new GraphNotifierImpl<>();
    SGraphImpl result = new SGraphImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SNode createSNode() {
    NodeNotifierImpl delegate = createNotifierNode();
    SNode result = new SNodeImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SRelation<SNode, SNode> createSRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SRelation<SNode, SNode> result = new SRelationImpl<>(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SAnnotation createSAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SAnnotation result = new SAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SMetaAnnotation createSMetaAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SMetaAnnotation result = new SMetaAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SProcessingAnnotation createSProcessingAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SProcessingAnnotation result = new SProcessingAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SFeature createSFeature() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SFeature result = new SFeatureImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SLayer createSLayer() {
    LayerNotifierImpl<SNode, SRelation<SNode, SNode>> delegate = new LayerNotifierImpl<>();
    SLayer result = new SLayerImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SaltProject createSaltProject() {
    return new SaltProjectNotifierImpl();
  }

  @Override
  public SCorpus createSCorpus() {
    NodeNotifierImpl delegate = createNotifierNode();
    SCorpus result = new SCorpusImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SDocument createSDocument() {
    NodeNotifierImpl delegate = createNotifierNode();
    SDocument result = new SDocumentImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SCorpusGraph createSCorpusGraph() {
    GraphNotifierImpl<SNode, SRelation<SNode, SNode>> delegate = new GraphNotifierImpl<>();
    SCorpusGraphImpl result = new SCorpusGraphImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SDocumentGraph createSDocumentGraph() {
    GraphNotifierImpl<SNode, SRelation<SNode, SNode>> delegate = new GraphNotifierImpl<>();
    SDocumentGraphImpl result = new SDocumentGraphImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }



  @Override
  public SCorpusRelation createSCorpusRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SCorpusRelation result = new SCorpusRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SCorpusDocumentRelation createSCorpusDocumentRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SCorpusDocumentRelation result = new SCorpusDocumentRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SSpanningRelation createSSpanningRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SSpanningRelation result = new SSpanningRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SDominanceRelation createSDominanceRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SDominanceRelation result = new SDominanceRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SPointingRelation createSPointingRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SPointingRelation result = new SPointingRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SOrderRelation createSOrderRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SOrderRelation result = new SOrderRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public STextualRelation createSTextualRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    STextualRelation result = new STextualRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public STimelineRelation createSTimelineRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    STimelineRelation result = new STimelineRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SMedialRelation createSMedialRelation() {
    RelationNotifierImpl<Node, Node> delegate = createNotifierRelation();
    SMedialRelation result = new SMedialRelationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SSpan createSSpan() {
    NodeNotifierImpl delegate = createNotifierNode();
    SSpan result = new SSpanImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SStructure createSStructure() {
    NodeNotifierImpl delegate = createNotifierNode();
    SStructure result = new SStructureImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public STextualDS createSTextualDS() {
    NodeNotifierImpl delegate = createNotifierNode();
    STextualDS result = new STextualDSImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SMedialDS createSMedialDS() {
    NodeNotifierImpl delegate = createNotifierNode();
    SMedialDS result = new SMedialDSImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public STimeline createSTimeline() {
    NodeNotifierImpl delegate = createNotifierNode();
    STimeline result = new STimelineImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SToken createSToken() {
    NodeNotifierImpl delegate = createNotifierNode();
    SToken result = new STokenImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SCatAnnotation createSCatAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SCatAnnotation result = new SCatAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SPOSAnnotation createSPOSAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SPOSAnnotation result = new SPOSAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SLemmaAnnotation createSLemmaAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SLemmaAnnotation result = new SLemmaAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public STypeAnnotation createSTypeAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    STypeAnnotation result = new STypeAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SWordAnnotation createSWordAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SWordAnnotation result = new SWordAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

  @Override
  public SSentenceAnnotation createSSentenceAnnotation() {
    LabelNotifierImpl delegate = createNotifierLabel();
    SSentenceAnnotation result = new SSentenceAnnotationImpl(delegate);
    delegate.setTypedDelegation(result);
    return result;
  }

}
