package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.zest.core.widgets.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestGraphEditor {

  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;
  private EPartService partService;

  private ErrorService errorService;
  private ProjectManager projectManager;

  @BeforeEach
  void setup() {
    org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.KEYBOARD_STRATEGY =
        "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy";

    IEclipseContext ctx = ContextHelper.getEclipseContext();

    errorService = ContextInjectionFactory.make(ErrorService.class, ctx);
    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);


    partService = ctx.get(EPartService.class);
    assertNotNull(partService);

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


  void openDefaultExample() {

    // Programmatically open the example corpus
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, exampleProjectUri.toFileString());
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

    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        SWTBotView view = TestGraphEditor.this.bot.partByTitle("doc1 (Graph Editor)");
        return view != null;
      }

      @Override
      public String getFailureMessage() {
        return "Showing the graph editor part took too long";
      }
    });

  }

  @Test
  @Order(1)
  void testShowSaltExample() {

    openDefaultExample();

    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    // Check all nodes and edges have been created
    assertEquals(23, g.getNodes().size());
    assertEquals(22, g.getConnections().size());
  }

  @Test
  @Order(2)
  void testAddPointingRelation() {

    openDefaultExample();

    SWTBotStyledText console = bot.styledTextWithId("graph-editor/text-console");
    console.insertText("e #structure3 -> #structure5");
    console.typeText("\n");

    // Check that no exception was thrown/handled by UI
    assertFalse(errorService.getLastException().isPresent());
  }
  
  /**
   * Tests if the "t" command adds the new tokens to the currently selected textual datasource.
   * This is a regression test for https://github.com/hexatomic/hexatomic/issues/139.
   */
  @Test
  @Order(3)
  void testTokenizeSelectedTextualDS() {
    
    openDefaultExample();
    
    // Get a reference to the opened document graph
    SDocument doc = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").get();
    SDocumentGraph graph = doc.getDocumentGraph();
    
    // Add an additional data source to the document graph
    STextualDS anotherText = graph.createTextualDS("");
    graph.insertTokensAt(anotherText, 0, Arrays.asList("Another", "text"), true);
    
    
    // Select the new text
    SWTBotTable textRangeTable = bot.tableWithId("graph-editor/text-range");
    textRangeTable.select("Another text");
    
    // Add a  new tokenized text to the end
    SWTBotStyledText console = bot.styledTextWithId("graph-editor/text-console");
    console.insertText("t has more tokens");
    console.typeText("\n");
    
    // Check that the right textual data source has been amended
    assertEquals("Another text has more tokens", anotherText.getText());
    
  }


}
