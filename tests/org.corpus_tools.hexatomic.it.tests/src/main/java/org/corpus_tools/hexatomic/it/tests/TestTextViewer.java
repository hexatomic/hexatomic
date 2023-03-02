package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class TestTextViewer {

  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ProjectManager projectManager;


  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();
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
  }

  @AfterEach
  void close() {
    // Programmatically close the example corpus by opening a new fresh one
    TestHelper.executeNewProjectCommand(commandService, handlerService);
  }


  SWTBotView openDefaultExample() {
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, exampleProjectUri.toFileString());
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.open_salt_project", params);
    handlerService.executeHandler(cmd);

    // Activate corpus structure editor
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();

    // Select the first example document
    SWTBotTreeItem docMenu = bot.tree().expandNode("corpusGraph1").expandNode("rootCorpus")
        .expandNode("subCorpus1").expandNode("doc2");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Text Viewer").click());

    SWTBotView view = bot.partByTitle("doc2 (Text Viewer)");
    assertNotNull(view);

    // Use all available windows space (the tableToTest needs to be fully visible for some of the
    // tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }



  @Test
  void testShowSaltExample() {

    // Open example and maximize part
    openDefaultExample();


    // Compare the shown text to the salt model
    assertEquals("Is this example more complicated than it appears to be?", bot.text().getText());

    // Change text and check its updated
    Optional<SDocument> document = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc2");
    assertTrue(document.isPresent());
    if (document.isPresent()) {
      SDocumentGraph docGraph = document.get().getDocumentGraph();
      assertNotNull(docGraph);

      docGraph.insertTokenAt(docGraph.getTextualDSs().get(0), 0, "Test", true);
      projectManager.addCheckpoint();

      assertEquals("Test Is this example more complicated than it appears to be?",
          bot.text().getText());
    }
  }

}
