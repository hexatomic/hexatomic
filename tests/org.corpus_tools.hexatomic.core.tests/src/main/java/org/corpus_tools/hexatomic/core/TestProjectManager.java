package org.corpus_tools.hexatomic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestProjectManager {

  private ProjectManager projectManager;

  private URI exampleProjectUri;

  private IEventBroker events;

  private ErrorService errorService;

  @BeforeEach
  public void setUp() throws Exception {
    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    events = mock(IEventBroker.class);
    errorService = mock(ErrorService.class);

    projectManager = new ProjectManager();
    projectManager.events = events;
    projectManager.errorService = errorService;

    projectManager.postConstruct();

  }

  @Test
  public void testOpenInvalid() {

    projectManager.open(URI.createFileURI("nonExistingPath"));
    verify(errorService).handleException(any(), any(), any());

    assertThrows(NullPointerException.class, () -> projectManager.open(URI.createFileURI(null)));

  }

  @Test
  public void testOpenExample() {

    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 0);

    projectManager.open(exampleProjectUri);

    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 1);

    assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").isPresent());
    assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc2").isPresent());

    assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());
    assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());

  }

  @Test
  public void testEventOnOpen() {

    projectManager.open(exampleProjectUri);
    verify(events).send(eq(Topics.CORPUS_STRUCTURE_CHANGED), anyString());

  }

}
