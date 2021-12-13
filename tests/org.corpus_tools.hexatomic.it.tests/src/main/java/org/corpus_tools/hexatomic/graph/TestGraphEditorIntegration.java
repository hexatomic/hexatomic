package org.corpus_tools.hexatomic.graph;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.it.tests.PartMaximizedCondition;
import org.corpus_tools.hexatomic.it.tests.TestCorpusStructure;
import org.corpus_tools.hexatomic.it.tests.TestHelper;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.draw2d.ScalableFigure;
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
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.WidgetTextDescription;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestGraphEditorIntegration {

  private static final String CORPUS_EDITOR_PART_ID =
      "org.corpus_tools.hexatomic.corpusedit.part.corpusstructure";
  private static final String INCLUDE_SPANS = "Include spans";
  private static final String FILTER_BY_NODE_ANNOTATION_NAME = "Filter by node annotation name";
  private static final String TEST_TOKEN = "abc";
  private static final String ADD_POINTING_COMMMAND = "e #structure3 -> #structure5";
  private static final String ANOTHER_TEXT = "Another text";
  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());
  private static final String GET_VIEWPORT = "getViewport";

  private static final String GET_VIEW_LOCATION = "getViewLocation";

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ErrorService errorService;
  private ProjectManager projectManager;

  private final Keyboard keyboard = KeyboardFactory.getAWTKeyboard();

  private final class ConsoleFontSizeCondition implements ICondition {
    private final SWTBotStyledText console;
    private final int expectedSize;

    private ConsoleFontSizeCondition(int expectedSize, SWTBotStyledText console) {
      this.expectedSize = expectedSize;
      this.console = console;
    }

    @Override
    public boolean test() throws Exception {
      final AtomicInteger actualSize = new AtomicInteger();
      UIThreadRunnable
          .syncExec(() -> actualSize.set(console.widget.getFont().getFontData()[0].getHeight()));
      return actualSize.get() == expectedSize;
    }

    @Override
    public void init(SWTBot bot) {
      // No initialization needed
    }

    @Override
    public String getFailureMessage() {
      return "Console font size was not the expected size " + expectedSize;
    }
  }

  private final class CurrentConsoleLineCondition implements ICondition {
    private final SWTBotStyledText console;
    private final String expected;

    private CurrentConsoleLineCondition(String expected, SWTBotStyledText console) {
      this.console = console;
      this.expected = expected;
    }

    @Override
    public boolean test() throws Exception {
      return Objects.equals(expected, console.getTextOnCurrentLine());
    }

    @Override
    public void init(SWTBot bot) {
      // No initialization needed
    }

    @Override
    public String getFailureMessage() {
      return "Current line on console should have been \"" + expected + "\" but was \""
          + console.getTextOnCurrentLine() + "\"";
    }
  }

  private final class GraphLoadedCondition extends DefaultCondition {

    private final List<Integer> segmentIndexes;
    private final String documentName;

    public GraphLoadedCondition(Integer... segmentIndexes) {
      this.segmentIndexes = Arrays.asList(segmentIndexes);
      this.documentName = "doc1";
    }

    public GraphLoadedCondition(String documentName, Integer... segmentIndexes) {
      this.segmentIndexes = Arrays.asList(segmentIndexes);
      this.documentName = documentName;
    }

    @Override
    public boolean test() throws Exception {
      SWTBotView view =
          TestGraphEditorIntegration.this.bot.partByTitle(this.documentName + " (Graph Editor)");
      if (view != null) {
        SWTBotTable textRangeTable = bot.tableWithId(GraphEditor.TEXT_RANGE_ID);
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
      return "Showing the expected number of " + expected + " nodes took too long";
    }
  }

  private final class HasNodeWithText extends DefaultCondition {

    private final String expectedText;

    public HasNodeWithText(String expectedText) {
      this.expectedText = expectedText;
    }

    @Override
    public boolean test() throws Exception {
      AtomicBoolean found = new AtomicBoolean(false);

      Graph g = bot.widget(widgetOfType(Graph.class));
      if (g != null) {
        bot.getDisplay().syncExec(() -> {
          List<?> nodes = g.getNodes();

          for (Object o : nodes) {
            if (o instanceof GraphNode) {
              GraphNode n = (GraphNode) o;
              if (Objects.equals(this.expectedText, n.getText().strip())) {
                found.set(true);
              }
            }
          }
        });
      }
      return found.get();
    }

    @Override
    public String getFailureMessage() {
      return "Could not find any node with the text '" + this.expectedText + "'";
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

    EPartService partService = ctx.get(EPartService.class);
    assertNotNull(partService);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    errorService.clearLastException();

    TestHelper.executeNewProjectCommand(commandService, handlerService);
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
    SWTBotView corpusStructurePart = bot.partById(CORPUS_EDITOR_PART_ID);
    corpusStructurePart.restore();
    corpusStructurePart.show();

    // Select the first example document
    SWTBotTreeItem docMenu = corpusStructurePart.bot().tree().expandNode("corpusGraph1")
        .expandNode("rootCorpus").expandNode("subCorpus1").expandNode("doc1");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Graph Editor").click());

    bot.waitUntil(new GraphLoadedCondition(0));
  }

  void openMinimalProjectStructure() {
    TestCorpusStructure.createMinimalCorpusStructure(bot);

    // Select the first example document
    SWTBotTreeItem docMenu = bot.partById(CORPUS_EDITOR_PART_ID).bot().tree()
        .expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("document_1");

    // Select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Graph Editor").click());

    bot.waitUntil(new GraphLoadedCondition("document_1"));
  }

  void enterCommand(String command) {
    SWTBotStyledText console = bot.styledTextWithId(GraphEditor.CONSOLE_ID);
    final SWTBotTable textRangeTable = bot.tableWithId(GraphEditor.TEXT_RANGE_ID);

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
    Keyboard mockKeyboadForGraph = KeyboardFactory.getMockKeyboard(g, new WidgetTextDescription(g));
    KeyStroke[] strokesZoomIn = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_ADD)};
    mockKeyboadForGraph.pressShortcut(strokesZoomIn);

    origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);

    // Scroll with arrow keys (left, right, up, down) and check that that view has been moved
    keyboard.pressShortcut(Keystrokes.RIGHT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x + 25, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.LEFT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
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
    bot.waitUntil(new ViewLocationReachedCondition(viewPort,
        new Point(origLocation.x + 250, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.LEFT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)));
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.DOWN);
    bot.waitUntil(new ViewLocationReachedCondition(viewPort,
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
  void testAddPointingRelation() {

    openDefaultExample();

    // Get a reference to the open graph
    Optional<SDocument> optionalDocument =
        projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1");
    assertTrue(optionalDocument.isPresent());
    if (optionalDocument.isPresent()) {
      SDocument doc = optionalDocument.get();
      SDocumentGraph graph = doc.getDocumentGraph();

      // Before state: no edges between the two structures
      assertEquals(0, graph.getRelations("salt:/rootCorpus/subCorpus1/doc1#structure3",
          "salt:/rootCorpus/subCorpus1/doc1#structure5").size());

      // Add a pointing relation between the two structures
      enterCommand(ADD_POINTING_COMMMAND);

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
  }

  /**
   * Tests if the "t" command adds the new tokens to the currently selected textual datasource. This
   * is a regression test for https://github.com/hexatomic/hexatomic/issues/139.
   */
  @Test
  void testTokenizeSelectedTextualDS() {

    openDefaultExample();


    // Get a reference to the opened document graph
    Optional<SDocument> optionalDoc =
        projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1");
    assertTrue(optionalDoc.isPresent());
    if (optionalDoc.isPresent()) {
      SDocument doc = optionalDoc.get();
      SDocumentGraph graph = doc.getDocumentGraph();

      STextualDS firstText = graph.getTextualDSs().get(0);
      final String originalText = firstText.getText();

      // Add an additional data source to the document graph
      STextualDS anotherText = graph.createTextualDS(ANOTHER_TEXT);
      graph.createToken(anotherText, 0, 7);
      graph.createToken(anotherText, 8, 12);

      // Add a checkpoint so the changes are propagated to the view
      projectManager.addCheckpoint();

      // Select the new text
      SWTBotTable textRangeTable = bot.tableWithId(GraphEditor.TEXT_RANGE_ID);
      textRangeTable.select(ANOTHER_TEXT);

      // Wait until the graph has been properly selected
      bot.waitUntil(new DefaultCondition() {

        @Override
        public boolean test() throws Exception {
          return textRangeTable.getTableItem(ANOTHER_TEXT).isChecked();
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
  }

  @Test
  void testFilterOptions() {
    openDefaultExample();


    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    // Add a pointing relation between the two structures
    enterCommand(ADD_POINTING_COMMMAND);

    // Pointing relations are shown initially and the new one should be visible now
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Deactivate/activate pointing relations in view and check the view has less/more connections
    SWTBotCheckBox includePointing = bot.checkBox("Include pointing relations");
    includePointing.deselect();
    bot.waitUntil(new NumberOfConnectionsCondition(22));
    includePointing.select();
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Deactivate/activate spans in view and check the view has less/more nodes
    SWTBotCheckBox includeSpans = bot.checkBox(INCLUDE_SPANS);
    includeSpans.deselect();
    bot.waitUntil(new NumberOfNodesCondition(23));
    includeSpans.select();
    bot.waitUntil(new NumberOfNodesCondition(26));
    // With spans enabled, filter for annotation names
    SWTBotText annoFilter = bot.textWithMessage(FILTER_BY_NODE_ANNOTATION_NAME);

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
  void testUndoRedoRendered() {
    openDefaultExample();

    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    bot.waitUntil(new NumberOfConnectionsCondition(22));

    // Add a pointing relation between the two structures
    enterCommand(ADD_POINTING_COMMMAND);

    // Pointing relations are shown initially and the new one should be visible now
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Undo/Redo the changes and check that the view has been updated
    bot.menu("Undo").click();
    bot.waitUntil(new NumberOfConnectionsCondition(22));

    bot.menu("Redo").click();
    bot.waitUntil(new NumberOfConnectionsCondition(23));
  }

  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/220
   * 
   * @throws IOException When the file is not a valid URI, this exception can be thrown.
   */
  @Test
  void testTokenizeSaveTokenizeSave() throws IOException {
    openMinimalProjectStructure();

    // Add the two tokens to the document graph
    enterCommand("t Oi!");

    // Save the corpus to a temporary location
    Path tmpDir = Files.createTempDirectory("hexatomic-regression-test-220");

    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, tmpDir.toString());
    final ParameterizedCommand cmdSaveAs = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.save_as_salt_project", params);

    UIThreadRunnable.syncExec(() -> handlerService.executeHandler(cmdSaveAs));

    // Add two additional tokens
    enterCommand("t Oioi!");

    // Save to original location
    params.put(CommandParams.LOCATION, tmpDir.toString());
    final ParameterizedCommand cmdSave = commandService.createCommand(
        "org.corpus_tools.hexatomic.core.command.save_salt_project", new HashMap<>());

    UIThreadRunnable.syncExec(() -> handlerService.executeHandler(cmdSave));

    // The last save should not have triggered any errors
    assertFalse(errorService.getLastException().isPresent());
  }

  @Test
  void testHistory() {
    openDefaultExample();

    enterCommand("c1");
    enterCommand("c2");
    enterCommand("c3");

    // Use arrow up key to navigate to the previous command
    SWTBotStyledText console = bot.styledTextWithId(GraphEditor.CONSOLE_ID);
    console.setFocus();

    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(new CurrentConsoleLineCondition("> c3", console));
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(new CurrentConsoleLineCondition("> c2", console));
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(new CurrentConsoleLineCondition("> c1", console));

    // Go forward in history again
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(new CurrentConsoleLineCondition("> c2", console));
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(new CurrentConsoleLineCondition("> c3", console));

    // Go back again, just to make sure the user does not need to click the arrow key twice
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(new CurrentConsoleLineCondition("> c2", console));
  }

  @Test
  void testConsoleTextSize() {
    openDefaultExample();

    SWTBotStyledText console = bot.styledTextWithId(GraphEditor.CONSOLE_ID);

    final AtomicInteger initialSize = new AtomicInteger(12);
    // We can only access the widget in the SWT thread
    UIThreadRunnable
        .syncExec(() -> initialSize.set(console.widget.getFont().getFontData()[0].getHeight()));
    Keyboard mockKeyboadForGraph =
        KeyboardFactory.getMockKeyboard(console.widget, new WidgetTextDescription(console.widget));

    console.setFocus();
    KeyStroke[] strokesZoomIn = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_ADD)};
    mockKeyboadForGraph.pressShortcut(strokesZoomIn);
    bot.waitUntil(new ConsoleFontSizeCondition(initialSize.get() + 1, console));

    KeyStroke[] strokesZoomOut = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_SUBTRACT)};
    mockKeyboadForGraph.pressShortcut(strokesZoomOut);
    bot.waitUntil(new ConsoleFontSizeCondition(initialSize.get(), console));

    mockKeyboadForGraph.pressShortcut(strokesZoomOut);
    bot.waitUntil(new ConsoleFontSizeCondition(initialSize.get() - 1, console));
  }

  @Test
  void testShowNewlyCreatedSpan() {
    openDefaultExample();

    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    // Deactivate/activate spans in view and check the view has less/more nodes
    SWTBotCheckBox includeSpans = bot.checkBox(INCLUDE_SPANS);
    includeSpans.deselect();
    bot.waitUntil(new NumberOfNodesCondition(23));
    includeSpans.select();
    bot.waitUntil(new NumberOfNodesCondition(26));

    // Add some span
    enterCommand("s spanno:test #sTok1 #sTok2");

    // Check that an extra node (the span) has been created and is visible
    bot.waitUntil(new NumberOfNodesCondition(27));
  }

  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/224
   * 
   * <p>
   * It checks that if you create new token in an empty document, that the zoom level is correct and
   * the tokens are actually shown.
   * </p>
   */
  @Test
  void testShowNewlyCreatedToken() {
    openMinimalProjectStructure();

    // Add some token
    enterCommand("t This is an example.");

    // Make sure the token node boxes are large enough to be visible
    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);
    bot.waitUntil(new NumberOfNodesCondition(5));

    ScalableFigure figure = g.getRootLayer();
    assertEquals(1.0, figure.getScale());
  }


  /**
   * Tests that the segmentation list is updated when a token is deleted. Regression test for
   * https://github.com/hexatomic/hexatomic/issues/261
   */
  @Test
  void testUpdateSegmentsOnDeletedToken() {
    openDefaultExample();

    SWTBotTable textRangeTable = bot.tableWithId(GraphEditor.TEXT_RANGE_ID);

    enterCommand("t abc");
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return textRangeTable.containsItem(TEST_TOKEN);
      }

      @Override
      public String getFailureMessage() {
        return "Segment for new token was not shown";
      }
    }, 5000);

    enterCommand("d #t12");
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return !textRangeTable.containsItem(TEST_TOKEN);
      }

      @Override
      public String getFailureMessage() {
        return "Segmentation for deleted token was not removed";
      }
    }, 5000);
  }

  /**
   * Tests that the view is updated when another editor changes the annotation value.
   * 
   * <p>
   * Regression test for https://github.com/hexatomic/hexatomic/issues/362
   * </p>
   */
  @Test
  void testUpdateAnnosOnExternalChange() {
    openDefaultExample();


    SWTBotView graphPart = bot.partByTitle("doc1 (Graph Editor)");
    final GraphEditor graphEditor = spy((GraphEditor) graphPart.getPart().getObject());

    // Make sure the relevant spans are shown
    bot.checkBox(INCLUDE_SPANS).select();
    SWTBotText annoFilter = bot.textWithMessage(FILTER_BY_NODE_ANNOTATION_NAME);
    annoFilter.setText("Inf");

    bot.waitUntil(new HasNodeWithText("Inf-Struct=contrast-focus"));

    // Open the same document in the grid editor
    SWTBotView corpusStructurePart = bot.partById(CORPUS_EDITOR_PART_ID);
    SWTBotTreeItem docMenu = corpusStructurePart.bot().tree().expandNode("corpusGraph1")
        .expandNode("rootCorpus").expandNode("subCorpus1").expandNode("doc1");
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Grid Editor").click());

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(1, 4);
    keyboard.typeText("anothertest");
    keyboard.pressShortcut(Keystrokes.CR);

    // Close the Grid editor, which selects the Graph Editor again and
    // wait for the annotation value to change
    TestGraphEditorIntegration.this.bot.partByTitle("doc1 (Grid Editor)").close();

    bot.waitUntil(new HasNodeWithText("Inf-Struct=anothertest"));


    // Open a grid editor on another document and check updates to it are ignored
    docMenu = corpusStructurePart.bot().tree().expandNode("corpusGraph1").expandNode("rootCorpus")
        .expandNode("subCorpus1").expandNode("doc2");
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Grid Editor").click());
    tableBot = new SWTNatTableBot();
    table = tableBot.nattable();
    table.click(1, 4);
    keyboard.typeText("abc");
    keyboard.pressShortcut(Keystrokes.CR);
    TestGraphEditorIntegration.this.bot.partByTitle("doc2 (Grid Editor)").close();
    bot.waitUntil(new GraphLoadedCondition());

    verify(graphEditor, Mockito.never()).updateView(anyBoolean(), anyBoolean());
  }
}
