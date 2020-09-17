package org.corpus_tools.hexatomic.it.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.corpusedit.CorpusStructureView;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.eclipse.swtbot.swt.finder.waits.Conditions

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestCorpusStructure {


  private static final String SALT_PREFIX = "salt:/";

  private static final String DELETE_BUTTON_TEXT = "Delete";

  private static final String ABC = "abc";
  private static final String DEF = "def";

  private static final String DOCUMENT_1 = "document_1";
  private static final String DOCUMENT_2 = "document_2";
  private static final String CORPUS_1 = "corpus_1";
  private static final String CORPUS_GRAPH_1 = "corpus_graph_1";

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private ECommandService commandService;
  private EHandlerService handlerService;

  private ProjectManager projectManager;

  private final Keyboard keyboard = KeyboardFactory.getSWTKeyboard();

  @BeforeEach
  void setup() {
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
    partBot.tree().getTreeItem(CORPUS_GRAPH_1).select();

    // Add corpus 1
    partBot.toolbarDropDownButton(0).click();
    partBot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).select();

    // Add document_1
    partBot.toolbarDropDownButton(0).click();
    partBot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(0).select();
  }


  private void createExampleStructure() {
    createMinimalCorpusStructure(bot);

    // Add document_2
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(1).select();


    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(DOCUMENT_1);
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DOCUMENT_1).select();
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DOCUMENT_1).doubleClick();
    bot.text(DOCUMENT_1).setText(ABC).setFocus();
    keyboard.pressShortcut(Keystrokes.LF);

    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(DOCUMENT_2);
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DOCUMENT_2).select();
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DOCUMENT_2).doubleClick();
    bot.text(DOCUMENT_2).setText(DEF).setFocus();
    keyboard.pressShortcut(Keystrokes.LF);
  }

  @Test
  @Order(1)
  void testRenameDocument() {

    createExampleStructure();

    // make sure that the salt project has been renamed in UI
    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(ABC);
    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(DEF);
    assertNotNull(bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(ABC));
    assertNotNull(bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DEF));

    // also check the names and IDs of the data model
    Optional<SDocument> doc1 =
        projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_1);
    Optional<SDocument> doc2 =
        projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_2);
    assertTrue(doc1.isPresent());
    assertTrue(doc2.isPresent());

    if (doc1.isPresent() && doc2.isPresent()) {
      assertEquals(ABC, doc1.get().getName());
      assertEquals(DEF, doc2.get().getName());
    }
  }

  @Test
  @Order(2)
  void testFilter() {
    createExampleStructure();

    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(ABC);
    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(DEF);

    // The function before already added some documents, add two more
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DEF).select();
    bot.toolbarDropDownButton(0).click();
    bot.toolbarDropDownButton(0).click();


    List<String> children = bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNodes();
    assertEquals(4, children.size());

    bot.textWithId(SWTBotPreferences.DEFAULT_KEY, "filter").setText("_3");
    // only one document should be visible
    children = bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNodes();
    assertEquals(1, children.size());
    assertEquals("document_3", children.get(0));

    bot.textWithId(SWTBotPreferences.DEFAULT_KEY, "filter").setText("");
  }

  @Test
  @Order(3)
  void testDelete() {
    createExampleStructure();

    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(ABC);
    bot.tree().expandNode(CORPUS_GRAPH_1).expandNode(CORPUS_1).expandNode(DEF);

    // Delete the first document
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(ABC).select();
    bot.toolbarButton(DELETE_BUTTON_TEXT).click();
    assertFalse(projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_1).isPresent());
    assertTrue(projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_2).isPresent());

    // Test that we can't delete a corpus when there is still a sub-document
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).select();
    bot.toolbarButton(DELETE_BUTTON_TEXT).click();
    bot.waitUntil(
        Conditions.shellIsActive(CorpusStructureView.ERROR_WHEN_DELETING_SUB_CORPUS_TITLE));
    bot.button("OK").click();

    assertTrue(projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_2).isPresent());
    SaltProject project = projectManager.getProject();
    assertEquals(1, project.getCorpusGraphs().get(0).getCorpora().size());

    // Delete the second document
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).getNode(DEF).select();
    bot.toolbarButton(DELETE_BUTTON_TEXT).click();
    assertFalse(projectManager.getDocument(SALT_PREFIX + CORPUS_1 + "/" + DOCUMENT_2).isPresent());

    // Delete the corpus which should be successful now
    bot.tree().getTreeItem(CORPUS_GRAPH_1).getNode(CORPUS_1).select();
    bot.toolbarButton(DELETE_BUTTON_TEXT).click();
    assertTrue(project.getCorpusGraphs().get(0).getCorpora().isEmpty());

    // Delete the whole corpus graph
    bot.tree().getTreeItem(CORPUS_GRAPH_1).select();
    bot.toolbarButton(DELETE_BUTTON_TEXT).click();
    assertTrue(project.getCorpusGraphs().isEmpty());
  }
}
