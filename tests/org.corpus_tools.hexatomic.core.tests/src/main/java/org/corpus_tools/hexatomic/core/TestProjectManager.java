package org.corpus_tools.hexatomic.core;

import static org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler.DOCUMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestProjectManager {

  private ProjectManager projectManager;

  private URI exampleProjectUri;

  private IEventBroker events;

  private ErrorService errorService;

  private EPartService partService;

  private UiStatusReport uiStatus;


  @BeforeEach
  public void setUp() throws Exception {
    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    events = mock(IEventBroker.class);
    when(events.send(anyString(), any()))
        .then((invocation) -> {
          projectManager.subscribeUndoOperationAdded(invocation.getArgument(1));
          return true;
        });
    
    errorService = mock(ErrorService.class);
    partService = mock(EPartService.class);
    uiStatus = mock(UiStatusReport.class);


    DummySync sync = new DummySync();

    SaltNotificationFactory factory = new SaltNotificationFactory();
    factory.setSync(sync);
    factory.setEvents(events);

    projectManager = new ProjectManager();
    projectManager.events = events;
    projectManager.errorService = errorService;
    projectManager.partService = partService;
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
    verifyNoMoreInteractions(events);
  }

  @Test
  public void testDocumentCleanup() {
    projectManager.open(exampleProjectUri);

    String documentID = "salt:/rootCorpus/subCorpus2/doc3";

    // Load document once
    Optional<SDocument> document = projectManager.getDocument(documentID, true);
    assertTrue(document.isPresent());
    assertNotNull(document.get().getDocumentGraphLocation());
    assertNotNull(document.get().getDocumentGraph());

    // Mock three open documents: two of them are the same
    Map<String, String> state1 = new HashMap<>();
    state1.put(DOCUMENT_ID, documentID);

    Map<String, String> state2 = new HashMap<>();
    state2.put(DOCUMENT_ID, "salt:/rootCorpus/subCorpus2/doc4");

    Map<String, String> state3 = new HashMap<>();
    state3.put(DOCUMENT_ID, documentID);

    MPart editor1 = mock(MPart.class);
    MPart editor2 = mock(MPart.class);
    MPart editor3 = mock(MPart.class);

    when(editor1.getPersistedState()).thenReturn(state1);
    when(editor2.getPersistedState()).thenReturn(state2);
    when(editor3.getPersistedState()).thenReturn(state3);

    when(partService.getParts()).thenReturn(Arrays.asList(editor1, editor2, editor3));

    // Notify one of the documents was closed
    projectManager.unloadDocumentGraphWhenClosed(documentID);
    // Document must still be loaded in memory
    assertNotNull(document.get().getDocumentGraph());

    // Remove the one of the closed parts
    when(partService.getParts()).thenReturn(Arrays.asList(editor2, editor3));
    // Notify about closing one of the documents again
    projectManager.unloadDocumentGraphWhenClosed(documentID);
    // Document should have been unloaded from memory
    assertNull(document.get().getDocumentGraph());
  }

  @Test
  public void testUndoRedo() {

    projectManager.open(exampleProjectUri);
    assertFalse(projectManager.canUndo());
    assertFalse(projectManager.canRedo());


    // Get the document graph and change the label of the first label a few times
    String documentID = "salt:/rootCorpus/subCorpus2/doc3";
    Optional<SDocument> document = projectManager.getDocument(documentID, true);
    assertTrue(document.isPresent());
    SDocumentGraph docGraph = document.get().getDocumentGraph();
    assertNotNull(docGraph);

    List<SToken> token = docGraph.getSortedTokenByText();
    token.get(0).createAnnotation("test", "anno", "0");
    assertFalse(projectManager.canRedo());
    assertFalse(projectManager.canRedo());

    projectManager.addCheckpoint();
    assertTrue(projectManager.canUndo());
    assertFalse(projectManager.canRedo());

    token.get(0).getAnnotation("test::anno").setValue("1");
    projectManager.addCheckpoint();

    token.get(0).getAnnotation("test::anno").setValue("2");
    projectManager.addCheckpoint();

    token.get(0).getAnnotation("test::anno").setValue("3");
    projectManager.addCheckpoint();

    assertEquals("3", token.get(0).getAnnotation("test::anno").getValue());

    // Undo the changes
    projectManager.undo();
    assertEquals("2", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.undo();
    assertEquals("1", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.undo();
    assertEquals("0", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.undo();
    assertEquals(null, token.get(0).getAnnotation("test::anno"));
    assertFalse(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    // Redo the simple label changes
    projectManager.redo();
    assertEquals("0", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.redo();
    assertEquals("1", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.redo();
    assertEquals("2", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertTrue(projectManager.canRedo());

    projectManager.redo();
    assertEquals("3", token.get(0).getAnnotation("test::anno").getValue());
    assertTrue(projectManager.canUndo());
    assertFalse(projectManager.canRedo());
  }


}
