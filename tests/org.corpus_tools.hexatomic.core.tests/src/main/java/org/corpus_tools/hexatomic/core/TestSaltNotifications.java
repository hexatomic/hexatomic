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

  @Test
  public void testExampleGraph() {
    // Create a document with a single primary data text
    SDocument doc = SaltFactory.createSDocument();
    SampleGenerator.createPrimaryData(doc);

    // Document graph connection is set as a label and consists of 3 events(set namespace, set name,
    // set value)
    SFeature docGraphFeat = doc.getFeature(SaltUtil.FEAT_SDOCUMENT_GRAPH_QNAME);
    verify(events, times(3)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(docGraphFeat));
    verify(events, times(3)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(docGraphFeat));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(docGraphFeat));


    // The connection is double linked
    SFeature docFeat =
        doc.getDocumentGraph().getFeature(SaltUtil.FEAT_SDOCUMENT_QNAME);
    verify(events, times(3)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(docFeat));
    verify(events, times(3)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(docFeat));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(docFeat));
    
    // Textual data source is added as node, its content and ID is a feature
    STextualDS text = doc.getDocumentGraph().getTextualDSs().get(0);
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text));
    SFeature textDataFeat = text.getFeature(SaltUtil.FEAT_SDATA_QNAME);
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textDataFeat));
    verify(events, times(3)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(textDataFeat));
    verify(events, times(3)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(textDataFeat));
    // Text text ID is an own object that is added to the node as label
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(text.getIdentifier()));

    // The name feature is first created with 3 calls, then the value is set: this makes 4 events in
    // total
    SFeature textNameFeat = text.getFeature(SaltUtil.FEAT_NAME_QNAME);
    verify(events, times(4)).send(eq(Topics.ANNOTATION_BEFORE_MODIFICATION), eq(textNameFeat));
    verify(events, times(4)).send(eq(Topics.ANNOTATION_AFTER_MODIFICATION), eq(textNameFeat));
    verify(events).send(eq(Topics.ANNOTATION_ADDED), eq(textNameFeat));

    // This have been all recorded events
    verifyNoMoreInteractions(events);

  }
}
