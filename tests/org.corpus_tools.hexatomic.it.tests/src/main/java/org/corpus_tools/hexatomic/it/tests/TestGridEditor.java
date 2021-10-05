package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.tuple.Pair;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.grid.GridEditor;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.style.editor.AbstractEditorPanel;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.Position;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRootMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the grid editor.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
@SuppressWarnings("restriction")
public class TestGridEditor {


  private static final String TEXT1_CAPTION = "sText1";

  private static final String DOC_GRID_EDITOR = "doc (Grid Editor)";

  private static final String DOC = "doc";

  private static final String CORPUS = "corpus";

  private static final String UNRENAMED_ANNOTATIONS_DIALOG_TITLE =
      "Some annotations were not renamed!";

  private static final String TOKEN_VALUE = "Token";

  private static final String OPEN_WITH_GRID_EDITOR = "Open with Grid Editor";

  private static final String TEST_ANNOTATION_VALUE = "TEST";
  private static final String CONTRAST_FOCUS_VALUE = "contrast-focus";
  private static final String MORE_VALUE = "more";
  private static final String COMPLICATED_VALUE = "complicated";
  private static final String IT_VALUE = "it";
  private static final String JJ_VALUE = "JJ";
  private static final String EXAMPLE_VALUE = "example";
  private static final String TOPIC_VALUE = "topic";

  private static final String NAMESPACE = SaltUtil.SALT_NAMESPACE + SaltUtil.NAMESPACE_SEPERATOR;

  private static final String TESTPATH_GRID = "../org.corpus_tools.hexatomic.grid.tests/"
      + "src/main/resources/org/corpus_tools/hexatomic/grid/";

  private static final String RENAME_DIALOG_TITLE = "Rename annotation";

  private static final String SPAN_1 = "span_1";
  private static final String SPAN_2 = "span_2";

  private static final String FIVE = "five::";

  private static final String LEMMA_NAME = "lemma";
  private static final String POS_NAME = "pos";
  private static final String INF_STRUCT_NAME = "Inf-Struct";

  private static final String NAMESPACED_LEMMA_NAME = NAMESPACE + LEMMA_NAME;



  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private URI overlappingExampleProjectUri;
  private URI twoDsExampleProjectUri;
  private URI scrollingExampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private final Keyboard keyboard = KeyboardFactory.getAWTKeyboard();

  private ProjectManager projectManager;


  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();

    IEclipseContext ctx = TestHelper.getEclipseContext();

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    EPartService partService = ctx.get(EPartService.class);
    assertNotNull(partService);

    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    File overlappingExampleProjectDirectory = new File(TESTPATH_GRID + "overlapping-spans/");
    assertTrue(overlappingExampleProjectDirectory.isDirectory());

    File twoDsExampleProjectDirectory = new File(TESTPATH_GRID + "two-ds/");
    assertTrue(twoDsExampleProjectDirectory.isDirectory());

    File scrollingExampleProjectDirectory = new File(TESTPATH_GRID + "scrolling/");
    assertTrue(scrollingExampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
    overlappingExampleProjectUri =
        URI.createFileURI(overlappingExampleProjectDirectory.getAbsolutePath());
    twoDsExampleProjectUri = URI.createFileURI(twoDsExampleProjectDirectory.getAbsolutePath());
    scrollingExampleProjectUri =
        URI.createFileURI(scrollingExampleProjectDirectory.getAbsolutePath());
  }

  @AfterEach
  void cleanup() {
    // Close all editors manually.
    // If the editor is not closed, it might trigger bugs when executing other tests.
    // For example, notifications about project changes might trigger exception when the document
    // is already gone.
    for (SWTBotView view : bot.parts()) {
      if (view.getPart().getPersistedState()
          .containsKey("org.corpus_tools.hexatomic.document-id")) {
        view.close();
      }
    }
    // Close any open rename dialogs
    for (SWTBotShell shell : bot.shells()) {
      if (shell.getText().equals(RENAME_DIALOG_TITLE)) {
        shell.close();
      }
    }
    // TODO: when close project is implemented with save functionality, change this to close the
    // project and its editors

  }

  SWTBotView openEditorForDefaultDocument() {
    // Select the first example document
    SWTBotTreeItem docMenu = bot.tree().expandNode("corpusGraph1").expandNode("rootCorpus")
        .expandNode("subCorpus1").expandNode("doc1");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    SWTBotView view = bot.partByTitle("doc1 (Grid Editor)");
    assertNotNull(view);

    // Use all available windows space (the table needs to be fully visible for some of the tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }

  SWTBotView openDefaultExample() {
    // Programmatically open the example corpus
    openExample(exampleProjectUri);

    return openEditorForDefaultDocument();
  }

  SWTBotView openOverlapExample() {
    // Programmatically open the example corpus
    openExample(overlappingExampleProjectUri);
    // Select the first example document
    SWTBotTreeItem docMenu =
        bot.tree().expandNode("corpus-graph").expandNode(CORPUS).expandNode(DOC);

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    final SWTBotView view = bot.partByTitle(DOC_GRID_EDITOR);
    assertNotNull(view);

    // Use all available windows space (the table needs to be fully visible for some of the tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }

  SWTBotView openTwoDsExample() {
    // Programmatically open the example corpus
    openExample(twoDsExampleProjectUri);
    // Select the first example document
    SWTBotTreeItem docMenu = bot.tree().expandNode("<unknown>").expandNode(CORPUS).expandNode(DOC);

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    SWTBotView view = bot.partByTitle(DOC_GRID_EDITOR);
    assertNotNull(view);

    // Use all available windows space (the table needs to be fully visible for some of the tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }

  SWTBotView openScrollingExample() {
    // Programmatically open the example corpus
    openExample(scrollingExampleProjectUri);
    // Select the first example document
    SWTBotTreeItem docMenu =
        bot.tree().expandNode("corpus-graph").expandNode(CORPUS).expandNode(DOC);

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    SWTBotView view = bot.partByTitle(DOC_GRID_EDITOR);
    assertNotNull(view);

    // Use all available windows space (the table needs to be fully visible for some of the tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }

  private void openExample(URI exampleUri) {
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, exampleUri.toFileString());
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.open_salt_project", params);
    handlerService.executeHandler(cmd);

    // Activate corpus structure editor
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();
  }

  @Test
  void testShowSaltExample() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Check number of rows and columns (11 + 1 header row, 4 + 1 header column)
    assertEquals(12, natTable.getRowCount());
    assertEquals(5, natTable.getColumnCount());

    // Test headers
    assertEquals(null, natTable.getDataValueByPosition(0, 0));
    assertEquals(2, natTable.getDataValueByPosition(0, 2));
    assertEquals(11, natTable.getDataValueByPosition(0, 11));
    assertEquals(TOKEN_VALUE, table.getCellDataValueByPosition(0, 1));
    assertEquals(TOKEN_VALUE, natTable.getDataValueByPosition(1, 0));
    assertEquals(INF_STRUCT_NAME, table.getCellDataValueByPosition(0, 4));
    assertEquals(INF_STRUCT_NAME, natTable.getDataValueByPosition(4, 0));

    // Test cells
    // Token text
    Object token1Obj = natTable.getDataValueByPosition(1, 1);
    assertTrue(token1Obj instanceof SToken);
    SToken token1 = (SToken) token1Obj;
    assertEquals("Is", (token1).getGraph().getText(token1));
    Object token2Obj = natTable.getDataValueByPosition(1, 11);
    assertTrue(token2Obj instanceof SToken);
    SToken token2 = (SToken) token2Obj;
    assertEquals("?", token2.getGraph().getText(token2));

    // Annotations
    String lemmaHeader = natTable.getDataValueByPosition(2, 0).toString();
    assertEquals("be", ((SAnnotationContainer) natTable.getDataValueByPosition(2, 1))
        .getAnnotation(lemmaHeader).getValue());
    String infStructHeader = natTable.getDataValueByPosition(4, 0).toString();
    assertEquals(CONTRAST_FOCUS_VALUE,
        ((SAnnotationContainer) natTable.getDataValueByPosition(4, 1))
            .getAnnotation(infStructHeader).getValue());
    assertEquals(TOPIC_VALUE, ((SAnnotationContainer) natTable.getDataValueByPosition(4, 2))
        .getAnnotation(infStructHeader).getValue());
    assertEquals(TOPIC_VALUE, ((SAnnotationContainer) natTable.getDataValueByPosition(4, 11))
        .getAnnotation(infStructHeader).getValue());
  }

  @Test
  void testShowOverlapSaltExample() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Check number of rows and columns (5 + 1 header row, 5 + 1 header column)
    assertEquals(6, natTable.getRowCount());
    assertEquals(6, natTable.getColumnCount());

    // Test headers
    assertEquals("", table.getCellDataValueByPosition(0, 0));
    assertEquals("2", table.getCellDataValueByPosition(2, 0));
    assertEquals("5", table.getCellDataValueByPosition(5, 0));
    assertEquals(TOKEN_VALUE, table.getCellDataValueByPosition(0, 1));
    assertEquals(FIVE + SPAN_1, table.getCellDataValueByPosition(0, 3));
    assertEquals(FIVE + SPAN_1 + " (2)", table.getCellDataValueByPosition(0, 4));
    assertEquals(FIVE + SPAN_2, table.getCellDataValueByPosition(0, 5));

    // Test cells
    assertEquals("val_span_1", ((SAnnotationContainer) natTable.getDataValueByPosition(3, 1))
        .getAnnotation(table.getCellDataValueByPosition(0, 3)).getValue());
    assertEquals("val_span_2", ((SAnnotationContainer) natTable.getDataValueByPosition(3, 2))
        .getAnnotation(table.getCellDataValueByPosition(0, 3)).getValue());
    assertEquals("val_span_2", ((SAnnotationContainer) natTable.getDataValueByPosition(3, 5))
        .getAnnotation(table.getCellDataValueByPosition(0, 3)).getValue());
    assertEquals("val_span_3", ((SAnnotationContainer) natTable.getDataValueByPosition(4, 1))
        .getAnnotation(table.getCellDataValueByPosition(0, 3)).getValue());
    assertEquals("val_span_3", ((SAnnotationContainer) natTable.getDataValueByPosition(4, 2))
        .getAnnotation(table.getCellDataValueByPosition(0, 3)).getValue());
    assertEquals("", table.getCellDataValueByPosition(1, 5));
    assertNull(natTable.getDataValueByPosition(5, 1));
  }

  @Test
  void testDsSelection() {
    openTwoDsExample();

    SWTBotCombo combo = bot.comboBox();
    assertNotNull(combo);

    // Select first text
    combo.setSelection(0);
    NatTable table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    assertEquals(3, table.getColumnCount());
    assertEquals(7, table.getRowCount());
    assertEquals("pony", ((SDocumentGraph) ((SNode) table.getDataValueByPosition(1, 5)).getGraph())
        .getText((SNode) table.getDataValueByPosition(1, 5)));
    assertEquals("one::span", table.getDataValueByPosition(2, 0));

    // Select second text
    combo.setSelection(1);
    table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    assertEquals(3, table.getColumnCount());
    assertEquals(5, table.getRowCount());
    assertEquals("annotations",
        ((SDocumentGraph) ((SNode) table.getDataValueByPosition(1, 3)).getGraph())
            .getText((SNode) table.getDataValueByPosition(1, 3)));
    assertEquals("four::token", table.getDataValueByPosition(2, 0));

    // Select first text again
    combo.setSelection(0);
    table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    assertEquals(3, table.getColumnCount());
    assertEquals(7, table.getRowCount());
    assertEquals("pony", ((SDocumentGraph) ((SNode) table.getDataValueByPosition(1, 5)).getGraph())
        .getText((SNode) table.getDataValueByPosition(1, 5)));
    assertEquals("one::span", table.getDataValueByPosition(2, 0));
  }

  @Test
  void testDropdownWithSingleText() {
    openDefaultExample();

    SWTBotCombo combo = bot.comboBox();
    assertNotNull(combo);

    assertEquals(TEXT1_CAPTION, combo.getText());
  }

  @Test
  void testDropdownWithTwoTexts() {
    openTwoDsExample();

    SWTBotCombo combo = bot.comboBox();
    assertNotNull(combo);

    assertEquals("", combo.getText());

    combo.setSelection(1);
    assertEquals("Token annotations only", combo.getText());

    combo.setSelection(0);
    assertEquals(TEXT1_CAPTION, combo.getText());

    combo.pressShortcut(KeyStroke.getInstance(SWT.ARROW_DOWN));
    assertEquals("Token annotations only", combo.getText());
  }

  @Test
  void testDropdownWithTextButNoToken() {
    // Programmatically open the example corpus, but do not open
    // the editor yet
    openExample(exampleProjectUri);


    // Delete all token of the first document (but keep the STextualDS)
    Optional<SDocument> document =
        projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(document.isPresent());
    if (document.isPresent()) {
      SDocumentGraph docGraph = document.get().getDocumentGraph();
      List<SToken> tokens = new LinkedList<>(docGraph.getTokens());
      tokens.stream().forEach(docGraph::removeNode);

      projectManager.addCheckpoint();

      assertEquals(0, docGraph.getTokens().size());
      assertEquals(1, docGraph.getTextualDSs().size());

      // Open the grid editor with the document
      openEditorForDefaultDocument();

      SWTBotCombo combo = bot.comboBox();
      assertNotNull(combo);

      assertEquals(TEXT1_CAPTION, combo.getText());

      Object decoRaw =
          UIThreadRunnable.syncExec(() -> combo.widget.getData(GridEditor.CONTROL_DECORATION));
      assertNotNull(decoRaw);
      assertTrue(decoRaw instanceof ControlDecoration);
      if (decoRaw instanceof ControlDecoration) {
        ControlDecoration deco = (ControlDecoration) decoRaw;
        assertTrue(UIThreadRunnable.syncExec(deco::isVisible));
      }
    }
  }

  @Test
  void testColumnPopupMenu() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> columnItems = table.contextMenu(0, 1).menuItems();
    assertEquals("Hide column(s)", columnItems.get(0));
    assertEquals("Show all columns", columnItems.get(1));
    assertEquals("Auto-resize column(s)", columnItems.get(3));
    assertEquals("Set column freeze", columnItems.get(5));
    assertEquals("Toggle freeze", columnItems.get(6));
  }

  @Test
  void testRowPopupMenu() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> columnItems = table.contextMenu(1, 0).menuItems();
    assertEquals("Auto-resize row(s)", columnItems.get(0));
    assertEquals("Set row freeze", columnItems.get(2));
    assertEquals("Toggle freeze", columnItems.get(3));
  }

  @Test
  void testHideShowColumns() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    assertEquals(TOKEN_VALUE, table.getCellDataValueByPosition(0, 1));

    // Hide token column
    table.contextMenu(0, 1).contextMenu("Hide column(s)").click();
    assertEquals(NAMESPACE + LEMMA_NAME, table.getCellDataValueByPosition(0, 1));

    // Show columns
    table.contextMenu(0, 1).contextMenu("Show all columns").click();
    assertEquals(TOKEN_VALUE, table.getCellDataValueByPosition(0, 1));
  }

  @Test
  void testEmptyCellStyleApplied() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(1, 5));
    assertTrue(table.hasConfigLabel(1, 5, StyleConfiguration.EMPTY_CELL_STYLE));
  }

  /**
   * Tests editing cells by activating the cell editor with a double-click on the cell.
   */
  @Test
  void testEditCellOnDoubleClick() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.doubleclick(2, 2);
    typeTextPressReturn(table);

    Object nodeObj = table.widget.getDataValueByPosition(2, 2);
    assertTrue(nodeObj instanceof SNode);
    SNode node = (SNode) nodeObj;
    assertEquals(TEST_ANNOTATION_VALUE,
        node.getAnnotation(table.getCellDataValueByPosition(0, 2)).getValue());
  }

  /**
   * Tests editing cells by activating the cell editor by just typing away.
   */
  @Test
  void testEditCellTyping() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(2, 2);
    typeTextPressReturn(table);
    Object nodeObj = table.widget.getDataValueByPosition(2, 2);
    assertTrue(nodeObj instanceof SNode);
    SNode node = (SNode) nodeObj;
    assertEquals(TEST_ANNOTATION_VALUE,
        node.getAnnotation(table.getCellDataValueByPosition(0, 2)).getValue());
  }

  /**
   * Tests editing cells by activating the cell editor by just typing away.
   */
  @Test
  void testEditCellSpaceActivatedTyping() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(2, 2);
    keyboard.pressShortcut(Keystrokes.SPACE);
    typeTextPressReturn(table);
    Object nodeObj = table.widget.getDataValueByPosition(2, 2);
    assertTrue(nodeObj instanceof SNode);
    SNode node = (SNode) nodeObj;
    assertEquals(TEST_ANNOTATION_VALUE,
        node.getAnnotation(table.getCellDataValueByPosition(0, 2)).getValue());
  }

  @Test
  void testCreateAnnotationOnEmptySpanCell() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(1, 5));
    table.click(1, 5);
    typeTextPressReturn(table);
    Object nodeObj = table.widget.getDataValueByPosition(5, 1);
    assertTrue(nodeObj instanceof SNode);
    SNode node = (SNode) nodeObj;
    assertEquals(TEST_ANNOTATION_VALUE,
        node.getAnnotation(table.getCellDataValueByPosition(0, 5)).getValue());
  }

  @Test
  void testCreateAnnotationOnEmptyTokenCell() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(2, 2));
    table.click(2, 2);
    typeTextPressReturn(table);
    Object nodeObj = table.widget.getDataValueByPosition(2, 2);
    assertTrue(nodeObj instanceof SNode);
    SNode node = (SNode) nodeObj;
    assertEquals(TEST_ANNOTATION_VALUE,
        node.getAnnotation(table.getCellDataValueByPosition(0, 2)).getValue());
  }

  /**
   * Types the value of TEST_ANNOTATION_VALUE, then Return, then waits until the table has no active
   * cell editors, up to 1000ms.
   * 
   * @param table The {@link NatTable} to operate on
   * @throws TimeoutException after 1000ms without returning successfully
   */
  private void typeTextPressReturn(SWTBotNatTable table) {
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    keyboard.pressShortcut(Keystrokes.CR);
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return table.widget.getActiveCellEditor() == null;
      }

      @Override
      public String getFailureMessage() {
        return "Setting new value for cell took too long.";
      }
    }, 1000);
  }

  protected static class CellDataValueCondition extends DefaultCondition {

    private final SWTNatTableBot tableBot;
    private final int row;
    private final int column;
    private final String expected;

    public CellDataValueCondition(SWTNatTableBot tableBot, int row, int column, String expected) {
      super();
      this.tableBot = tableBot;
      this.row = row;
      this.column = column;
      this.expected = expected;
    }

    @Override
    public boolean test() throws Exception {
      String value = tableBot.nattable().getCellDataValueByPosition(row, column);
      return Objects.equals(value, expected);
    }

    @Override
    public String getFailureMessage() {
      return "NatTable cell at position " + row + "," + column + " did not have expected value "
          + expected;
    }

  }

  @Test
  void testRemoveSingleAnnotationByDelKey() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(1, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 1, 4, ""));

    table.click(2, 3);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 2, 3, ""));

    table.click(2, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 2, 4, ""));
    NatTable natTable = table.widget;
    assertEquals("", table.getCellDataValueByPosition(3, 4));
    assertNull(natTable.getDataValueByPosition(4, 3));
    assertEquals("", table.getCellDataValueByPosition(4, 4));
    assertNull(natTable.getDataValueByPosition(4, 4));
    assertEquals("", table.getCellDataValueByPosition(5, 4));
    assertNull(natTable.getDataValueByPosition(4, 5));
    assertEquals("", table.getCellDataValueByPosition(6, 4));
    assertNull(natTable.getDataValueByPosition(4, 6));
    assertEquals("", table.getCellDataValueByPosition(7, 4));
    assertNull(natTable.getDataValueByPosition(4, 7));
  }

  @Test
  void testRemoveSingleAnnotationByPopupMenu() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(1, 4);
    List<String> columnItems = table.contextMenu(2, 3).menuItems();
    assertTrue(columnItems.get(0).contains(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL));
    table.contextMenu(2, 3).contextMenu(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL).click();
    assertEquals("", table.getCellDataValueByPosition(1, 4));
    assertNull(table.widget.getDataValueByPosition(4, 1));
    assertNotNull(table.getCellDataValueByPosition(2, 3));
  }

  @Test
  void testPopupMenuInvisibleOnSelectedTokenText() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    table.click(1, 1);
    SWTBotRootMenu contextMenu = table.contextMenu(1, 1);
    // No context menu should exist
    assertTrue(contextMenu.menuItems().isEmpty());
    assertThrows(WidgetNotFoundException.class, contextMenu::contextMenu);
  }

  @Test
  void testPopupMenuInvisibleOnNoSelection() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    SWTBotRootMenu contextMenu = table.contextMenu(1, 1);
    // No context menu should exist
    assertTrue(contextMenu.menuItems().isEmpty());
    assertThrows(WidgetNotFoundException.class, contextMenu::contextMenu);
  }

  @Test
  void testDeleteCellsPopupMenuInvisibleOnNoSelection() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> column0HeaderMenuItems = table.contextMenu(1, 1).menuItems();
    assertFalse(column0HeaderMenuItems.contains(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL));
  }

  @Test
  void testChangeAnnotatioNamePopupMenuInvisibleOnSelectedTokenText() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    table.click(1, 1);
    List<String> column0HeaderMenuItems = table.contextMenu(1, 1).menuItems();
    assertFalse(
        column0HeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
  }

  @Test
  void testChangeAnnotationNamePopupMenuInvisibleOnNoSelection() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> column0HeaderMenuItems = table.contextMenu(1, 1).menuItems();
    assertFalse(
        column0HeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
  }

  @Test
  void testRenameColumnByContextMenuPresent() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> columnHeaderMenuItems = table.contextMenu(0, 2).menuItems();
    assertTrue(columnHeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
    assertTrue(table.contextMenu(0, 2)
        .contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).isVisible());
    assertTrue(table.contextMenu(0, 2)
        .contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).isEnabled());
  }

  @Test
  void testRenameColumnMenuNotVisibleInIdxAndTokenTextColumn() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    List<String> column0HeaderMenuItems = table.contextMenu(0, 0).menuItems();
    assertFalse(
        column0HeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
    List<String> column1HeaderMenuItems = table.contextMenu(0, 1).menuItems();
    assertFalse(
        column1HeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
  }

  @Test
  void testAnnotationChangeDialogWithOkButton() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertEquals(NAMESPACE + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 2));
    // Also check if annotation name has changed for all cells in column
    for (int i = 1; i < 11; i++) {
      Object nodeObj = table.widget.getDataValueByPosition(2, i);
      assertNotNull(((SToken) nodeObj).getAnnotation(NAMESPACE + TEST_ANNOTATION_VALUE));
    }
  }

  @Test
  void testAnnotationChangeDialogWithReturn() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    keyboard.pressShortcut(Keystrokes.CR);
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertEquals(NAMESPACE + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 2));
    // Also check if annotation name has changed for all cells in column
    for (int i = 1; i < 11; i++) {
      Object nodeObj = table.widget.getDataValueByPosition(2, i);
      assertNotNull(((SToken) nodeObj).getAnnotation(NAMESPACE + TEST_ANNOTATION_VALUE));
    }
  }

  @Test
  void testAnnotationChangeDialogCancel() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("Cancel").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertEquals(NAMESPACE + LEMMA_NAME, table.getCellDataValueByPosition(0, 2));
  }

  /**
   * The popup menus for "Delete cell(s)" and "Change annotation name" should only be visible when
   * no token cells are selected. This tests whether the menu is hidden with a mixed selection of
   * token and non-token cells.
   */
  @Test
  void testMenusVisibleOnNoTokenSelection() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(1, 1);
    shiftClick(table, 5, 1);
    ctrlClick(table, 2, 3);
    ctrlClick(table, 4, 3);

    List<String> column0HeaderMenuItems = table.contextMenu(0, 0).menuItems();
    assertFalse(
        column0HeaderMenuItems.contains(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL));
    assertFalse(column0HeaderMenuItems.contains(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL));
  }

  /**
   * Tests whether changing the annotation name registers as making the project saveable.
   */
  @Test
  void testAnnotationNameChangeRegistered() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertTrue(projectManager.isDirty());
    // Also check that the undo toolbar item has been enabled
    assertTrue(bot.toolbarButtonWithTooltip("Undo (Ctrl+Z)").isEnabled());
    assertFalse(bot.toolbarButtonWithTooltip("Redo (Shift+Ctrl+Z)").isEnabled());
  }

  /**
   * Test that annotation name editing is cancelled when the dialog is closed.
   */
  @Test
  void testAnnotationFormClosed() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals(NAMESPACED_LEMMA_NAME, table.getCellDataValueByPosition(0, 2));
    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    dialog.close();
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertTrue(dialog.widget.isDisposed());
    assertEquals(NAMESPACED_LEMMA_NAME, table.getCellDataValueByPosition(0, 2));
  }

  /**
   * Tests that for overlapping columns (whose cells share the same annotation name), a change of
   * annotation in one column also changes the name in the other columns.
   */
  @Test
  void testChangeAnnotationNamesForOverlappingColumns() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Check number of rows and columns (5 + 1 header row, 5 + 1 header column)
    assertEquals(6, natTable.getRowCount());
    assertEquals(6, natTable.getColumnCount());

    // Test headers
    assertEquals(FIVE + SPAN_1, table.getCellDataValueByPosition(0, 3));
    assertEquals(FIVE + SPAN_1 + " (2)", table.getCellDataValueByPosition(0, 4));
    assertEquals(FIVE + SPAN_2, table.getCellDataValueByPosition(0, 5));

    // Change annotation name in first overlapping column (currently at position 3)
    table.contextMenu(0, 3).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have changed
    assertEquals(FIVE + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 3));
    assertEquals(FIVE + TEST_ANNOTATION_VALUE + " (2)", table.getCellDataValueByPosition(0, 4));
    assertEquals(FIVE + SPAN_2, table.getCellDataValueByPosition(0, 5));
    // Also check if annotation name has changed for all cells in column
    for (int i = 1; i < 11; i++) {
      Object nodeObjFirstColumn = table.widget.getDataValueByPosition(3, i);
      if (nodeObjFirstColumn != null) {
        assertNotNull(((SSpan) nodeObjFirstColumn).getAnnotation(FIVE + TEST_ANNOTATION_VALUE));
      }
      Object nodeObjSecondColumn = table.widget.getDataValueByPosition(4, i);
      if (nodeObjSecondColumn != null) {
        assertNotNull(((SSpan) nodeObjSecondColumn).getAnnotation(FIVE + TEST_ANNOTATION_VALUE));
      }
    }
  }

  /**
   * Tests that when a single cell is selected and its annotation name changed, that a new column
   * for the new annotation name is created, and that the cell is moved into that column.
   * 
   * @throws Exception if the thread extracting the information from a given dialog is interrupted,
   *         or if there is an error during execution. Can be either {@link InterruptedException} or
   *         {@link ExecutionException}.
   */
  @Test
  void testChangeAnnotationNameSingleCell() throws InterruptedException, ExecutionException {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertTrue(table.widget.getDataValueByPosition(2, 3) instanceof SToken);
    SToken token = (SToken) table.widget.getDataValueByPosition(2, 3);
    assertEquals(EXAMPLE_VALUE,
        token.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    // Select and change name
    table.click(3, 2);
    table.contextMenu(3, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    assertNotNull(dialog);
    // Check that the fields are pre-filled
    assertDialogTexts(dialog, SaltUtil.SALT_NAMESPACE + SaltUtil.NAMESPACE_SEPERATOR + LEMMA_NAME);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have changed
    assertEquals(NAMESPACE + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 2));
    assertEquals(NAMESPACED_LEMMA_NAME, table.getCellDataValueByPosition(0, 3));
    assertTrue(table.widget.getDataValueByPosition(2, 3) instanceof SToken);
    // Old cell should now be null, old column has been pushed from col position 2 to 3
    assertNull(table.widget.getDataValueByPosition(3, 3));
    // token should be the same as before
    assertEquals(token, table.widget.getDataValueByPosition(2, 3));
    assertEquals(EXAMPLE_VALUE,
        token.getAnnotation(SaltUtil.SALT_NAMESPACE, TEST_ANNOTATION_VALUE).getValue());
  }

  /**
   * Tests that when a single cell is selected and the annotation rename dialog is cancelled, that
   * everything has stayed the same.
   * 
   * @throws Exception if the thread extracting the information from a given dialog is interrupted,
   *         or if there is an error during execution. Can be either {@link InterruptedException} or
   *         {@link ExecutionException}.
   */
  @Test
  void testCancelChangeAnnotationNameSingleCell() throws InterruptedException, ExecutionException {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertTrue(table.widget.getDataValueByPosition(2, 3) instanceof SToken);
    SToken token = (SToken) table.widget.getDataValueByPosition(2, 3);
    assertEquals(EXAMPLE_VALUE,
        token.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    // Select and change name
    table.click(3, 2);
    table.contextMenu(3, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    assertNotNull(dialog);
    // Check that the fields are pre-filled
    assertDialogTexts(dialog, SaltUtil.SALT_NAMESPACE + SaltUtil.NAMESPACE_SEPERATOR + LEMMA_NAME);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("Cancel").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have not changed
    assertEquals(token, table.widget.getDataValueByPosition(2, 3));
    token = (SToken) table.widget.getDataValueByPosition(2, 3);
    assertEquals(EXAMPLE_VALUE,
        token.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());

  }


  /**
   * Tests that when multiple cells in the same column are selected, that the annotation renaming
   * works for all of these cells.
   * 
   * @throws Exception if the thread extracting the information from a given dialog is interrupted,
   *         or if there is an error during execution. Can be either {@link InterruptedException} or
   *         {@link ExecutionException}.
   */
  @Test
  void testChangeAnnotationNameMultipleCellsInOneColumn()
      throws InterruptedException, ExecutionException {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    // Assert model elements
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    SToken token1 = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE, token1.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    assertTrue(table.widget.getDataValueByPosition(2, 5) instanceof SToken);
    SToken token2 = (SToken) table.widget.getDataValueByPosition(2, 5);
    assertEquals(COMPLICATED_VALUE,
        token2.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    assertTrue(table.widget.getDataValueByPosition(2, 7) instanceof SToken);
    SToken token3 = (SToken) table.widget.getDataValueByPosition(2, 7);
    assertEquals(IT_VALUE, token3.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    // Select and change name of lemma annotations
    NatTable natTable = table.widget;
    Display.getDefault().syncExec(() -> {
      // Coordinates are offset by -1 as header columns and rows are not within the body layer, but
      // within the table widget.
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 1, 3, false, false));
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 1, 4, false, true));
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 1, 6, false, true));
    });

    table.contextMenu(3, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    assertNotNull(dialog);
    // Check that the fields are pre-filled
    assertDialogTexts(dialog, NAMESPACED_LEMMA_NAME);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have changed
    assertEquals("salt::" + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 2));
    assertEquals(NAMESPACED_LEMMA_NAME, table.getCellDataValueByPosition(0, 3));
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    assertTrue(table.widget.getDataValueByPosition(2, 5) instanceof SToken);
    assertTrue(table.widget.getDataValueByPosition(2, 7) instanceof SToken);
    // Old cell should now be null, old column has been pushed from col position 2 to 3
    assertNull(table.widget.getDataValueByPosition(3, 4));
    assertNull(table.widget.getDataValueByPosition(3, 5));
    assertNull(table.widget.getDataValueByPosition(3, 7));
    // Tokens should be the same as before
    assertEquals(token1, table.widget.getDataValueByPosition(2, 4));
    assertEquals(MORE_VALUE,
        token1.getAnnotation(SaltUtil.SALT_NAMESPACE, TEST_ANNOTATION_VALUE).getValue());
    assertEquals(token2, table.widget.getDataValueByPosition(2, 5));
    assertEquals(COMPLICATED_VALUE,
        token2.getAnnotation(SaltUtil.SALT_NAMESPACE, TEST_ANNOTATION_VALUE).getValue());
    assertEquals(token3, table.widget.getDataValueByPosition(2, 7));
    assertEquals(IT_VALUE,
        token3.getAnnotation(SaltUtil.SALT_NAMESPACE, TEST_ANNOTATION_VALUE).getValue());
  }

  /**
   * Tests that when multiple cells from multiple different columns are selected, that the
   * annotation renaming works for all of these cells.
   * 
   * @throws Exception if the thread extracting the information from a given dialog is interrupted,
   *         or if there is an error during execution. Can be either {@link InterruptedException} or
   *         {@link ExecutionException}.
   */
  @Test
  void testChangeAnnotationNameMultipleCellsInDifferentColumns()
      throws InterruptedException, ExecutionException {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    // Assert model elements
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    SToken lemmaToken = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE,
        lemmaToken.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());
    assertTrue(table.widget.getDataValueByPosition(3, 5) instanceof SToken);
    SToken posToken = (SToken) table.widget.getDataValueByPosition(3, 5);
    assertEquals(JJ_VALUE, posToken.getAnnotation(SaltUtil.SALT_NAMESPACE, POS_NAME).getValue());
    assertTrue(table.widget.getDataValueByPosition(4, 1) instanceof SSpan);
    SSpan infSpan = (SSpan) table.widget.getDataValueByPosition(4, 1);
    assertEquals(CONTRAST_FOCUS_VALUE, infSpan.getAnnotation(null, INF_STRUCT_NAME).getValue());
    // Select and change name of lemma annotations
    NatTable natTable = table.widget;
    Display.getDefault().asyncExec(() -> {
      // Coordinates are offset by -1 as header columns and rows are not within the body layer, but
      // within the table widget.
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 1, 3, false, false));
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 2, 4, false, true));
      natTable.doCommand(new SelectCellCommand(getBodyLayer(table), 3, 0, false, true));
    });
    table.contextMenu(3, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    assertNotNull(dialog);
    // Check that the fields are pre-filled
    assertDialogTexts(dialog, null);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have changed, the span annotation is added to a second new column
    // with the name TEST, as columns are specific to model element types.
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 2));
    assertEquals(NAMESPACED_LEMMA_NAME, table.getCellDataValueByPosition(0, 3));
    assertEquals(SaltUtil.SALT_NAMESPACE + SaltUtil.NAMESPACE_SEPERATOR + POS_NAME,
        table.getCellDataValueByPosition(0, 4));
    assertEquals(INF_STRUCT_NAME, table.getCellDataValueByPosition(0, 5));
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 6));
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    assertTrue(table.widget.getDataValueByPosition(2, 5) instanceof SToken);
    assertTrue(table.widget.getDataValueByPosition(6, 1) instanceof SSpan);
    // Old cells should now be null
    assertNull(table.widget.getDataValueByPosition(3, 4));
    assertNull(table.widget.getDataValueByPosition(4, 5));
    assertNull(table.widget.getDataValueByPosition(5, 1));
    // Model elements should be the same as before
    assertEquals(lemmaToken, table.widget.getDataValueByPosition(2, 4));
    assertEquals(MORE_VALUE, lemmaToken.getAnnotation(TEST_ANNOTATION_VALUE).getValue());
    assertEquals(posToken, table.widget.getDataValueByPosition(2, 5));
    assertEquals(JJ_VALUE, posToken.getAnnotation(TEST_ANNOTATION_VALUE).getValue());
    assertEquals(infSpan, table.widget.getDataValueByPosition(6, 1));
    assertEquals(CONTRAST_FOCUS_VALUE, infSpan.getAnnotation(TEST_ANNOTATION_VALUE).getValue());
  }

  /**
   * Tests that when annotations with the qualified target annotation name already exist on a node
   * during a rename action, the cells and nodes remain unchanged, and a dialog is displayed
   * notifying the user of these unchanged annotations.
   * 
   * @throws InterruptedException If the thread extracting the label text in the given dialog is
   *         interrupted, or if there is an error during execution.
   * @throws ExecutionException see InterruptedException
   */
  @Test
  void testAnnotationsRemainUnchanged() throws InterruptedException, ExecutionException {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    // Assert model elements
    assertEquals(5, table.columnCount());
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    SToken lemmaToken = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE,
        lemmaToken.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());

    // Start renaming action to existing annotation
    table.click(4, 2);
    table.contextMenu(4, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(POS_NAME);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));

    // Assert model elements unchanged
    assertEquals(5, table.columnCount());
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    lemmaToken = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE,
        lemmaToken.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());

    // Assert that dialog is displayed
    SWTBotShell infoDialog = tableBot.shell(UNRENAMED_ANNOTATIONS_DIALOG_TITLE);
    assertNotNull(infoDialog);
    // Check that the displayed text is correct
    LabelTextExtractor uq = new LabelTextExtractor(infoDialog);
    FutureTask<String> labelTextFuture = new FutureTask<>(uq);
    Display.getDefault().syncExec(labelTextFuture);
    try {
      assertEquals(
          "Could not rename some annotations, as annotations with the qualified target name 'pos'"
              + " already exist on the respective nodes:\n- Token with text 'more'",
          labelTextFuture.get());
    } catch (InterruptedException | ExecutionException e) {
      fail(e);
      throw e;
    }
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(infoDialog));

  }

  /**
   * Tests that when during a renaming action for annotations the current qualified annotation name
   * and the new one are the same, that no information dialog is presented to the user.
   */
  @Test
  void testDialogNotDisplayedOnSameQNameValues() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    // Assert model elements
    assertEquals(5, table.columnCount());
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    SToken lemmaToken = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE,
        lemmaToken.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());

    // Start renaming action to existing annotation
    table.click(4, 2);
    table.contextMenu(4, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(LEMMA_NAME);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));

    // Assert model elements unchanged
    assertEquals(5, table.columnCount());
    assertTrue(table.widget.getDataValueByPosition(2, 4) instanceof SToken);
    lemmaToken = (SToken) table.widget.getDataValueByPosition(2, 4);
    assertEquals(MORE_VALUE,
        lemmaToken.getAnnotation(SaltUtil.SALT_NAMESPACE, LEMMA_NAME).getValue());

    // Assert that dialog is NOT displayed
    Display.getDefault().syncExec(() -> {
      Shell[] shells = Display.getDefault().getShells();
      for (int i = 0; i < shells.length; i++) {
        assertNotEquals(UNRENAMED_ANNOTATIONS_DIALOG_TITLE, shells[i].getText());
      }
    });
  }


  /**
   * Tests the creation of spans over continuous empty cells in an existing span column.
   */
  @Test
  void testCreateContinuousSpan() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Remove large existing span
    assertEquals(TOPIC_VALUE, ((SAnnotationContainer) natTable.getDataValueByPosition(4, 2))
        .getAnnotation(INF_STRUCT_NAME).getValue());
    table.click(2, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 2, 4, ""));
    assertNull(natTable.getDataValueByPosition(4, 2));

    // Select cells for new span
    table.click(3, 4);
    shiftClick(table, 7, 4);

    // Create span and assert
    List<String> contextMenuItems = table.contextMenu(5, 4).menuItems();
    assertTrue(contextMenuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
    table.contextMenu(5, 4).contextMenu(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL).click();
    typeTextPressReturn(table);
    assertNull(natTable.getDataValueByPosition(4, 2));
    Object potentialSpan = natTable.getDataValueByPosition(4, 3);
    assertTrue(potentialSpan instanceof SSpan);
    SSpan span = (SSpan) potentialSpan;
    assertEquals(span, natTable.getDataValueByPosition(4, 4));
    assertEquals(span, natTable.getDataValueByPosition(4, 5));
    assertEquals(span, natTable.getDataValueByPosition(4, 6));
    assertEquals(span, natTable.getDataValueByPosition(4, 7));
    assertNull(natTable.getDataValueByPosition(4, 8));
    assertEquals(TEST_ANNOTATION_VALUE, span.getAnnotation(INF_STRUCT_NAME).getValue());
  }

  /**
   * Tests the creation of spans over discontinuous empty cells in an existing span column.
   */
  @Test
  void testCreateDiscontinuousSpan() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Remove large existing span
    assertEquals(TOPIC_VALUE, ((SAnnotationContainer) natTable.getDataValueByPosition(4, 2))
        .getAnnotation(INF_STRUCT_NAME).getValue());
    table.click(2, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 2, 4, ""));
    assertNull(natTable.getDataValueByPosition(4, 2));

    // Select cells for new span
    table.click(3, 4);
    shiftClick(table, 5, 4);
    ctrlClick(table, 7, 4);
    ctrlClick(table, 9, 4);
    ctrlClick(table, 10, 4);

    // Create span and assert
    List<String> contextMenuItems = table.contextMenu(5, 4).menuItems();
    assertTrue(contextMenuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
    table.contextMenu(5, 4).contextMenu(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL).click();
    typeTextPressReturn(table);
    assertNull(natTable.getDataValueByPosition(4, 2));
    Object potentialSpan = natTable.getDataValueByPosition(4, 3);
    assertTrue(potentialSpan instanceof SSpan);
    SSpan span = (SSpan) potentialSpan;
    assertEquals(span, natTable.getDataValueByPosition(4, 4));
    assertEquals(span, natTable.getDataValueByPosition(4, 5));
    assertNull(natTable.getDataValueByPosition(4, 6));
    assertEquals(span, natTable.getDataValueByPosition(4, 7));
    assertNull(natTable.getDataValueByPosition(4, 8));
    assertEquals(span, natTable.getDataValueByPosition(4, 9));
    assertEquals(span, natTable.getDataValueByPosition(4, 10));
    assertEquals(TEST_ANNOTATION_VALUE, span.getAnnotation(INF_STRUCT_NAME).getValue());
  }

  /**
   * Tests the creation of spans over discontinuous empty cells in an existing span column.
   */
  @Test
  void testCreateSingleCellSpan() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Remove existing single cell span
    assertEquals(CONTRAST_FOCUS_VALUE,
        ((SAnnotationContainer) natTable.getDataValueByPosition(4, 1))
            .getAnnotation(INF_STRUCT_NAME).getValue());
    table.click(1, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);
    bot.waitUntil(new CellDataValueCondition(tableBot, 1, 4, ""));
    assertNull(natTable.getDataValueByPosition(4, 1));

    // Select single cell for new span
    table.click(1, 4);

    // Create span and assert
    List<String> contextMenuItems = table.contextMenu(1, 4).menuItems();
    assertTrue(contextMenuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
    table.contextMenu(1, 4).contextMenu(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL).click();
    typeTextPressReturn(table);
    Object potentialSpan = natTable.getDataValueByPosition(4, 1);
    assertTrue(potentialSpan instanceof SSpan);
    SSpan span = (SSpan) potentialSpan;
    assertEquals(TEST_ANNOTATION_VALUE, span.getAnnotation(INF_STRUCT_NAME).getValue());
  }

  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/252.
   */
  @Test
  void testPositionResolvedCorrectly() {
    openScrollingExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    Position pos = table.scrollViewport(new Position(1, 1), 1, 10);
    table.click(pos.row, pos.column);
    try {
      SWTBotRootMenu menu = table.contextMenu(pos.row, pos.column);
      List<String> menuItems = menu.menuItems();
      assertFalse(menuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
      SWTBotMenu contextMenu = menu.contextMenu(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL);
      assertNotNull(contextMenu);
      contextMenu.click();
      menu = table.contextMenu(pos.row, pos.column);
      menuItems = menu.menuItems();
      assertTrue(menuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
    } catch (WidgetNotFoundException e) {
      fail(e);
    }
  }

  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/256.
   */
  @Test
  void testFixScrolledCellMenuThrowsIndexOutOfBoundsException() {
    openScrollingExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    Position pos = table.scrollViewport(new Position(1, 1), 1, 10);
    table.click(pos.row, pos.column);
    // Make sure that the position we're checking is the correct one
    assertEquals("NodeNotifierImpl(salt:/corpus/doc#sSpan10)[anno9=value]], salt::SNAME=sSpan10]",
        table.getCellDataValueByPosition(pos));
    List<String> menuItems = table.contextMenu(pos.row, pos.column).menuItems();
    // If #256 is fixed, the "Create span" menu item will not be present, as the respective
    // selection state validation will not have thrown an IndexOutofBoundsException
    assertFalse(menuItems.contains(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL));
  }

  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/257.
   * 
   * @throws InterruptedException If the thread extracting the label text in the given dialog is
   *         interrupted, or if there is an error during execution.
   * @throws ExecutionException see InterruptedException
   */
  @Test
  void testFixOldQualifiedNameNotUsed() throws InterruptedException, ExecutionException {
    openScrollingExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    Position pos = table.scrollViewport(new Position(1, 1), 1, 10);
    table.click(pos.row, pos.column);
    // Make sure that the position we're checking is the correct one
    assertEquals("NodeNotifierImpl(salt:/corpus/doc#sSpan10)[anno9=value]], salt::SNAME=sSpan10]",
        table.getCellDataValueByPosition(pos));
    table.contextMenu(pos.row, pos.column)
        .contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    bot.waitUntil(Conditions.shellIsActive(RENAME_DIALOG_TITLE));
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    assertNotNull(dialog);
    assertDialogTexts(dialog, "anno9");
  }
  
  /**
   * Regression test for https://github.com/hexatomic/hexatomic/issues/258:
   * Deleting all annotations should remove all visual columns.
   */
  @Test
  void testRemoveVisualColumnsOnAnnotationsDeleted() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    // Select all annotations
    table.click(1, 2);
    shiftClick(table, 5, 5);
    // Delete all annotations
    keyboard.pressShortcut(Keystrokes.DELETE);
    table.click(1, 1);
    
    assertEquals(6, table.widget.getRowCount());
    assertEquals(2, table.widget.getColumnCount());
  }

  // #################### Helper methods ####################

  private void assertDialogTexts(SWTBotShell dialog, String qualifiedName)
      throws InterruptedException, ExecutionException {
    String namespace = null;
    String name = null;
    if (qualifiedName != null) {
      Pair<String, String> namespaceNamePair = SaltUtil.splitQName(qualifiedName);
      String namespaceObject = namespaceNamePair.getLeft();
      String nameObject = namespaceNamePair.getRight();
      namespace = namespaceObject == null ? "" : namespaceObject;
      name = nameObject == null ? "" : nameObject;
    } else {
      namespace = "";
      name = "";
    }
    PanelTextsTextExtractor extractor = new PanelTextsTextExtractor(dialog);
    FutureTask<Pair<String, String>> textTextFuture = new FutureTask<>(extractor);
    Pair<String, String> extractedNamePair = null;
    String extractedNamespace = null;
    String extractedName = null;
    Display.getDefault().syncExec(textTextFuture);
    try {
      extractedNamePair = textTextFuture.get();
      extractedNamespace = extractedNamePair.getLeft();
      extractedName = extractedNamePair.getRight();
    } catch (InterruptedException | ExecutionException e) {
      fail(e);
      throw e;
    }
    assertEquals(namespace, extractedNamespace);
    assertEquals(name, extractedName);
  }


  private void ctrlClick(SWTBotNatTable table, int rowPosition, int columnPosition) {
    clickWithMask(false, true, rowPosition, columnPosition, table);
  }

  private void shiftClick(SWTBotNatTable table, int rowPosition, int columnPosition) {
    clickWithMask(true, false, rowPosition, columnPosition, table);
  }

  private void clickWithMask(boolean shiftMask, boolean ctrlMask, int rowPosition,
      int columnPosition, SWTBotNatTable table) {
    ILayer selectionLayer = getSelectionLayer(table);
    Display.getDefault()
        .syncExec(() -> selectionLayer.doCommand(new SelectCellCommand(selectionLayer,
            columnPosition - 1, rowPosition - 1, shiftMask, ctrlMask)));
  }

  private SelectionLayer getSelectionLayer(SWTBotNatTable table) {
    CompositeFreezeLayer freezeLayer = getBodyLayer(table);
    ILayer freezeLayerUll = freezeLayer.getUnderlyingLayerByPosition(1, 1);
    assertEquals(ViewportLayer.class, freezeLayerUll.getClass());
    ViewportLayer viewPortLayer = (ViewportLayer) freezeLayerUll;

    ILayer viewPortLayerUll = viewPortLayer.getUnderlyingLayerByPosition(1, 1);
    assertEquals(SelectionLayer.class, viewPortLayerUll.getClass());
    return (SelectionLayer) viewPortLayerUll;
  }

  private CompositeFreezeLayer getBodyLayer(SWTBotNatTable table) {
    NatTable nattable = table.widget;

    ILayer layer = nattable.getLayer();

    ILayer layerUl = layer.getUnderlyingLayerByPosition(1, 1);
    assertTrue(layerUl instanceof CompositeFreezeLayer);
    return (CompositeFreezeLayer) layerUl;
  }

  /**
   * A {@link Callable} which, when called, extracts the text of the first two {@link Text} fields
   * it finds in the first child of type {@link AbstractEditorPanel} in the given
   * {@link SWTBotShell} and returns them as {@link Pair}.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private class PanelTextsTextExtractor implements Callable<Pair<String, String>> {

    private final SWTBotShell dialog;

    PanelTextsTextExtractor(SWTBotShell dialog) {
      this.dialog = dialog;
    }

    @Override
    public Pair<String, String> call() throws Exception {
      Control[] children = dialog.widget.getChildren();
      String namespace = null;
      String name = null;
      boolean checkedFirstText = false;
      // Constraint: There may only be one panel in the dialog, and it must contain two texts
      AbstractEditorPanel<?> panel = findFirstPanel(children);
      if (panel == null) {
        fail("AbstractEditorPanel not found but should be there!");
        return null;
      }
      Control[] panelChildren = panel.getChildren();
      for (int i = 0; i < panelChildren.length; i++) {
        // Iterate through the children tree until we find the AnnotationLabelPanel
        Control child = panelChildren[i];
        if (child instanceof Text) {
          if (!checkedFirstText) {
            namespace = ((Text) child).getText();
            checkedFirstText = true;
          } else {
            name = ((Text) child).getText();
          }
        }
      }
      return Pair.of(namespace, name);
    }

    private AbstractEditorPanel<?> findFirstPanel(Control[] children) {
      for (int i = 0; i < children.length; i++) {
        Control control = children[i];
        if (control instanceof Composite) {
          // Can potentially contain the panel we're looking for
          for (Control compositeChild : ((Composite) control).getChildren()) {
            if (compositeChild instanceof AbstractEditorPanel<?>) {
              return (AbstractEditorPanel<?>) compositeChild;
            }
          }
        }
      }
      return null;
    }
  }

  /**
   * A {@link Callable} which, when called, extracts the text of the first non-empty {@link Label}
   * it finds in the given {@link SWTBotShell} and returns it.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private class LabelTextExtractor implements Callable<String> {

    private final SWTBotShell dialog;

    LabelTextExtractor(SWTBotShell infoDialog) {
      this.dialog = infoDialog;
    }

    public String call() throws Exception {
      for (Control child : dialog.widget.getChildren()) {
        if (child instanceof Label && !((Label) child).getText().isEmpty()) {
          Label label = (Label) child;
          return label.getText();
        }
      }
      return null;
    }
  }


}
