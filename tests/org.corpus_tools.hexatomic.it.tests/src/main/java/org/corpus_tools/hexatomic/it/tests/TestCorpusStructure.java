package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestCorpusStructure {

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  private ECommandService commandService;
  private EHandlerService handlerService;


  @BeforeEach
  void setup() {

    IEclipseContext ctx = ContextHelper.getEclipseContext();

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    // Programmatically close the salt project to get a clean state
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.close_salt_project", params);
    handlerService.executeHandler(cmd);

    // Make sure to activate the part to test before selecting SWT components
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();
  }


  private void createExampleStructure() {
    // Add corpus graph 1 by clicking on the first toolbar button ("Add") in the corpus structure
    // editor part
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem("corpus_graph_1").select();

    // Add corpus 1
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").select();

    // Add document_1
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode(0).select();

    // Add document_2
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode(1).select();


    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("document_1");
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1").select();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1")
        .doubleClick();
    bot.text("document_1").setText("abc").pressShortcut(Keystrokes.LF);

    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("document_2");
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_2").select();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_2")
        .doubleClick();
    bot.text("document_2").setText("def").pressShortcut(Keystrokes.LF);
  }

  @Test
  @Order(1)
  void testRenameDocument() {

    createExampleStructure();

    // make sure that the salt project has been renamed
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("abc");
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("def");
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("abc"));
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("def"));
  }

  @Test
  @Order(2)
  void testFilter() {
    createExampleStructure();

    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("abc");
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("def");

    // The function before already added some documents, add two more
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("def").select();
    bot.toolbarDropDownButton(0).click();
    bot.toolbarDropDownButton(0).click();


    List<String> children = bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNodes();
    assertEquals(4, children.size());

    bot.textWithId(SWTBotPreferences.DEFAULT_KEY, "filter").setText("_3");
    // only one document should be visible
    children = bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNodes();
    assertEquals(1, children.size());
    assertEquals("document_3", children.get(0));

    bot.textWithId(SWTBotPreferences.DEFAULT_KEY, "filter").setText("");

  }
}
