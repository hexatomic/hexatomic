package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.corpus_tools.hexatomic.core.handlers.OpenSaltProjectHandler;
import org.corpus_tools.hexatomic.graph.GraphEditor;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.e4.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class TestGraphEditor {

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  @BeforeEach
  void setup() {
    IEclipseContext ctx = ContextHelper.getEclipseContext();

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

  }

  @Test
  @Order(1)
  void testShowSaltExample() {

    // Programatically open the example corpus
    Map<String, String> params = new HashMap<>();
    params.put(OpenSaltProjectHandler.COMMAND_PARAM_LOCATION_ID, exampleProjectUri.toFileString());
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.open_salt_project", params);
    handlerService.executeHandler(cmd);

    // Activate corpus structure editor
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();

    // Select the first example document
    SWTBotTreeItem docMenu = bot.tree().expandNode("corpusGraph1").expandNode("rootCorpus")
        .expandNode("subCorpus1").expandNode("doc1");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Graph Editor").click());

    SWTBotView view = bot.partByTitle("doc1 (Graph Editor)");
    assertNotNull(view);

  }
}
