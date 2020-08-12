package org.corpus_tools.hexatomic.core;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.List;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.core.undo.operations.AddLayerToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddNodeToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddNodeToLayerOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddRelationToGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.AddRelationToLayerOperation;
import org.corpus_tools.hexatomic.core.undo.operations.LabelAddOperation;
import org.corpus_tools.hexatomic.core.undo.operations.LabelModifyOperation;
import org.corpus_tools.hexatomic.core.undo.operations.LabelRemoveOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveLayerFromGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveNodeFromGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.RemoveRelationFromGraphOperation;
import org.corpus_tools.hexatomic.core.undo.operations.SetSourceOperation;
import org.corpus_tools.hexatomic.core.undo.operations.SetTargetOperation;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusDocumentRelation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SMedialDS;
import org.corpus_tools.salt.common.SMedialRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SAbstractAnnotation;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.core.SProcessingAnnotation;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.corpus_tools.salt.semantics.SSentenceAnnotation;
import org.corpus_tools.salt.semantics.STypeAnnotation;
import org.corpus_tools.salt.semantics.SWordAnnotation;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSaltNotifications {

  private IEventBroker events;


  @BeforeEach
  public void setUp() {

    events = mock(IEventBroker.class);
    final ErrorService errorService = mock(ErrorService.class);
    final EPartService partService = mock(EPartService.class);
    final UiStatusReport uiStatus = mock(UiStatusReport.class);

    final UISynchronize sync = new DummySync();

    SaltNotificationFactory factory = new SaltNotificationFactory();
    factory.setSync(sync);
    factory.setEvents(events);

    ProjectManager projectManager = new ProjectManager();
    projectManager.events = events;
    projectManager.errorService = errorService;
    projectManager.partService = partService;
    projectManager.uiStatus = uiStatus;
    projectManager.sync = new DummySync();
    projectManager.notificationFactory = factory;

    projectManager.postConstruct();

    SaltFactory.setFactory(factory);
  }

  /**
   * Create a simple example graph and check that all events have been fired.
   */
  @Test
  public void testExampleGraph() {

    /// Part 1, create and manipulate a sample graph

    // Create a document with a single primary data text
    final SDocument doc = SaltFactory.createSDocument();
    SampleGenerator.createPrimaryData(doc);

    // Textual data source is added as node, its content and ID is a feature
    final STextualDS text = doc.getDocumentGraph().getTextualDSs().get(0);

    // Add a token "Is"
    final SToken tok = doc.getDocumentGraph().createToken(text, 0, 2);
    final STextualRelation textRel = doc.getDocumentGraph().getTextualRelations().get(0);

    // Add and remove an annotation on the token
    final SAnnotation tokAnno = tok.createAnnotation("test", "somename", "anyvalue");
    tok.removeLabel("test", "somename");

    // Add and remove a processing annotation on the token annotation
    final SProcessingAnnotation processingAnno =
        tokAnno.createProcessingAnnotation("salt", "processing", "anno");
    tokAnno.removeLabel("salt::processing");

    // Add and remove a layer
    final SLayer layer = SaltFactory.createSLayer();
    doc.getDocumentGraph().addLayer(layer);
    layer.addNode(tok);
    layer.removeNode(tok);
    doc.getDocumentGraph().removeLayer(layer);


    // Remove the token, this should also remove the relation
    doc.getDocumentGraph().removeNode(tok);

    // Part 2: verify all expected events have been send

    // Document graph connection is set as a label and consists of 3 events(set namespace, set name,
    // set value)
    // The connection is double linked
    verifyCreatedAnnoEvents(doc.getDocumentGraph().getFeature(SaltUtil.FEAT_SDOCUMENT_QNAME));
    verifyCreatedAnnoEvents(doc.getFeature(SaltUtil.FEAT_SDOCUMENT_GRAPH_QNAME));


    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(AddNodeToGraphOperation.class), hasProperty("changedElement", is(text)))));
    // Text text ID is an own object that is added to the node as label
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(
            allOf(instanceOf(LabelAddOperation.class), hasProperty("changedContainer", is(text)),
                hasProperty("qname", is(text.getIdentifier().getQName())))));
    verifySetNameEvents(text);

    verifyCreatedAnnoEvents(text.getFeature(SaltUtil.FEAT_SDATA_QNAME));

    // Test all events for the created token
    // token has been added to times, once to the graph and once to the layer
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(AddNodeToGraphOperation.class), hasProperty("changedElement", is(tok)))));
    verifySetNameEvents(tok);
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(AddNodeToLayerOperation.class), hasProperty("changedElement", is(tok)))));

    // Creating a token also adds a relation from the token to the text
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddRelationToGraphOperation.class),
            hasProperty("changedElement", is(textRel)))));
    verifySetNameEvents(textRel);

    // Setting the target and source of the relation creates each events
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(SetSourceOperation.class), hasProperty("changedElement", is(textRel)))));
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(SetTargetOperation.class), hasProperty("changedElement", is(textRel)))));

    verifyCreatedAnnoEvents(textRel.getFeature(SaltUtil.FEAT_SSTART_QNAME));
    verifyCreatedAnnoEvents(textRel.getFeature(SaltUtil.FEAT_SEND_QNAME));

    verifyCreatedAnnoEvents(tokAnno);

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(LabelRemoveOperation.class), hasProperty("changedElement", is(tokAnno)))));

    verifyCreatedAnnoEvents(processingAnno);

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelRemoveOperation.class),
            hasProperty("changedElement", is(processingAnno)))));

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddLayerToGraphOperation.class),
            hasProperty("changedElement", is(layer)))));
    verifySetNameEvents(layer);


    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(instanceOf(RemoveNodeFromGraphOperation.class)));

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(instanceOf(RemoveLayerFromGraphOperation.class)));
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(RemoveRelationFromGraphOperation.class),
            hasProperty("changedElement", is(textRel)))));
  }

  /**
   * Tests the different removeAll functions of the graph objects.
   */
  @Test
  public void testRemoveAll() {

    final SCorpusGraph graph = SaltFactory.createSCorpusGraph();

    final SCorpus corpus = SaltFactory.createSCorpus();
    final SDocument doc = SaltFactory.createSDocument();

    graph.addNode(corpus);
    graph.addNode(doc);

    graph.createProcessingAnnotation("test", "name", "something");

    verifySetNameEvents(corpus);
    verifySetNameEvents(doc);
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddNodeToGraphOperation.class),
            hasProperty("changedElement", is(corpus)),
            hasProperty("changedContainer", is(graph)))));
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddNodeToGraphOperation.class),
            hasProperty("changedElement", is(doc)), hasProperty("changedContainer", is(graph)))));


    // Add and remove an annotation on the document and on the label itself
    final SMetaAnnotation docAnno = doc.createMetaAnnotation("meta", "anno", "value");
    docAnno.createFeature("another", "feature", "value");

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelAddOperation.class), hasProperty("changedContainer", is(doc)),
            hasProperty("qname", is("meta::anno")))));
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(
            allOf(instanceOf(LabelAddOperation.class), hasProperty("changedContainer", is(docAnno)),
                hasProperty("qname", is("another::feature")))));

    // Create a relation
    final SCorpusDocumentRelation corpusRel = SaltFactory.createSCorpusDocumentRelation();
    corpusRel.createProcessingAnnotation("just", "another", "annotation");

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(
            allOf(instanceOf(LabelAddOperation.class), hasProperty("qname", is("just::another")),
                hasProperty("changedContainer", is(corpusRel)))));

    corpusRel.setSource(corpus);
    corpusRel.setTarget(doc);

    graph.addRelation(corpusRel);
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddRelationToGraphOperation.class),
            hasProperty("changedElement", is(corpusRel)))));
    graph.removeRelations();

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(RemoveRelationFromGraphOperation.class),
            hasProperty("changedElement", is(corpusRel)),
            hasProperty("changedContainer", is(graph)))));

    // Create a layer and add the node and relation to it
    final SLayer layer = SaltFactory.createSLayer();
    layer.addNode(doc);
    layer.addRelation(corpusRel);
    layer.createProcessingAnnotation("test", "name", "something");

    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddNodeToLayerOperation.class),
            hasProperty("changedContainer", is(layer)), hasProperty("changedElement", is(doc)))));
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(AddRelationToLayerOperation.class),
            hasProperty("changedContainer", is(layer)),
            hasProperty("changedElement", is(corpusRel)))));

    // Call the removeAll functions on the created objects
    graph.removeAll();
    layer.removeAll();
    corpusRel.removeAll();
    docAnno.removeAll();
    doc.removeAll();


    // Check all removing the labels add the events
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(LabelRemoveOperation.class), hasProperty("changedContainer", is(graph)))));

    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(LabelRemoveOperation.class), hasProperty("changedContainer", is(layer)))));

    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelRemoveOperation.class),
            hasProperty("changedContainer", is(corpusRel)))));

    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelRemoveOperation.class),
            hasProperty("changedContainer", is(docAnno)))));

    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED), argThat(
        allOf(instanceOf(LabelRemoveOperation.class), hasProperty("changedContainer", is(doc)))));

  }

  private void verifySetNameEvents(SAnnotationContainer element) {
    SFeature nameFeat = element.getFeature(SaltUtil.FEAT_NAME_QNAME);
    // The name feature is first created by setting the namespace/name/value at least once, each
    // call creating an event
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelModifyOperation.class),
            hasProperty("changedContainer", is(element)),
            hasProperty("namespace", is(nameFeat.getNamespace())))));
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelModifyOperation.class),
            hasProperty("changedContainer", is(element)),
            hasProperty("name", is(nameFeat.getName())))));
    // The event needs to reset the value and therefore stores the old value, which is null
    verify(events, atLeastOnce()).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelModifyOperation.class),
            hasProperty("changedContainer", is(element)), hasProperty("value", nullValue()))));
  }

  private void verifyCreatedAnnoEvents(SAbstractAnnotation anno) {
    Object container = SaltHelper.resolveDelegation(anno.getContainer());
    verify(events).send(eq(Topics.UNDO_OPERATION_ADDED),
        argThat(allOf(instanceOf(LabelAddOperation.class),
            hasProperty("changedContainer", is(container)),
            hasProperty("qname", is(anno.getQName())))));
  }


  /**
   * This test ensures creating a full Salt example does not trigger any exceptions.
   */
  @Test
  public void testCreateAllAnnotationTypesFromFactory() {
    SaltProject project = SampleGenerator.createSaltProject();
    List<SDocument> docs = project.getCorpusGraphs().get(0).getDocuments();
    assertEquals(4, docs.size());
    SampleGenerator.createDocumentStructure(docs.get(0));
    assertEquals(11, docs.get(0).getDocumentGraph().getTokens().size());
    docs.get(0).getDocumentGraph().getStructures().get(0)
        .addAnnotation(SaltFactory.createSCatAnnotation());

    SampleGenerator.createDialogue(docs.get(1));
    assertEquals(14, docs.get(1).getDocumentGraph().getTokens().size());

    // Add some more specific Salt objects not generated by the sample
    SampleGenerator.createPrimaryData(docs.get(2));
    SampleGenerator.createTokens(docs.get(2));
    STypeAnnotation typeAnno = SaltFactory.createSTypeAnnotation();
    SWordAnnotation wordAnno = SaltFactory.createSWordAnnotation();
    SSentenceAnnotation sentenceAnno = SaltFactory.createSSentenceAnnotation();

    docs.get(2).getDocumentGraph().getTokens().get(0).addAnnotation(typeAnno);
    docs.get(2).getDocumentGraph().getTokens().get(0).addAnnotation(wordAnno);
    SSpan sentenceSpan =
        docs.get(2).getDocumentGraph().createSpan(docs.get(2).getDocumentGraph().getTokens());
    sentenceSpan.addAnnotation(sentenceAnno);
    assertEquals(4, docs.get(2).getDocumentGraph().getTokens().get(0).getAnnotations().size());
    assertEquals(1, sentenceSpan.getAnnotations().size());

    // Also create the Salt core types directly
    assertNotNull(SaltFactory.createSGraph());
    assertNotNull(SaltFactory.createSNode());
    assertNotNull(SaltFactory.createSRelation());
    assertNotNull(SaltFactory.createSLayer());

    // Add a media type relation
    SMedialDS mediaDS = SaltFactory.createSMedialDS();
    docs.get(2).getDocumentGraph().addNode(mediaDS);
    SMedialRelation mediaRel = SaltFactory.createSMedialRelation();
    mediaRel.setSource(docs.get(2).getDocumentGraph().getTokens().get(0));
    mediaRel.setTarget(mediaDS);
    docs.get(2).getDocumentGraph().addRelation(mediaRel);

    // Check that events have been fired
    verify(events, atLeastOnce()).send(anyString(), any());

  }

}
