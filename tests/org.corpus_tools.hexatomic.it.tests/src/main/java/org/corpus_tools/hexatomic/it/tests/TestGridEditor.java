package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.grid.GridEditor;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
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
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
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

  private static final String TOKEN_VALUE = "Token";

  private static final String OPEN_WITH_GRID_EDITOR = "Open with Grid Editor";

  private static final String TEST_ANNOTATION_VALUE = "TEST";

  private static final String NAMESPACE = SaltUtil.SALT_NAMESPACE + SaltUtil.NAMESPACE_SEPERATOR;

  private static final String RENAME_DIALOG_TITLE = "Rename annotation";
  
  private static final String SPAN_1 = "span_1";
  
  private static final String FIVE = "five::";

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private URI overlappingExampleProjectUri;
  private URI twoDsExampleProjectUri;
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

    File overlappingExampleProjectDirectory = new File("../org.corpus_tools.hexatomic.grid.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/grid/overlapping-spans/");
    assertTrue(overlappingExampleProjectDirectory.isDirectory());

    File twoDsExampleProjectDirectory = new File("../org.corpus_tools.hexatomic.grid.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/grid/two-ds/");
    assertTrue(twoDsExampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
    overlappingExampleProjectUri =
        URI.createFileURI(overlappingExampleProjectDirectory.getAbsolutePath());
    twoDsExampleProjectUri = URI.createFileURI(twoDsExampleProjectDirectory.getAbsolutePath());
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
    // TODO: when close project is implemented with save functionality, change this to close the
    // project and its editors

  }

  SWTBotView openDefaultExample() {
    // Programmatically open the example corpus
    openExample(exampleProjectUri);
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

  SWTBotView openOverlapExample() {
    // Programmatically open the example corpus
    openExample(overlappingExampleProjectUri);
    // Select the first example document
    SWTBotTreeItem docMenu =
        bot.tree().expandNode("corpus-graph").expandNode("corpus").expandNode("doc");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    final SWTBotView view = bot.partByTitle("doc (Grid Editor)");
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
    SWTBotTreeItem docMenu =
        bot.tree().expandNode("<unknown>").expandNode("corpus").expandNode("doc");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_GRID_EDITOR).click());

    SWTBotView view = bot.partByTitle("doc (Grid Editor)");
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
    assertEquals("Inf-Struct", table.getCellDataValueByPosition(0, 4));
    assertEquals("Inf-Struct", natTable.getDataValueByPosition(4, 0));

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
    assertEquals("contrast-focus", ((SAnnotationContainer) natTable.getDataValueByPosition(4, 1))
        .getAnnotation(infStructHeader).getValue());
    assertEquals("topic", ((SAnnotationContainer) natTable.getDataValueByPosition(4, 2))
        .getAnnotation(infStructHeader).getValue());
    assertEquals("topic", ((SAnnotationContainer) natTable.getDataValueByPosition(4, 11))
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
    assertEquals(FIVE + "span_2", table.getCellDataValueByPosition(0, 5));

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

    assertEquals("sText1", combo.getText());
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
    assertEquals("sText1", combo.getText());

    combo.pressShortcut(KeyStroke.getInstance(SWT.ARROW_DOWN));
    assertEquals("Token annotations only", combo.getText());
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
    assertEquals(NAMESPACE + "lemma", table.getCellDataValueByPosition(0, 1));

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
    assertEquals(NAMESPACE + "lemma", table.getCellDataValueByPosition(0, 2));
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

    assertEquals("salt::lemma", table.getCellDataValueByPosition(0, 2));
    table.contextMenu(0, 2).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    dialog.close();
    bot.waitUntil(Conditions.shellCloses(dialog));
    assertTrue(dialog.widget.isDisposed());
    assertEquals("salt::lemma", table.getCellDataValueByPosition(0, 2));
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
    assertEquals(FIVE + "span_2", table.getCellDataValueByPosition(0, 5));

    // Change annotation name in first overlapping column (currently at position 3)
    table.contextMenu(0, 3).contextMenu(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL).click();
    SWTBotShell dialog = tableBot.shell(RENAME_DIALOG_TITLE);
    keyboard.typeText(TEST_ANNOTATION_VALUE);
    tableBot.button("OK").click();
    bot.waitUntil(Conditions.shellCloses(dialog));
    // Assert names and positions have changed
    assertEquals(FIVE + TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(0, 3));
    assertEquals(FIVE + TEST_ANNOTATION_VALUE + " (2)", table.getCellDataValueByPosition(0, 4));
    assertEquals(FIVE + "span_2", table.getCellDataValueByPosition(0, 5));
    // Also check if annotation name has changed for all cells in column
    for (int i = 1; i < 11; i++) {
      Object nodeObjFirstColumn = table.widget.getDataValueByPosition(3, i);
      if (nodeObjFirstColumn != null) {
        assertNotNull(((SSpan) nodeObjFirstColumn).getAnnotation(FIVE + TEST_ANNOTATION_VALUE));
      }
      Object nodeObjSecondColumn = table.widget.getDataValueByPosition(4, i);
      if (nodeObjSecondColumn != null) {
        assertNotNull(
            ((SSpan) nodeObjSecondColumn).getAnnotation(FIVE + TEST_ANNOTATION_VALUE));
      }
    }



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

  private ILayer getSelectionLayer(SWTBotNatTable table) {
    NatTable nattable = table.widget;

    // Loop through the layers in the stack until the selection layer is hit
    ILayer layer = nattable.getLayer();

    ILayer layerUl = layer.getUnderlyingLayerByPosition(1, 1);
    assertEquals(CompositeFreezeLayer.class, layerUl.getClass());
    CompositeFreezeLayer freezeLayer = (CompositeFreezeLayer) layerUl;

    ILayer freezeLayerUll = freezeLayer.getUnderlyingLayerByPosition(1, 1);
    assertEquals(ViewportLayer.class, freezeLayerUll.getClass());
    ViewportLayer viewPortLayer = (ViewportLayer) freezeLayerUll;

    ILayer viewPortLayerUll = viewPortLayer.getUnderlyingLayerByPosition(1, 1);
    assertEquals(SelectionLayer.class, viewPortLayerUll.getClass());
    return viewPortLayerUll;
  }


}
