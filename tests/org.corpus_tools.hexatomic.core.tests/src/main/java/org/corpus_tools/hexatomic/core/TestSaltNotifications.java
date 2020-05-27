package org.corpus_tools.hexatomic.core;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SFeature;
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
   * Create a simple example graph and check that all events have been fired (and not more).
   */
  @Test
  public void testExampleGraph() {
    // Create a document with a single primary data text
    SDocument doc = SaltFactory.createSDocument();
    SampleGenerator.createPrimaryData(doc);

    // Document graph connection is set as a label and consists of 3 events(set namespace, set name,
    // set value)
    verifyCreatedFeatureEvents(doc.getFeature(SaltUtil.FEAT_SDOCUMENT_GRAPH_QNAME));

    // The connection is double linked
    verifyCreatedFeatureEvents(doc.getDocumentGraph().getFeature(SaltUtil.FEAT_SDOCUMENT_QNAME));

    // Textual data source is added as node, its content and ID is a feature
    STextualDS text = doc.getDocumentGraph().getTextualDSs().get(0);
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text));
    // Text text ID is an own object that is added to the node as label
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text.getIdentifier()));
    verifySetNameEvents(text);

    verifyCreatedFeatureEvents(text.getFeature(SaltUtil.FEAT_SDATA_QNAME));

    // This have been all recorded events
    verifyNoMoreInteractions(events);

    // Add a token "Is"
    SToken tok = doc.getDocumentGraph().createToken(text, 0, 2);

    // Test all events for the created token
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(tok));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(tok.getIdentifier()));
    verifySetNameEvents(tok);


    // Creating a token also adds a relation from the token to the text
    STextualRelation textRel = doc.getDocumentGraph().getTextualRelations().get(0);
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textRel.getIdentifier()));
    verifySetNameEvents(textRel);

    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textRel));
    // Setting the target and source of the relation creates each 2 events
    verify(events, times(2)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(textRel));
    verify(events, times(2)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(textRel));

    verifyCreatedFeatureEvents(textRel.getFeature(SaltUtil.FEAT_SSTART_QNAME));
    verifyCreatedFeatureEvents(textRel.getFeature(SaltUtil.FEAT_SEND_QNAME));

    verifyNoMoreInteractions(events);
  }

  private void verifySetNameEvents(SAnnotationContainer element) {
    SFeature nameFeat = element.getFeature(SaltUtil.FEAT_NAME_QNAME);
    // The name feature is first created with 3 calls, then the value is set: this makes 4 events in
    // total
    verify(events, times(4)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(nameFeat));
    verify(events, times(4)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(nameFeat));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(nameFeat));
  }

  private void verifyCreatedFeatureEvents(SFeature feat) {
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(feat));
    // Creating a new feature consists of 3 events(set namespace, set name,
    // set value)
    verify(events, times(3)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(feat));
    verify(events, times(3)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(feat));
  }


}
