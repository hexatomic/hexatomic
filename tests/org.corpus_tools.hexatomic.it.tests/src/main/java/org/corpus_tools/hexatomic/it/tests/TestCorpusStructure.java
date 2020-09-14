package org.corpus_tools.hexatomic.it.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
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

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private ECommandService commandService;
  private EHandlerService handlerService;

  private ProjectManager projectManager;


  @BeforeEach
  void setup() {
    org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.KEYBOARD_STRATEGY =
        "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy";
    TestHelper.setKeyboardLayout();

    IEclipseContext ctx = TestHelper.getEclipseContext();

    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    TestHelper.executeNewProjectCommand(commandService, handlerService);

    // Make sure to activate the part to test before selecting SWT components
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();
  }

  /**
   * Create a minimal corpus structure with one corpus graph, a single corpus and a document.
   * 
   * @param bot The SWT bot used to perform the UI interactions.
   */
  protected static void createMinimalCorpusStructure(SWTWorkbenchBot bot) {
    // Activate corpus structure editor
    SWTBotView corpusStructurePart =
        bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure");
    corpusStructurePart.restore();
    corpusStructurePart.show();

    SWTBot partBot = corpusStructurePart.bot();

    // Add corpus graph 1 by clicking on the first toolbar button ("Add") in the corpus structure
    // editor part
    partBot.toolbarDropDownButton(0).click();
    partBot.tree().getTreeItem("corpus_graph_1").select();

    // Add corpus 1
    partBot.toolbarDropDownButton(0).click();
    partBot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").select();

    // Add document_1
    partBot.toolbarDropDownButton(0).click();
    partBot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode(0).select();
  }


  private void createExampleStructure() {
    createMinimalCorpusStructure(bot);

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

    // make sure that the salt project has been renamed in UI
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("abc");
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("def");
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("abc"));
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("def"));

    // also check the names and IDs of the data model
    Optional<SDocument> doc1 = projectManager.getDocument("salt:/corpus_1/document_1");
    Optional<SDocument> doc2 = projectManager.getDocument("salt:/corpus_1/document_2");
    assertTrue(doc1.isPresent());
    assertTrue(doc2.isPresent());

    if (doc1.isPresent() && doc2.isPresent()) {
      assertEquals("abc", doc1.get().getName());
      assertEquals("def", doc2.get().getName());
    }
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
