package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
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

  private static final String TEST_ANNOTATION_VALUE = "TEST";

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  private URI exampleProjectUri;
  private URI overlappingExampleProjectUri;
  private URI twoDsExampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ErrorService errorService = new ErrorService();

  private final Keyboard keyboard = KeyboardFactory.getAWTKeyboard();

  @BeforeEach
  void setup() {
    IEclipseContext ctx = ContextHelper.getEclipseContext();

    ctx.set(ErrorService.class, errorService);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

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
    assertNotNull(docMenu.contextMenu("Open with Grid Editor").click());

    SWTBotView view = bot.partByTitle("doc1 (Grid Editor)");
    assertNotNull(view);

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
    assertNotNull(docMenu.contextMenu("Open with Grid Editor").click());

    SWTBotView view = bot.partByTitle("doc (Grid Editor)");
    assertNotNull(view);

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
    assertNotNull(docMenu.contextMenu("Open with Grid Editor").click());

    SWTBotView view = bot.partByTitle("doc (Grid Editor)");
    assertNotNull(view);

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

    NatTable table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    // Check number of rows and columns (11 + 1 header row, 4 + 1 header column)
    assertEquals(12, table.getRowCount());
    assertEquals(5, table.getColumnCount());

    // Test headers
    assertEquals(null, table.getDataValueByPosition(0, 0));
    assertEquals(2, table.getDataValueByPosition(0, 2));
    assertEquals(11, table.getDataValueByPosition(0, 11));
    assertEquals("Token", table.getDataValueByPosition(1, 0));
    assertEquals("Inf-Struct", table.getDataValueByPosition(4, 0));

    // Test cells
    assertEquals("Is", table.getDataValueByPosition(1, 1));
    assertEquals("?", table.getDataValueByPosition(1, 11));
    assertEquals("be", table.getDataValueByPosition(2, 1));
    assertEquals("contrast-focus", table.getDataValueByPosition(4, 1));
    assertEquals("topic", table.getDataValueByPosition(4, 2));
    assertEquals("topic", table.getDataValueByPosition(4, 11));
  }

  @Test
  void testShowOverlapSaltExample() {
    openOverlapExample();

    NatTable table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    // Need to maximize the window so that all columns are displayed,
    // as NatTable knows only about completely displayed columns in the virtual table
    bot.activeShell().maximize(true);

    // Check number of rows and columns (5 + 1 header row, 5 + 1 header column)
    assertEquals(6, table.getRowCount());
    assertEquals(6, table.getColumnCount());
    //
    // // Test headers
    assertEquals(null, table.getDataValueByPosition(0, 0));
    assertEquals(2, table.getDataValueByPosition(0, 2));
    assertEquals(5, table.getDataValueByPosition(0, 5));
    assertEquals("Token", table.getDataValueByPosition(1, 0));
    assertEquals("five::span_1", table.getDataValueByPosition(3, 0));
    assertEquals("five::span_1 (2)", table.getDataValueByPosition(4, 0));
    assertEquals("five::span_2", table.getDataValueByPosition(5, 0));
    //
    // // Test cells
    assertEquals("val_span_1", table.getDataValueByPosition(3, 1));
    assertEquals("val_span_2", table.getDataValueByPosition(3, 2));
    assertEquals("val_span_2", table.getDataValueByPosition(3, 5));
    assertEquals("val_span_3", table.getDataValueByPosition(4, 1));
    assertEquals("val_span_3", table.getDataValueByPosition(4, 2));
    assertEquals(null, table.getDataValueByPosition(5, 1));

    // Un-maximize to reset to original dimensions
    bot.activeShell().maximize(false);
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
    assertEquals("pony", table.getDataValueByPosition(1, 5));
    assertEquals("one::span", table.getDataValueByPosition(2, 0));

    // Select second text
    combo.setSelection(1);
    table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    assertEquals(3, table.getColumnCount());
    assertEquals(5, table.getRowCount());
    assertEquals("annotations", table.getDataValueByPosition(1, 3));
    assertEquals("four::token", table.getDataValueByPosition(2, 0));

    // Select first text again
    combo.setSelection(0);
    table = bot.widget(widgetOfType(NatTable.class));
    assertNotNull(table);

    assertEquals(3, table.getColumnCount());
    assertEquals(7, table.getRowCount());
    assertEquals("pony", table.getDataValueByPosition(1, 5));
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
    assertEquals("Token", table.getCellDataValueByPosition(0, 1));

    // Hide token column
    table.contextMenu(0, 1).contextMenu("Hide column(s)").click();
    assertEquals("salt::lemma", table.getCellDataValueByPosition(0, 1));

    // Show columns
    table.contextMenu(0, 1).contextMenu("Show all columns").click();
    assertEquals("Token", table.getCellDataValueByPosition(0, 1));
  }

  @Test
  void testEmptyCellStyleApplied() {
    bot.activeShell().maximize(true);
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(1, 5));
    assertTrue(table.hasConfigLabel(1, 5, StyleConfiguration.EMPTY_CELL_STYLE));
    bot.activeShell().maximize(false);
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
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(2, 2));
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
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(2, 2));
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
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(2, 2));
  }

  @Test
  void testCreateAnnotationOnEmptySpanCell() {
    openOverlapExample();

    // Need to maximize the window so that all columns are displayed,
    // as NatTable knows only about completely displayed columns in the virtual table
    bot.activeShell().maximize(true);

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(1, 5));
    table.click(1, 5);
    typeTextPressReturn(table);
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(1, 5));

    bot.activeShell().maximize(false);
  }

  @Test
  void testCreateAnnotationOnEmptyTokenCell() {
    openOverlapExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    assertEquals("", table.getCellDataValueByPosition(2, 2));
    table.click(2, 2);
    typeTextPressReturn(table);
    assertEquals(TEST_ANNOTATION_VALUE, table.getCellDataValueByPosition(2, 2));

  }

  /**
   * Types the value of TEST_ANNOTATION_VALUE, then Return, then waits until the table has no active
   * cell editors, up to 1000ms.
   * 
   * @param table The {@link NatTable} to operate on
   * @throws TimeoutException after 1000ms without returning successfully
   */
  private void typeTextPressReturn(SWTBotNatTable table) throws TimeoutException {
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

  @Test
  void testRemoveSingleAnnotationByDelKey() {
    openDefaultExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();

    table.click(1, 4);
    keyboard.pressShortcut(Keystrokes.DELETE);

    assertEquals(null, table.getCellDataValueByPosition(1, 4));
  }

  @Test
  void testRemoveMultipleAnnotationsByDelKey() {
    // TODO Implement once this can be tested!
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=563300
  }

}
