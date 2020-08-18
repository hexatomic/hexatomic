package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.WidgetTextDescription;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
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

  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());
  private static final String GET_VIEWPORT = "getViewport";

  private static final String GET_VIEW_LOCATION = "getViewLocation";

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;
  private EPartService partService;

  private ErrorService errorService;
  private ProjectManager projectManager;

  private final Keyboard keyboard = KeyboardFactory.getAWTKeyboard();

  private final class GraphLoadedCondition extends DefaultCondition {

    private final List<Integer> segmentIndexes;

    public GraphLoadedCondition(Integer... segmentIndexes) {
      this.segmentIndexes = Arrays.asList(segmentIndexes);
    }

    @Override
    public boolean test() throws Exception {
      SWTBotView view = TestGraphEditor.this.bot.partByTitle("doc1 (Graph Editor)");
      if (view != null) {
        SWTBotTable textRangeTable = bot.tableWithId("graph-editor/text-range");
        // Wait until the graph has been loaded
        for (int i : segmentIndexes) {
          if (!textRangeTable.getTableItem(i).isChecked()) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    @Override
    public String getFailureMessage() {
      return "Showing the graph editor part took too long";
    }
  }

  private final class NumberOfConnectionsCondition extends DefaultCondition {

    private final int expected;

    public NumberOfConnectionsCondition(int expected) {
      this.expected = expected;
    }

    @Override
    public boolean test() throws Exception {
      Graph g = bot.widget(widgetOfType(Graph.class));
      if (g != null) {
        return g.getConnections().size() == expected;
      }
      return false;
    }

    @Override
    public String getFailureMessage() {
      return "Showing the expected number of " + expected + " connections took too long";
    }
  }

  private final class NumberOfNodesCondition extends DefaultCondition {

    private final int expected;

    public NumberOfNodesCondition(int expected) {
      this.expected = expected;
    }

    @Override
    public boolean test() throws Exception {
      Graph g = bot.widget(widgetOfType(Graph.class));
      if (g != null) {
        return g.getNodes().size() == expected;
      }
      return false;
    }

    @Override
    public String getFailureMessage() {
      return "Showing the expected number of " + expected + " connections took too long";
    }
  }

  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();

    IEclipseContext ctx = TestHelper.getEclipseContext();

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
    params.put(CommandParams.FORCE_CLOSE, "true");
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

    bot.waitUntil(new GraphLoadedCondition(0));
  }

  void enterCommand(String command) {
    SWTBotStyledText console = bot.styledTextWithId("graph-editor/text-console");
    final SWTBotTable textRangeTable = bot.tableWithId("graph-editor/text-range");

    // Remember the index of the currently selected segment
    Optional<Integer> firstSelectedRow = Optional.empty();
    for (int i = 0; i < textRangeTable.rowCount(); i++) {
      if (textRangeTable.getTableItem(i).isChecked()) {
        firstSelectedRow = Optional.of(i);
        break;
      }
    }

    // We can't use typeText() here, because it would generate upper case characters
    console.insertText(command);
    // Make sure the cursor is at the end of the line
    console.navigateTo(console.getLineCount() - 1, command.length() + 2);
    // Finish with typing the return character
    console.typeText("\n");

    // Wait until the graph has been rendered
    if (firstSelectedRow.isPresent()) {
      int row = firstSelectedRow.get();
      bot.waitUntil(new DefaultCondition() {

        @Override
        public boolean test() throws Exception {
          return textRangeTable.getTableItem(row).isChecked();
        }

        @Override
        public String getFailureMessage() {
          return "Second text segment was not checked";
        }
      }, 5000);
    }
  }

  @Test
  @Order(1)
  void testShowSaltExample()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    // Open example and maximize part
    openDefaultExample();

    SWTBotView view = bot.partByTitle("doc1 (Graph Editor)");
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    // Check all nodes and edges have been created
    bot.waitUntil(new NumberOfNodesCondition(23));
    bot.waitUntil(new NumberOfConnectionsCondition(22));
    
    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);
    SWTUtils.display().syncExec(g::forceFocus);

    final Viewport viewPort = (Viewport) SWTUtils.invokeMethod(g, GET_VIEWPORT);
    
    Point origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);

    // Initially, the zoom is adjusted to match the height, so moving up/down should not do anything
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));

    // Zoom in so we can move the view with the arrow keys
    // Use the mock keyboard for this since AWT/SWT keyboards don't map the keypad keys yet.
    Keyboard mockKeyboadForGraph =
        KeyboardFactory.getMockKeyboard(g, new WidgetTextDescription(g));
    KeyStroke[] strokesZoomIn = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_ADD)};
    mockKeyboadForGraph.pressShortcut(strokesZoomIn);

    origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);

    // Scroll with arrow keys (left, right, up, down) and check that that view has been moved
    keyboard.pressShortcut(Keystrokes.RIGHT);
    bot.waitUntil(new ViewLocationReachedCondition(viewPort,
        new Point(origLocation.x + 25, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.LEFT);
    bot.waitUntil(new ViewLocationReachedCondition(viewPort,
        new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 25)));
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.PAGE_DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 25)));
    keyboard.pressShortcut(Keystrokes.PAGE_UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));

    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.RIGHT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort,
            new Point(origLocation.x + 250, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.LEFT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort,
            new Point(origLocation.x, origLocation.y + 250)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.PAGE_DOWN);
    bot.waitUntil(new ViewLocationReachedCondition(viewPort,
        new Point(origLocation.x, origLocation.y + 250)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.PAGE_UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));

    // Zoom out again: moving up should not have any effect again
    KeyStroke[] strokesZoomOut = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_SUBTRACT)};
    mockKeyboadForGraph.pressShortcut(strokesZoomOut);
    origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
  }

  @Test
  @Order(2)
  void testAddPointingRelation() {

    openDefaultExample();

    // Get a reference to the open graph
    SDocument doc = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").get();
    SDocumentGraph graph = doc.getDocumentGraph();

    // Before state: no edges between the two structures
    assertEquals(0, graph.getRelations("salt:/rootCorpus/subCorpus1/doc1#structure3",
        "salt:/rootCorpus/subCorpus1/doc1#structure5").size());

    // Add a pointing relation between the two structures
    enterCommand("e #structure3 -> #structure5");

    // Check that no exception was thrown/handled by UI
    assertFalse(errorService.getLastException().isPresent());

    // Check that the edge has been added to the graph
    List<?> rels = graph.getRelations("salt:/rootCorpus/subCorpus1/doc1#structure3",
        "salt:/rootCorpus/subCorpus1/doc1#structure5");
    assertEquals(1, rels.size());
    assertTrue(rels.get(0) instanceof SPointingRelation);

    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    // Check that the edge was also added as connection in the view
    bot.waitUntil(new NumberOfConnectionsCondition(23));
  }

  /**
   * Tests if the "t" command adds the new tokens to the currently selected textual datasource. This
   * is a regression test for https://github.com/hexatomic/hexatomic/issues/139.
   */
  @Test
  @Order(3)
  void testTokenizeSelectedTextualDS() {

    openDefaultExample();

    // Get a reference to the opened document graph
    SDocument doc = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").get();
    SDocumentGraph graph = doc.getDocumentGraph();

    STextualDS firstText = graph.getTextualDSs().get(0);
    final String originalText = firstText.getText();

    // Add an additional data source to the document graph
    STextualDS anotherText = graph.createTextualDS("Another text");
    graph.createToken(anotherText, 0, 7);
    graph.createToken(anotherText, 8, 12);

    // Add a checkpoint so the changes are propagated to the view
    projectManager.addCheckpoint();

    // Select the new text
    SWTBotTable textRangeTable = bot.tableWithId("graph-editor/text-range");
    textRangeTable.select("Another text");

    // Wait until the graph has been properly selected
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return textRangeTable.getTableItem("Another text").isChecked();
      }

      @Override
      public String getFailureMessage() {
        return "Second text segment was not checked";
      }
    }, 5000);

    // Add a new tokenized text to the end
    enterCommand("t has more tokens");

    // Check that the right textual data source has been amended
    assertEquals("Another text has more tokens", anotherText.getText());

    // Check the original text has not been altered and is displayed correctly
    assertEquals(originalText, firstText.getText());
    assertEquals(originalText, textRangeTable.cell(0, 0));

  }

  @Test
  @Order(4)
  void testFilterOptions() {
    openDefaultExample();


    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    // Add a pointing relation between the two structures
    enterCommand("e #structure3 -> #structure5");

    // Pointing relations are shown initially and the new one should be visible now
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Deactivate/activate pointing relations in view and check the view has less/more connections
    SWTBotCheckBox includePointing = bot.checkBox("Include pointing relations");
    includePointing.deselect();
    bot.waitUntil(new NumberOfConnectionsCondition(22));
    includePointing.select();
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Deactivate/activate spans in view and check the view has less/more nodes
    SWTBotCheckBox includeSpans = bot.checkBox("Include spans");
    includeSpans.deselect();
    bot.waitUntil(new NumberOfNodesCondition(23));
    includeSpans.select();
    bot.waitUntil(new NumberOfNodesCondition(26));
    // With spans enabled, filter for annotation names
    SWTBotText annoFilter = bot.textWithMessage("Filter by node annotation name");

    // Tokens and the matching structure nodes
    annoFilter.setText("const");
    bot.waitUntil(new NumberOfNodesCondition(23));

    // Tokens and the matching spans
    annoFilter.setText("Inf-Struct");
    bot.waitUntil(new NumberOfNodesCondition(13));

    // Only tokens because annotation is non-existing
    annoFilter.setText("not actually there");
    bot.waitUntil(new NumberOfNodesCondition(11));
  }


  @Test
  @Order(5)
  void testUndoRedoRendered() {
    openDefaultExample();
    
    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);
    
    bot.waitUntil(new NumberOfConnectionsCondition(22));

    // Add a pointing relation between the two structures
    enterCommand("e #structure3 -> #structure5");

    // Pointing relations are shown initially and the new one should be visible now
    bot.waitUntil(new NumberOfConnectionsCondition(23));
    
    // Undo/Redo the changes and check that the view has been updated
    bot.menu("Undo").click();
    bot.waitUntil(new NumberOfConnectionsCondition(22));

    bot.menu("Redo").click();
    bot.waitUntil(new NumberOfConnectionsCondition(23));


  }

}
