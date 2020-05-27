package org.corpus_tools.hexatomic.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAbstractAnnotation;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSaltNotifications {
  private ProjectManager projectManager;

  private IEventBroker events;

  private ErrorService errorService;

  private EPartService partService;

  private UiStatusReport uiStatus;


  @BeforeEach
  public void setUp() throws Exception {

    events = mock(IEventBroker.class);
    errorService = mock(ErrorService.class);
    partService = mock(EPartService.class);
    uiStatus = mock(UiStatusReport.class);

    projectManager = new ProjectManager();
    projectManager.events = events;
    projectManager.errorService = errorService;
    projectManager.partService = partService;
    projectManager.uiStatus = uiStatus;
    projectManager.sync = new UISynchronize() {

      @Override
      public void syncExec(Runnable runnable) {
        runnable.run();
      }

      @Override
      public void asyncExec(Runnable runnable) {
        runnable.run();
      }
    };

    projectManager.postConstruct();

    SaltFactory.setFactory(new SaltNotificationFactory(events, projectManager));

  }

  /**
   * Create a simple example graph and check that all events have been fired.
   */
  @Test
  public void testExampleGraph() {

    /// Part 1, create and manipulate a sample graph

    // Create a document with a single primary data text
    SDocument doc = SaltFactory.createSDocument();
    SampleGenerator.createPrimaryData(doc);

    // Textual data source is added as node, its content and ID is a feature
    STextualDS text = doc.getDocumentGraph().getTextualDSs().get(0);

    // Add a token "Is"
    SToken tok = doc.getDocumentGraph().createToken(text, 0, 2);
    STextualRelation textRel = doc.getDocumentGraph().getTextualRelations().get(0);

    // Add and remove an annotation on the token
    SAnnotation tokAnno = tok.createAnnotation("test", "somename", "anyvalue");
    tok.removeLabel("test", "somename");

    // Add and remove a layer
    SLayer layer = SaltFactory.createSLayer();
    doc.getDocumentGraph().addLayer(layer);
    layer.addNode(tok);
    layer.removeNode(tok);
    doc.getDocumentGraph().removeLayer(layer);

    // Remove the token, this should also remove the relation
    doc.getDocumentGraph().removeNode(tok);

    // Part 2: verify all expected events have been send

    // Document graph connection is set as a label and consists of 3 events(set namespace, set name,
    // set value)
    verifyCreatedAnnoEvents(doc.getFeature(SaltUtil.FEAT_SDOCUMENT_GRAPH_QNAME));

    // The connection is double linked
    verifyCreatedAnnoEvents(doc.getDocumentGraph().getFeature(SaltUtil.FEAT_SDOCUMENT_QNAME));


    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text));
    // Text text ID is an own object that is added to the node as label
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text.getIdentifier()));
    verifySetNameEvents(text);

    verifyCreatedAnnoEvents(text.getFeature(SaltUtil.FEAT_SDATA_QNAME));

    // Test all events for the created token
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(tok.getIdentifier()));
    verifySetNameEvents(tok);
    // token has been added to times, once to the graph and once to the layer
    verify(events, times(2)).send(eq(Topics.ANNOTATION_ADDED), eq(tok));

    // Creating a token also adds a relation from the token to the text
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textRel.getIdentifier()));
    verifySetNameEvents(textRel);

    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textRel));
    // Setting the target and source of the relation creates each 2 events
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(textRel));
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(textRel));

    verifyCreatedAnnoEvents(textRel.getFeature(SaltUtil.FEAT_SSTART_QNAME));
    verifyCreatedAnnoEvents(textRel.getFeature(SaltUtil.FEAT_SEND_QNAME));

    verifyCreatedAnnoEvents(tokAnno);
    
    verify(events).send(eq(Topics.ANNOTATION_REMOVED), eq(tokAnno));

    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(layer));
    verifySetNameEvents(layer);
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(layer.getIdentifier()));


    verify(events, times(2)).send(eq(Topics.ANNOTATION_REMOVED), eq(tok));
    verify(events).send(eq(Topics.ANNOTATION_REMOVED), eq(layer));
    verify(events).send(eq(Topics.ANNOTATION_REMOVED), eq(textRel));
  }

  private void verifySetNameEvents(SAnnotationContainer element) {
    SFeature nameFeat = element.getFeature(SaltUtil.FEAT_NAME_QNAME);
    // The name feature is first created by setting the namespace/name/value at least once, each
    // call creating an event
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(nameFeat));
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(nameFeat));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(nameFeat));
  }

  private void verifyCreatedAnnoEvents(SAbstractAnnotation anno) {
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(anno));
    // The annotation/feature is first created by setting the namespace/name/value at least once,
    // each call creating an event
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(anno));
    verify(events, atLeastOnce()).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(anno));
  }


}
