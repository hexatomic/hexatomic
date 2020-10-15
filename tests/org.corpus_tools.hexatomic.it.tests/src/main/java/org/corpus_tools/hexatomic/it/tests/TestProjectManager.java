package org.corpus_tools.hexatomic.it.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.Difference;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestProjectManager {

  private static final String DOC1_SALT_ID = "salt:/rootCorpus/subCorpus1/doc1";

  private SWTWorkbenchBot bot;

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ProjectManager projectManager;
  private ErrorService errorService;

  @BeforeEach
  void setup() {
    IEclipseContext ctx = TestHelper.getEclipseContext();

    bot = new SWTWorkbenchBot(ctx);

    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);
    errorService = ContextInjectionFactory.make(ErrorService.class, ctx);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    errorService.clearLastException();

    TestHelper.executeNewProjectCommand(commandService, handlerService);
  }

  @Test
  @Order(1)
  public void testOpenAndSave() throws IOException {

    projectManager.getProject().setName(null);
    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 0);

    // Open the example project
    projectManager.open(exampleProjectUri);

    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 1);

    final String[] docIDs = {DOC1_SALT_ID, "salt:/rootCorpus/subCorpus1/doc2",
        "salt:/rootCorpus/subCorpus2/doc3", "salt:/rootCorpus/subCorpus2/doc4"};

    for (String id : docIDs) {
      assertTrue(projectManager.getDocument(id).isPresent());
    }

    assertFalse(projectManager.isDirty());

    // Load a single document into memory
    Optional<SDocument> optionalDoc1 =
        projectManager.getDocument(DOC1_SALT_ID, true);
    assertTrue(optionalDoc1.isPresent());
    if (optionalDoc1.isPresent()) {
      SDocument doc1 = optionalDoc1.get();
      SDocumentGraph doc1Graph = doc1.getDocumentGraph();
      assertNotNull(doc1Graph);

      // Apply some changes to the loaded document graph
      List<SToken> tokens = doc1Graph.getSortedTokenByText();
      doc1Graph.createSpan(tokens.get(0), tokens.get(1));

      projectManager.addCheckpoint();

      assertTrue(projectManager.isDirty());
      bot.waitUntil(new ICondition() {
        
        @Override
        public boolean test() throws Exception {
          return bot.toolbarButtonWithTooltip("Undo (Ctrl+Z)").isEnabled();
        }
        
        @Override
        public void init(SWTBot bot) {
        }
        
        @Override
        public String getFailureMessage() {
          return "Undo toolbar button not enabled";
        }
      });
      // Also check that the undo toolbar item has been enabled
      assertTrue(bot.toolbarButtonWithTooltip("Undo (Ctrl+Z)").isEnabled());
      assertFalse(bot.toolbarButtonWithTooltip("Redo (Shift+Ctrl+Z)").isEnabled());

      // Save the project to a different location
      Path tmpDir = Files.createTempDirectory("hexatomic-project-manager-test");

      Map<String, String> params = new HashMap<>();
      params.put(CommandParams.LOCATION, tmpDir.toString());
      final ParameterizedCommand cmdSaveAs = commandService
          .createCommand("org.corpus_tools.hexatomic.core.command.save_as_salt_project", params);

      UIThreadRunnable.syncExec(() -> {
        handlerService.executeHandler(cmdSaveAs);
      });

      assertFalse(projectManager.isDirty());

      // Compare the saved project with the one currently in memory
      SaltProject savedProject =
          SaltUtil.loadCompleteSaltProject(URI.createFileURI(tmpDir.toString()));

      SDocument savedDoc = (SDocument) savedProject.getCorpusGraphs().get(0)
          .getNode(DOC1_SALT_ID);

      Set<Difference> docDiff =
          SaltUtil.compare(doc1Graph).with(savedDoc.getDocumentGraph()).andFindDiffs();
      assertThat(docDiff, is(empty()));

      // Apply some more changes to the loaded document graph and save to same
      // location
      optionalDoc1 = projectManager.getDocument(DOC1_SALT_ID, true);
      assertTrue(optionalDoc1.isPresent());
      if (optionalDoc1.isPresent()) {
        doc1 = optionalDoc1.get();
        doc1Graph = doc1.getDocumentGraph();
        assertNotNull(doc1Graph);
        tokens = doc1Graph.getSortedTokenByText();
        doc1Graph.createSpan(tokens.get(2), tokens.get(3));

        projectManager.addCheckpoint();

        assertTrue(projectManager.isDirty());

        final ParameterizedCommand cmdSave = commandService
            .createCommand("org.corpus_tools.hexatomic.core.command.save_salt_project");

        UIThreadRunnable.syncExec(() -> handlerService.executeHandler(cmdSave));

        assertFalse(projectManager.isDirty());

        savedProject = SaltUtil.loadCompleteSaltProject(URI.createFileURI(tmpDir.toString()));

        savedDoc = (SDocument) savedProject.getCorpusGraphs().get(0)
            .getNode(DOC1_SALT_ID);

        docDiff = SaltUtil.compare(doc1Graph).with(savedDoc.getDocumentGraph()).andFindDiffs();
        assertThat(docDiff, is(empty()));
      }
    }

  }

  @Test
  @Order(2)
  public void testSaveToInvalidLocation() {

    projectManager.open(exampleProjectUri);
    assertFalse(errorService.getLastException().isPresent());

    // Use an URI which can't be saved to
    UIThreadRunnable.syncExec(() -> projectManager.saveTo(URI.createURI("http://localhost"),
        bot.getDisplay().getActiveShell()));
    Optional<IStatus> lastException =
        UIThreadRunnable.syncExec(() -> errorService.getLastException());
    // Check the error has been recorded
    assertTrue(lastException.isPresent());
    if (lastException.isPresent()) {
      assertTrue(lastException.get().getException() instanceof InvocationTargetException);
    }
  }

  @Test
  @Order(3)
  public void testSaveToInterrupted()
      throws IOException, InvocationTargetException, InterruptedException {

    // Mock a progress monitor dialog that interrupts and spy on the project manager
    // to allow returning the mocked dialog instead of the real one.
    ProjectManager spyingManager = spy(projectManager);
    ProgressMonitorDialog dialog = mock(ProgressMonitorDialog.class);

    when(spyingManager.createProgressMonitorDialog(any())).thenReturn(dialog);
    // The mocked dialog should throw an exception when it is run
    doThrow(InterruptedException.class).when(dialog).run(anyBoolean(), anyBoolean(), any());
    spyingManager.open(exampleProjectUri);

    // Check no error has been set yet
    assertFalse(errorService.getLastException().isPresent());

    Path tmpDir = Files.createTempDirectory("hexatomic-project-manager-test");

    UIThreadRunnable.syncExec(() -> {
      // Call saveTo which should show an error
      spyingManager.saveTo(URI.createFileURI(tmpDir.toAbsolutePath().toString()),
          bot.getDisplay().getActiveShell());

    });
    Optional<IStatus> lastException =
        UIThreadRunnable.syncExec(() -> errorService.getLastException());
    // Check the error has been recorded
    assertTrue(lastException.isPresent());
    if (lastException.isPresent()) {
      assertTrue(lastException.get().getException() instanceof InterruptedException);
    }
  }
}

