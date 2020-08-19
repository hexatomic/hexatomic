package org.corpus_tools.hexatomic.it.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.Difference;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestProjectManager {

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ProjectManager projectManager;

  @BeforeEach
  void setup() {
    IEclipseContext ctx = TestHelper.getEclipseContext();

    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    // Programmatically start a new salt project to get a clean state
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.new_salt_project", params);
    handlerService.executeHandler(cmd);
  }

  @Test
  @Order(1)
  public void testOpenAndSave() throws IOException {

    projectManager.getProject().setName(null);
    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 0);

    // Open the example project
    projectManager.open(exampleProjectUri);

    assertEquals(projectManager.getProject().getCorpusGraphs().size(), 1);

    final String[] docIDs = {"salt:/rootCorpus/subCorpus1/doc1", "salt:/rootCorpus/subCorpus1/doc2",
        "salt:/rootCorpus/subCorpus2/doc3", "salt:/rootCorpus/subCorpus2/doc4"};

    for (String id : docIDs) {
      assertTrue(projectManager.getDocument(id).isPresent());
    }

    assertFalse(projectManager.isDirty());

    // Load a single document into memory
    Optional<SDocument> optionalDoc1 =
        projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(optionalDoc1.isPresent());
    if (optionalDoc1.isPresent()) {
      SDocument doc1 = optionalDoc1.get();
      SDocumentGraph doc1Graph = doc1.getDocumentGraph();
      assertNotNull(doc1Graph);

      // Apply some changes to the loaded document graph
      List<SToken> tokens = doc1Graph.getSortedTokenByText();
      doc1Graph.createSpan(tokens.get(0), tokens.get(1));

      assertTrue(projectManager.isDirty());

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
          .getNode("salt:/rootCorpus/subCorpus1/doc1");

      Set<Difference> docDiff =
          SaltUtil.compare(doc1Graph).with(savedDoc.getDocumentGraph()).andFindDiffs();
      assertThat(docDiff, is(empty()));

      // Apply some more changes to the loaded document graph and save to same
      // location
      optionalDoc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
      assertTrue(optionalDoc1.isPresent());
      if (optionalDoc1.isPresent()) {
        doc1 = optionalDoc1.get();
        doc1Graph = doc1.getDocumentGraph();
        assertNotNull(doc1Graph);
        tokens = doc1Graph.getSortedTokenByText();
        doc1Graph.createSpan(tokens.get(2), tokens.get(3));

        assertTrue(projectManager.isDirty());

        final ParameterizedCommand cmdSave = commandService
            .createCommand("org.corpus_tools.hexatomic.core.command.save_salt_project");

        UIThreadRunnable.syncExec(() -> {
          handlerService.executeHandler(cmdSave);
        });

        assertFalse(projectManager.isDirty());

        savedProject = SaltUtil.loadCompleteSaltProject(URI.createFileURI(tmpDir.toString()));

        savedDoc = (SDocument) savedProject.getCorpusGraphs().get(0)
            .getNode("salt:/rootCorpus/subCorpus1/doc1");

        docDiff = SaltUtil.compare(doc1Graph).with(savedDoc.getDocumentGraph()).andFindDiffs();
        assertThat(docDiff, is(empty()));
      }
    }

  }
}
