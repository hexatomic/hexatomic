package org.corpus_tools.hexatomic.graph;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestGraphEditor {

  private static final String DOC1_ID = "salt:/rootCorpus/subCorpus1/doc1";
  private GraphEditor editor;


  @BeforeEach
  void setUp() {
    editor = spy(new GraphEditor());
  }

  @Test
  void testIgnoreEmptyDocumentChanges() {
    Map<String, String> persistedState = new LinkedHashMap<>();
    persistedState.put(OpenSaltDocumentHandler.DOCUMENT_ID, DOC1_ID);
    editor.thisPart = mock(MPart.class);
    when(editor.thisPart.getPersistedState()).thenReturn(persistedState);
    // An empty change set should not trigger the updateView function
    List<ReversibleOperation> changes = new ArrayList<>();
    ChangeSet changeSet = new ChangeSet(changes);
    editor.onCheckpointCreated(changeSet);
    verify(editor, Mockito.never()).updateView(anyBoolean(), anyBoolean());
  }

  @Test
  void testIgnoreOtherDocumentChanges() {
    Map<String, String> persistedState = new LinkedHashMap<>();
    persistedState.put(OpenSaltDocumentHandler.DOCUMENT_ID, DOC1_ID);
    editor.thisPart = mock(MPart.class);
    when(editor.thisPart.getPersistedState()).thenReturn(persistedState);

    // Add change for a different document which should be ignored
    List<ReversibleOperation> changes = new ArrayList<>();
    SDocumentGraph otherGraph = SaltFactory.createSDocumentGraph();
    otherGraph.setId("salt:/rootCorpus/subCorpus1/doc2");

    ReversibleOperation op1 = mock(ReversibleOperation.class);
    when(op1.getChangedContainer()).thenReturn(otherGraph);
    changes.add(op1);

    ChangeSet changeSet = new ChangeSet(changes);
    editor.onCheckpointCreated(changeSet);

    verify(editor, Mockito.never()).updateView(anyBoolean(), anyBoolean());
  }

  @Test
  void testUpdateOnChange() {
    Map<String, String> persistedState = new LinkedHashMap<>();
    persistedState.put(OpenSaltDocumentHandler.DOCUMENT_ID, DOC1_ID);
    editor.thisPart = mock(MPart.class);
    when(editor.thisPart.getPersistedState()).thenReturn(persistedState);

    // Create a graph and document which are used as the objects
    // the graph editor should work on.
    final SDocumentGraph graph = SaltFactory.createSDocumentGraph();
    graph.setId(DOC1_ID);
    final SDocument doc = SaltFactory.createSDocument();
    doc.setDocumentGraph(graph);
    doc.setId(DOC1_ID);

    ProjectManager projectManager = mock(ProjectManager.class);
    editor.projectManager = projectManager;
    when(projectManager.getDocument(anyString())).thenReturn(Optional.of(doc));

    // Add change for same document the editor is supposed to have opened
    ReversibleOperation op1 = mock(ReversibleOperation.class);
    when(op1.getChangedContainer()).thenReturn(graph);
    List<ReversibleOperation> changes = new ArrayList<>();
    changes.add(op1);

    ChangeSet changeSet = new ChangeSet(changes);
    // Call the event handler, but make sure the updateView function is mocked.
    // Using the actual implementation would need a lot of mocked dependencies
    doNothing().when(editor).updateView(anyBoolean(), anyBoolean());
    editor.onCheckpointCreated(changeSet);

    verify(editor, Mockito.times(1)).updateView(false, false);
  }
}
