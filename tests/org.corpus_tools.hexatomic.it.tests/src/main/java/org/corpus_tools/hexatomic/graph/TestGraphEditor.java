package org.corpus_tools.hexatomic.graph;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.SystemUtils;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.UiStatusReport;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.it.tests.PartMaximizedCondition;
import org.corpus_tools.hexatomic.it.tests.TableCellEditorInactiveCondition;
import org.corpus_tools.hexatomic.it.tests.TestCorpusStructure;
import org.corpus_tools.hexatomic.it.tests.TestHelper;
import org.corpus_tools.hexatomic.it.tests.utils.SwtBotChips;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.chips.Chips;
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
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.WidgetTextDescription;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotExpandBar;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotScale;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@TestMethodOrder(OrderAnnotation.class)
class TestGraphEditor {

  private static final String STRUCTURE5_ID = "salt:/rootCorpus/subCorpus1/doc1#structure5";
  private static final String STRUCTURE3_ID = "salt:/rootCorpus/subCorpus1/doc1#structure3";
  private static final String DOC1_SALT_ID = "salt:/rootCorpus/subCorpus1/doc1";
  private static final String DOC1_TITLE = "doc1 (Graph Editor)";
  private static final String CONST = "const";
  private static final String SEARCH = "Search";
  private static final String ANNOTATION_NAME = "Node Annotations";
  private static final String SPANS = "Spans";
  private static final String FILTER_VIEW = "Filter View";
  private static final String GRAPH_LAYOUT = "Graph Layout";
  private static final String ANNOTATION_TYPES = "Annotation Types";
  private static final String CORPUS_EDITOR_PART_ID =
      "org.corpus_tools.hexatomic.corpusedit.part.corpusstructure";
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
  private UiStatusReport uiStatus;

  private final Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
  private final Keyboard awtKeyboard = KeyboardFactory.getAWTKeyboard();

  private final class VisibleChipsCondition extends DefaultCondition {
    private final int expected;

    public VisibleChipsCondition(int expected) {
      super();
      this.expected = expected;
    }

    @Override
    public boolean test() throws Exception {
      return getVisibleChips(bot).size() == expected;
    }

    @Override
    public String getFailureMessage() {
      return MessageFormat.format(
          "Number of annotation filter facets should have been <{0}> but was <{1}>", expected,
          getVisibleChips(bot).size());
    }
  }

  private final class HorizontalNodeDistanceCondition extends DefaultCondition {
    private final SNode leftNode;
    private final SNode rightNode;
    private final double expected;
    private final Graph graphWidget;

    private HorizontalNodeDistanceCondition(SNode leftNode, SNode rightNode, double expected,
        Graph graphWidget) {
      this.leftNode = leftNode;
      this.rightNode = rightNode;
      this.expected = expected;
      this.graphWidget = graphWidget;
    }

    private double getDistance() {
      Rectangle l = getGraphNodeForSalt(bot, graphWidget, leftNode).getNodeFigure().getBounds();
      Rectangle r = getGraphNodeForSalt(bot, graphWidget, rightNode).getNodeFigure().getBounds();
      return Math.abs(l.getRight().x - r.getLeft().x);
    }

    @Override
    public boolean test() throws Exception {
      double diff = Math.abs(getDistance() - expected);
      // Allow for 20% tolerance, but at least 1 point
      return diff <= Math.max(1.0, expected * 0.2);
    }

    @Override
    public String getFailureMessage() {
      return "Horizontal distance between node " + leftNode.getName() + " and "
          + rightNode.getName() + " should have been " + expected + " but was " + getDistance()
          + ".";
    }
  }

  private final class VerticalNodeDistanceCondition extends DefaultCondition {
    private final SNode topNode;
    private final SNode bottomNode;
    private final double expected;
    private final Graph graphWidget;

    private VerticalNodeDistanceCondition(SNode topNode, SNode bottomNode, double expected,
        Graph graphWidget) {
      this.topNode = topNode;
      this.bottomNode = bottomNode;
      this.expected = expected;
      this.graphWidget = graphWidget;
    }

    private double getDistance() {
      GraphNode t = getGraphNodeForSalt(bot, graphWidget, topNode);
      GraphNode b = getGraphNodeForSalt(bot, graphWidget, bottomNode);
      return Math
          .abs(t.getNodeFigure().getBounds().bottom() - b.getNodeFigure().getBounds().getTop().y);
    }

    @Override
    public boolean test() throws Exception {
      double diff = Math.abs(getDistance() - expected);
      // Allow for 20% tolerance, but at least 1 point
      return diff <= Math.max(1.0, expected * 0.2);
    }

    @Override
    public String getFailureMessage() {
      return "Vertical distance between node " + topNode.getName() + " and " + bottomNode.getName()
          + " should have been " + expected + " but was " + getDistance() + ".";
    }
  }

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
      SWTBotView view = TestGraphEditor.this.bot.partByTitle(this.documentName + " (Graph Editor)");
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
    uiStatus = ContextInjectionFactory.make(UiStatusReport.class, ctx);

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

  @AfterEach
  void closeEditor() {
    for (SWTBotView part : bot.parts()) {
      if (part.getTitle().endsWith("(Graph Editor)")) {
        part.close();
      }
    }
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

    bot.waitUntil(new GraphLoadedCondition(0), SWTBotPreferences.TIMEOUT, 100);
  }

  void openMinimalProjectStructure() {
    TestCorpusStructure.createMinimalCorpusStructure(bot);

    // Select the first example document
    SWTBotTreeItem docMenu = bot.partById(CORPUS_EDITOR_PART_ID).bot().tree()
        .expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("document_1");

    // Select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu("Open with Graph Editor").click());

    bot.waitUntil(new GraphLoadedCondition("document_1"), SWTBotPreferences.TIMEOUT, 100);
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
    console.insertText("\n");

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
      });
    }
  }

  @Test
  void testShowSaltExample()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    // Open example and maximize part
    openDefaultExample();

    SWTBotView view = bot.partByTitle(DOC1_TITLE);
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

    // Initially, the zoom is adjusted to match the height, so moving up/down should
    // not do anything
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);

    // Zoom in so we can move the view with the arrow keys
    // Use the mock keyboard for this since AWT/SWT keyboards don't map the keypad
    // keys yet.
    Keyboard mockKeyboadForGraph = KeyboardFactory.getMockKeyboard(g, new WidgetTextDescription(g));
    KeyStroke[] strokesZoomIn = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_ADD)};
    mockKeyboadForGraph.pressShortcut(strokesZoomIn);

    origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);

    // Scroll with arrow keys (left, right, up, down) and check that that view has
    // been moved
    keyboard.pressShortcut(Keystrokes.RIGHT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x + 25, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.LEFT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 25)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.PAGE_DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 25)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.PAGE_UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);

    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.RIGHT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x + 250, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.LEFT);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 250)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.PAGE_DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y + 250)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.SHIFT, Keystrokes.PAGE_UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);

    // Zoom out again: moving up should not have any effect again
    KeyStroke[] strokesZoomOut = {Keystrokes.CTRL, KeyStroke.getInstance(0, SWT.KEYPAD_SUBTRACT)};
    mockKeyboadForGraph.pressShortcut(strokesZoomOut);
    origLocation = (Point) SWTUtils.invokeMethod(viewPort, GET_VIEW_LOCATION);
    keyboard.pressShortcut(Keystrokes.DOWN);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
    keyboard.pressShortcut(Keystrokes.UP);
    bot.waitUntil(
        new ViewLocationReachedCondition(viewPort, new Point(origLocation.x, origLocation.y)),
        SWTBotPreferences.TIMEOUT, 100);
  }

  @Test
  void testAddPointingRelation() {

    openDefaultExample();

    // Get a reference to the open graph
    Optional<SDocument> optionalDocument = projectManager.getDocument(DOC1_SALT_ID);
    assertTrue(optionalDocument.isPresent());
    if (optionalDocument.isPresent()) {
      SDocument doc = optionalDocument.get();
      SDocumentGraph graph = doc.getDocumentGraph();

      // Before state: no edges between the two structures
      assertEquals(0, graph.getRelations(STRUCTURE3_ID, STRUCTURE5_ID).size());

      SWTBotView view = bot.partByTitle(DOC1_TITLE);
      view.show();
      bot.waitUntil(new DefaultCondition() {

        @Override
        public boolean test() throws Exception {
          return view.getPart().getObject() instanceof GraphEditor;
        }

        @Override
        public String getFailureMessage() {
          return "Part object did not have the GraphEditor type";
        }
      });
      GraphEditor graphEditor = (GraphEditor) view.getPart().getObject();

      // Test that double clicking without selected node does nothing
      SWTBotStyledText console = bot.styledTextWithId(GraphEditor.CONSOLE_ID);
      assertEquals("> ", console.getTextOnCurrentLine());
      bot.getDisplay().syncExec(() -> {
        graphEditor.getViewer().setSelection(new StructuredSelection(new ArrayList<>()));
        graphEditor.appendSelectedNodeNameToConsole();
      });
      assertEquals("> ", console.getTextOnCurrentLine());

      // Type start of the command
      console.insertText("e");
      console.navigateTo(console.getLineCount() - 1, console.getTextOnCurrentLine().length());

      // Emulate double clicking on the first referenced node
      bot.getDisplay().syncExec(() -> {
        graphEditor.getViewer().setSelection(new StructuredSelection(graph.getNode(STRUCTURE3_ID)));
        graphEditor.appendSelectedNodeNameToConsole();
      });

      // Add the point relation operator to the command
      console.insertText("-> ");
      console.navigateTo(console.getLineCount() - 1, console.getTextOnCurrentLine().length());

      // Emulate double clicking on the second referenced node
      bot.getDisplay().syncExec(() -> {
        graphEditor.getViewer().setSelection(new StructuredSelection(graph.getNode(STRUCTURE5_ID)));
        graphEditor.appendSelectedNodeNameToConsole();
      });

      assertEquals("> " + ADD_POINTING_COMMMAND + " ", console.getTextOnCurrentLine());
      console.navigateTo(console.getLineCount() - 1, console.getTextOnCurrentLine().length());
      console.insertText("\n");


      // Check that no exception was thrown/handled by UI
      assertFalse(errorService.getLastException().isPresent());

      // Check that the edge has been added to the graph
      List<?> rels = graph.getRelations(STRUCTURE3_ID, STRUCTURE5_ID);
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
    Optional<SDocument> optionalDoc = projectManager.getDocument(DOC1_SALT_ID);
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

      bot.waitUntil(new NoBackgroundJobsCondition(uiStatus));

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

    // Deactivate/activate pointing relations in view and check the view has
    // less/more connections
    bot.expandBarInGroup(FILTER_VIEW).expandItem(ANNOTATION_TYPES);
    SWTBotCheckBox includePointing = bot.checkBox("Pointing Relations");
    includePointing.deselect();
    bot.waitUntil(new NumberOfConnectionsCondition(22));
    includePointing.select();
    bot.waitUntil(new NumberOfConnectionsCondition(23));

    // Deactivate/activate spans in view and check the view has less/more nodes
    SWTBotCheckBox includeSpans = bot.checkBox(SPANS);
    includeSpans.deselect();
    bot.waitUntil(new NumberOfNodesCondition(23));
    includeSpans.select();
    bot.waitUntil(new NumberOfNodesCondition(26));
    // With spans enabled, filter for annotation names
    bot.expandBarInGroup(FILTER_VIEW).expandItem(ANNOTATION_NAME);
    SWTBotText annoFilter = bot.textWithMessage(SEARCH);

    // Tokens and the matching structure nodes
    annoFilter.setFocus();
    awtKeyboard.typeText(CONST);
    awtKeyboard.pressShortcut(Keystrokes.LF);
    bot.waitUntil(new VisibleChipsCondition(1));
    final SwtBotChips constChip = new SwtBotChips(getVisibleChips(bot).get(0));
    bot.waitUntil(new NumberOfNodesCondition(23));

    // Tokens and the matching spans
    annoFilter.setFocus();
    if (SystemUtils.IS_OS_MAC_OSX) {
      keyboard.typeText("inf-struct");
      keyboard.pressShortcut(Keystrokes.LF);
    } else {
      awtKeyboard.typeText("inf-struct");
      awtKeyboard.pressShortcut(Keystrokes.LF);
    }
    bot.waitUntil(new VisibleChipsCondition(2));

    bot.waitUntil(new NumberOfNodesCondition(25));

    // Check that already added annotation names are not added twice
    annoFilter.typeText("inf");
    annoFilter.pressShortcut(Keystrokes.LF);
    assertEquals(2, getVisibleChips(bot).size());

    // Remove chip again by simulating a mouse click
    constChip.click();
    assertEquals(1, getVisibleChips(bot).size());
    bot.waitUntil(new NumberOfNodesCondition(13));
  }

  @Test

  @EnabledOnOs({OS.WINDOWS, OS.LINUX})
  void testLayoutParameters() {
    openDefaultExample();

    SWTBotView view = bot.partByTitle(DOC1_TITLE);
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    Graph g = bot.widget(widgetOfType(Graph.class));
    assertNotNull(g);

    Optional<SDocument> optionalDoc = projectManager.getDocument(DOC1_SALT_ID);
    assertTrue(optionalDoc.isPresent());
    if (optionalDoc.isPresent()) {
      SDocument doc = optionalDoc.get();
      SDocumentGraph graph = doc.getDocumentGraph();
      graph.sortTokenByText();

      SWTBotExpandBar layoutPanel = bot.expandBarWithId(GraphEditor.ID_PREFIX + "layout-expandbar");
      layoutPanel.expandItem(GRAPH_LAYOUT);

      // Use initial values from which we will multiply the margins
      SWTBot botLayout = new SWTBot(layoutPanel.widget);
      SWTBotScale horizontalScale = botLayout.scale(0);

      // Change the horizontal margin parameter and check that distance between the
      // first two token is updated
      horizontalScale.setValue(0);
      assertEquals("0.0", botLayout.label(1).getText());
      List<SToken> token = graph.getTokens();
      bot.waitUntil(new HorizontalNodeDistanceCondition(token.get(0), token.get(1), 0.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      horizontalScale.setValue(5);
      assertEquals("0.5", botLayout.label(1).getText());
      bot.waitUntil(new HorizontalNodeDistanceCondition(token.get(0), token.get(1), 65.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      horizontalScale.setValue(10);
      assertEquals("1.0", botLayout.label(1).getText());
      bot.waitUntil(new HorizontalNodeDistanceCondition(token.get(0), token.get(1), 130.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      horizontalScale.setValue(14);
      assertEquals("1.4", botLayout.label(1).getText());
      bot.waitUntil(new HorizontalNodeDistanceCondition(token.get(0), token.get(1), 182.0, g),
          SWTBotPreferences.TIMEOUT, 100);
      horizontalScale.setValue(15);

      horizontalScale.setValue(20);
      assertEquals("2.0", botLayout.label(1).getText());
      bot.waitUntil(new HorizontalNodeDistanceCondition(token.get(0), token.get(1), 260.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      // Change the vertical margin and compare the distance between the root node and
      // a node below
      // it
      SNode rootNode = graph.getNodesByName("structure1").get(0);
      SNode struct2 = graph.getNodesByName("structure2").get(0);
      SWTBotScale verticalScale = botLayout.scale(1);

      verticalScale.setValue(0);
      assertEquals("0.0", botLayout.label(3).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(rootNode, struct2, 0.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      verticalScale.setValue(3);
      assertEquals("0.3", botLayout.label(3).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(rootNode, struct2, 15.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      verticalScale.setValue(10);
      assertEquals("1.0", botLayout.label(3).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(rootNode, struct2, 53.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      verticalScale.setValue(20);
      assertEquals("2.0", botLayout.label(3).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(rootNode, struct2, 106.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      // Change the vertical token margin and compare the distance between a token
      // node and node and
      // a node on the lowest level
      verticalScale.setValue(0);

      SWTBotScale tokenScale = botLayout.scale(2);
      tokenScale.setValue(0);
      assertEquals("0", botLayout.label(5).getText());
      SNode tok10 = graph.getNodesByName("sTok10").get(0);
      SNode struct12 = graph.getNodesByName("structure12").get(0);
      bot.waitUntil(new VerticalNodeDistanceCondition(struct12, tok10, 0.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      tokenScale.setValue(1);
      assertEquals("1", botLayout.label(5).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(struct12, tok10, 53.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      tokenScale.setValue(2);
      assertEquals("2", botLayout.label(5).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(struct12, tok10, 106.0, g),
          SWTBotPreferences.TIMEOUT, 100);

      tokenScale.setValue(5);
      assertEquals("5", botLayout.label(5).getText());
      bot.waitUntil(new VerticalNodeDistanceCondition(struct12, tok10, 265.0, g),
          SWTBotPreferences.TIMEOUT, 100);

    }
  }

  private GraphNode getGraphNodeForSalt(SWTBot bot, Graph g, SNode saltNode) {
    return bot.getDisplay().syncCall(() -> {
      for (Object n : g.getNodes()) {
        if (n instanceof GraphNode) {
          GraphNode gn = (GraphNode) n;
          if (gn.getData() == saltNode) {
            return gn;
          }
        }
      }
      return null;
    });
  }

  private List<? extends Chips> getVisibleChips(SWTBot bot) {
    return bot.widgets(widgetOfType(Chips.class), bot.expandBarInGroup(FILTER_VIEW).widget);
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
    assertFalse(errorService.getLastException().isPresent(),
        () -> "Unexpected exception recorded by error service: "
            + errorService.getLastException().get().toString());
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

    // Go back again, just to make sure the user does not need to click the arrow
    // key twice
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
    bot.expandBarInGroup(FILTER_VIEW).expandItem(ANNOTATION_TYPES);
    SWTBotCheckBox includeSpans = bot.checkBox(SPANS);
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
   * Tests that the segmentation list is updated when a token text is changed (especially the last
   * one).
   */
  @Test
  void testUpdateSegmentsOnLastTokenTextChanged() {
    openDefaultExample();

    SWTBotTable textRangeTable = bot.tableWithId(GraphEditor.TEXT_RANGE_ID);

    enterCommand("tc #sTok11 ???");
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return textRangeTable
            .containsItem("Is this example more complicated than it appears to be???");
      }

      @Override
      public String getFailureMessage() {
        return "Segment for updated token was not shown";
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

    // Make sure the relevant spans are shown
    bot.expandBarInGroup(FILTER_VIEW).expandItem(ANNOTATION_TYPES);
    bot.checkBox(SPANS).select();

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

    if (SystemUtils.IS_OS_MAC_OSX) {
      // There seems to be an issue with editing a cell when the span was created from
      // a context menu triggered by SWT bot. Clicking manually on the context menu
      // works and the text can be inserted right away. Pressing ESC first on macOS
      // circumvents this problem, but is more a workaround.
      awtKeyboard.pressShortcut(Keystrokes.ESC);
    }
    awtKeyboard.pressShortcut(Keystrokes.ESC);

    awtKeyboard.typeText("anothertest", 10);
    awtKeyboard.pressShortcut(Keystrokes.CR);
    bot.waitUntil(new TableCellEditorInactiveCondition(table), 1000);

    // Close the Grid editor, which selects the Graph Editor again and
    // wait for the annotation value to change
    for (SWTBotView part : bot.parts()) {
      if (part.getTitle().endsWith("(Grid Editor)")) {
        part.close();
      }
    }

    bot.waitUntil(new HasNodeWithText("Inf-Struct=anothertest"));
  }
}
