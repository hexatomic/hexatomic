package org.corpus_tools.hexatomic.core;

import static org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler.DOCUMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestProjectManager {

  private static final String DOC1_ID = "salt:/rootCorpus/subCorpus1/doc1";
  private static final String DOC3_ID = "salt:/rootCorpus/subCorpus2/doc3";

  private static final String TEST_ANNO_QNAME = "test::anno";

  private ProjectManager projectManager;

  private URI exampleProjectUri;

  private IEventBroker events;

  private ErrorService errorService;

  private EPartService partService;

  @BeforeEach
  public void setUp() {
    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    events = mock(IEventBroker.class);
    when(events.send(anyString(), any())).then(invocation -> {
      projectManager.subscribeUndoOperationAdded(invocation.getArgument(1));
      return true;
    });

    DummySync sync = new DummySync();

    SaltNotificationFactory factory = new SaltNotificationFactory();
    factory.setSync(sync);
    factory.setEvents(events);

    projectManager = new ProjectManager();
    projectManager.events = events;
    
    errorService = mock(ErrorService.class);
    projectManager.errorService = errorService;
    
    partService = mock(EPartService.class);
    projectManager.partService = partService;

    UiStatusReport uiStatus = mock(UiStatusReport.class);
    projectManager.uiStatus = uiStatus;
    
    projectManager.sync = sync;
    projectManager.notificationFactory = factory;

    projectManager.postConstruct();
  }

  @Test
  public void testOpenInvalid() {

    projectManager.open(URI.createFileURI("nonExistingPath"));
    verify(errorService).handleException(any(), any(), any());

    assertThrows(NullPointerException.class, () -> projectManager.open(URI.createFileURI(null)));

  }


  @Test
  public void testEventOnOpen() {
    projectManager.open(exampleProjectUri);
    verify(events).send(eq(Topics.PROJECT_LOADED), anyString());
    verify(events).send(eq(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC), anyString());
    verifyNoMoreInteractions(events);
  }

  @Test
  public void testDocumentCleanup() {
    projectManager.open(exampleProjectUri);

    // Load document once
    Optional<SDocument> document = projectManager.getDocument(DOC3_ID, true);
    assertTrue(document.isPresent());
    if (document.isPresent()) {
      assertNotNull(document.get().getDocumentGraphLocation());
      assertNotNull(document.get().getDocumentGraph());

      // Mock three open documents: two of them are the same
      Map<String, String> state1 = new HashMap<>();
      state1.put(DOCUMENT_ID, DOC3_ID);

      Map<String, String> state2 = new HashMap<>();
      state2.put(DOCUMENT_ID, "salt:/rootCorpus/subCorpus2/doc4");

      Map<String, String> state3 = new HashMap<>();
      state3.put(DOCUMENT_ID, DOC3_ID);

      MPart editor1 = mock(MPart.class);
      MPart editor2 = mock(MPart.class);
      MPart editor3 = mock(MPart.class);

      when(editor1.getPersistedState()).thenReturn(state1);
      when(editor2.getPersistedState()).thenReturn(state2);
      when(editor3.getPersistedState()).thenReturn(state3);

      when(partService.getParts()).thenReturn(Arrays.asList(editor1, editor2, editor3));

      // Notify one of the documents was closed
      projectManager.unloadDocumentGraphWhenClosed(DOC3_ID);
      // Document must still be loaded in memory
      assertNotNull(document.get().getDocumentGraph());

      // Remove the one of the closed parts
      when(partService.getParts()).thenReturn(Arrays.asList(editor2, editor3));
      // Notify about closing one of the documents again
      projectManager.unloadDocumentGraphWhenClosed(DOC3_ID);
      // Document should have been unloaded from memory
      assertNull(document.get().getDocumentGraph());
    }
  }

  @Test
  public void testUndoRedo() {

    projectManager.open(exampleProjectUri);
    assertFalse(projectManager.canUndo());
    assertFalse(projectManager.canRedo());


    // Get the document graph and change the label of the first label a few times
    Optional<SDocument> document = projectManager.getDocument(DOC3_ID, true);
    assertTrue(document.isPresent());
    if (document.isPresent()) {
      SDocumentGraph docGraph = document.get().getDocumentGraph();
      assertNotNull(docGraph);

      List<SToken> token = docGraph.getSortedTokenByText();
      token.get(0).createAnnotation("test", "anno", "0");
      assertFalse(projectManager.canRedo());
      assertFalse(projectManager.canRedo());

      projectManager.addCheckpoint();
      assertTrue(projectManager.canUndo());
      assertFalse(projectManager.canRedo());

      token.get(0).getAnnotation(TEST_ANNO_QNAME).setValue("1");
      projectManager.addCheckpoint();

      token.get(0).getAnnotation(TEST_ANNO_QNAME).setValue("2");
      projectManager.addCheckpoint();

      token.get(0).getAnnotation(TEST_ANNO_QNAME).setValue("3");
      projectManager.addCheckpoint();
      assertEquals("3", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());

      // Add the pointing relation to a layer
      SLayer testLayer = SaltFactory.createSLayer();
      testLayer.setName("test-layer");
      docGraph.addLayer(testLayer);
      SPointingRelation pointing = docGraph.getPointingRelations().get(0);
      testLayer.addRelation(pointing);
      projectManager.addCheckpoint();
      assertEquals(3, docGraph.getLayers().size());

      // Undo the changes
      projectManager.undo();
      assertEquals(2, docGraph.getLayers().size());

      projectManager.undo();
      assertEquals("2", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.undo();
      assertEquals("1", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.undo();
      assertEquals("0", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.undo();
      assertEquals(null, token.get(0).getAnnotation(TEST_ANNO_QNAME));
      assertFalse(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      // Redo the simple label changes
      projectManager.redo();
      assertEquals("0", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.redo();
      assertEquals("1", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.redo();
      assertEquals("2", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.redo();
      assertEquals("3", token.get(0).getAnnotation(TEST_ANNO_QNAME).getValue());
      assertTrue(projectManager.canUndo());
      assertTrue(projectManager.canRedo());

      projectManager.redo();
      assertEquals(3, docGraph.getLayers().size());
      assertTrue(projectManager.canUndo());
      assertFalse(projectManager.canRedo());
    }
  }

  /**
   * Deletes all elements of a document graph and checks if the changes can be undone.
   */
  @Test
  public void testUndoDeletion() {
    projectManager.open(exampleProjectUri);

    // Documents 1 and 3 should be the same
    Optional<SDocument> document1 = projectManager.getDocument(DOC1_ID, true);
    Optional<SDocument> document3 = projectManager.getDocument(DOC3_ID, true);
    assertTrue(document1.isPresent());
    assertTrue(document3.isPresent());

    if (document1.isPresent() && document3.isPresent()) {
      SDocumentGraph docGraph1 = document1.get().getDocumentGraph();
      assertNotNull(docGraph1);

      SDocumentGraph docGraph3 = document3.get().getDocumentGraph();
      assertNotNull(docGraph3);

      assertEquals(new HashSet<>(), docGraph1.findDiffs(docGraph3));

      docGraph1.removeRelations();
      List<SNode> nodes = new LinkedList<>(docGraph1.getNodes());
      for (SNode n : nodes) {
        docGraph1.removeNode(n);
      }
      // This has changed the document graph
      assertFalse(docGraph1.findDiffs(docGraph3).isEmpty());

      projectManager.addCheckpoint();

      projectManager.undo();

      // Test that the graphs are equal again
      assertEquals(new HashSet<>(), docGraph1.findDiffs(docGraph3));
    }
  }

  @Test
  public void testRevertToLastCheckpoint() {

    projectManager.open(exampleProjectUri);
    assertFalse(projectManager.canUndo());
    assertFalse(projectManager.canRedo());

    Optional<SDocument> document = projectManager.getDocument(DOC3_ID, true);
    assertTrue(document.isPresent());
    if (document.isPresent()) {
      SDocumentGraph docGraph = document.get().getDocumentGraph();
      assertNotNull(docGraph);

      // Change a token annotation without creating a checkpoint
      List<SToken> token = docGraph.getSortedTokenByText();
      token.get(0).createAnnotation("test", "anno", "0");
      assertTrue(token.get(0).containsLabel("test::anno"));
      token.get(1).createAnnotation("test", "anno", "1");
      assertTrue(token.get(1).containsLabel("test::anno"));

      assertFalse(projectManager.canRedo());
      assertFalse(projectManager.canRedo());

      // Revert changes and check that the created annotations are gone
      projectManager.revertToLastCheckpoint();
      assertFalse(token.get(0).containsLabel("test::anno"));
      assertFalse(token.get(1).containsLabel("test::anno"));

      assertFalse(projectManager.canRedo());
      assertFalse(projectManager.canRedo());
    }
  }

  /**
   * If a checkpoint was added due to user interaction, all "redo" operations should become invalid.
   */
  @Test
  public void testNoRedoAfterManualCheckpoint() {
    projectManager.open(exampleProjectUri);

    Optional<SDocument> document = projectManager.getDocument(DOC1_ID, true);
    assertTrue(document.isPresent());

    if (document.isPresent()) {
      SDocumentGraph docGraph = document.get().getDocumentGraph();
      assertNotNull(docGraph);

      String textBefore = docGraph.getTextualDSs().get(0).getText();
      docGraph.insertTokenAt(docGraph.getTextualDSs().get(0), 0, "Before-Token", false);
      projectManager.addCheckpoint();

      assertNotEquals(textBefore, docGraph.getTextualDSs().get(0).getText());

      assertTrue(projectManager.canUndo());
      assertFalse(projectManager.canRedo());

      projectManager.undo();
      assertEquals(textBefore, docGraph.getTextualDSs().get(0).getText());

      // Add a manual change
      docGraph.createFeature("test", "feature", "somevalue");
      projectManager.addCheckpoint();

      assertFalse(projectManager.canRedo());
      assertTrue(projectManager.canUndo());

    }
  }
}
