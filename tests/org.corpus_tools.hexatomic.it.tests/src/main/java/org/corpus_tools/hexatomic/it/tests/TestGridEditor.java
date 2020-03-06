package org.corpus_tools.hexatomic.it.tests;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltProjectHandler;
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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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

  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  private URI exampleProjectUri;
  private URI overlappingExampleProjectUri;
  private URI twoDsExampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ErrorService errorService = new ErrorService();



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
    params.put(OpenSaltProjectHandler.COMMAND_PARAM_LOCATION_ID, exampleUri.toFileString());
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

    // Check number of rows and columns (5 + 1 header row, 5 + 1 header column)
    assertEquals(6, table.getRowCount());
    assertEquals(6, table.getColumnCount());

    // Test headers
    assertEquals(null, table.getDataValueByPosition(0, 0));
    assertEquals(2, table.getDataValueByPosition(0, 2));
    assertEquals(5, table.getDataValueByPosition(0, 5));
    assertEquals("Token", table.getDataValueByPosition(1, 0));
    assertEquals("five::span_1", table.getDataValueByPosition(3, 0));
    assertEquals("five::span_1 (2)", table.getDataValueByPosition(4, 0));
    assertEquals("five::span_2", table.getDataValueByPosition(5, 0));

    // Test cells
    assertEquals("val_span_1", table.getDataValueByPosition(3, 1));
    assertEquals("val_span_2", table.getDataValueByPosition(3, 2));
    assertEquals("val_span_2", table.getDataValueByPosition(3, 5));
    assertEquals("val_span_3", table.getDataValueByPosition(4, 1));
    assertEquals("val_span_3", table.getDataValueByPosition(4, 2));
    assertEquals(null, table.getDataValueByPosition(5, 1));
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


}
