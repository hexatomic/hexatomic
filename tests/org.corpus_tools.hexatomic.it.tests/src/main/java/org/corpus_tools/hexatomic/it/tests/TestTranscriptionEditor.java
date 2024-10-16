package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Integration tests for the transcription editor.
 * 
 * @author Thomas Krause (thomas.krause@hu-berlin.de)
 *
 */
@Execution(ExecutionMode.SAME_THREAD)
public class TestTranscriptionEditor {

  private static final String SUB_CORPUS1 = "subCorpus1";
  private static final String ROOT_CORPUS = "rootCorpus";
  private static final String CORPUS_GRAPH1 = "corpusGraph1";
  private static final String CORPUS_STRUCTURE = "Corpus Structure";
  private static final String DOC_TRANSCRIPTION_EDITOR = "doc (Transcription Editor)";
  private static final String DOC = "doc";
  private static final String CORPUS = "corpus";
  private static final String OPEN_WITH_TRANSCRIPTION_EDITOR = "Open with Transcription Editor";


  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private URI twoDsExampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private final Keyboard keyboard = KeyboardFactory.getAWTKeyboard();


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

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());


    File twoDsExampleProjectDirectory = new File("../org.corpus_tools.hexatomic.grid.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/grid/two-ds/");
    assertTrue(twoDsExampleProjectDirectory.isDirectory());


    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
    twoDsExampleProjectUri = URI.createFileURI(twoDsExampleProjectDirectory.getAbsolutePath());
  }

  @AfterEach
  void closeEditor() {
    for (SWTBotView part : bot.parts()) {
      if (part.getTitle().endsWith("(Grid Editor)")) {
        part.close();
      }
    }
  }

  SWTBotView openEditorForDefaultDocument() {

    SWTBotView corpusStructurePart = bot.partByTitle(CORPUS_STRUCTURE);

    // Select the first example document
    SWTBotTreeItem docMenu = corpusStructurePart.bot().tree().expandNode(CORPUS_GRAPH1)
        .expandNode(ROOT_CORPUS).expandNode(SUB_CORPUS1).expandNode("doc2");

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_TRANSCRIPTION_EDITOR).click());

    SWTBotView view = bot.partByTitle("doc2 (Grid Editor)");
    assertNotNull(view);

    // Use all available windows space (the tableToTest needs to be fully visible
    // for some of the
    // tests)
    bot.waitUntil(new PartActiveCondition(view.getPart()));
    view.maximise();
    bot.waitUntil(new PartMaximizedCondition(view.getPart()));

    return view;
  }

  /**
   * Opens the default example corpus document in transcription editor.
   * 
   * <p>
   * 1 token col, 11 rows; 2 token annotation columns, 1 span annotation column (2 spancs: cell 1,
   * cells 2-11)
   * </p>
   */
  SWTBotView openDefaultExample() {
    // Programmatically open the example corpus
    openExample(exampleProjectUri);

    return openEditorForDefaultDocument();
  }


  SWTBotView openTwoDsExample() {
    // Programmatically open the example corpus
    openExample(twoDsExampleProjectUri);
    SWTBotView corpusStructurePart = bot.partByTitle(CORPUS_STRUCTURE);
    // Select the first example document
    SWTBotTreeItem docMenu =
        corpusStructurePart.bot().tree().expandNode("<unknown>").expandNode(CORPUS).expandNode(DOC);

    // select and open the editor
    docMenu.click();
    assertNotNull(docMenu.contextMenu(OPEN_WITH_TRANSCRIPTION_EDITOR).click());

    SWTBotView view = bot.partByTitle(DOC_TRANSCRIPTION_EDITOR);
    assertNotNull(view);

    // Use all available windows space (the tableToTest needs to be fully visible
    // for some of the
    // tests)
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
    bot.partByTitle(CORPUS_STRUCTURE).show();
  }

  @Test
  void testAppendToken() {
    openTwoDsExample();

    SWTNatTableBot tableBot = new SWTNatTableBot();
    SWTBotNatTable table = tableBot.nattable();
    NatTable natTable = table.widget;

    // Check the initial display
    assertEquals(3, natTable.getColumnCount());
    assertEquals(12, natTable.getRowCount());
    assertEquals("TLI", table.getCellDataValueByPosition(0, 0));
    assertEquals("0", table.getCellDataValueByPosition(1, 0));
    assertEquals("1", table.getCellDataValueByPosition(2, 0));
    assertEquals("2", table.getCellDataValueByPosition(3, 0));
    assertEquals("3", table.getCellDataValueByPosition(4, 0));
    assertEquals("4", table.getCellDataValueByPosition(5, 0));
    assertEquals("5", table.getCellDataValueByPosition(6, 0));
    assertEquals("6", table.getCellDataValueByPosition(7, 0));
    assertEquals("7", table.getCellDataValueByPosition(8, 0));
    assertEquals("8", table.getCellDataValueByPosition(9, 0));
    assertEquals("9", table.getCellDataValueByPosition(10, 0));
    assertEquals("10", table.getCellDataValueByPosition(11, 0));

    // Append a token to the second column
    table.doubleclick(11, 2);

    typeTextPressReturn(table, "test");

    // This should add one empty TLI/row
    assertEquals(13, natTable.getRowCount());
    assertEquals("11", table.getCellDataValueByPosition(12, 0));
    Object createdTokenRaw = table.widget.getDataValueByPosition(2, 11);
    assertInstanceOf(SToken.class, createdTokenRaw);
    SToken createdToken = (SToken) createdTokenRaw;
    assertEquals("test", createdToken.getGraph().getText(createdToken));
  }

  /**
   * Types the value of TEST_ANNOTATION_VALUE, then Return, then waits until the tableToTest has no
   * active cell editors, up to 1000ms.
   *
   * @param tableToTest The {@link NatTable} to operate on
   * @param annoValue The value to insert
   * @throws TimeoutException after 1000ms without returning successfully
   */
  private void typeTextPressReturn(SWTBotNatTable table, String annoValue) {
    if (SystemUtils.IS_OS_MAC_OSX) {
      // There seems to be an issue with editing a cell when the span was created from
      // a context menu triggered by SWT bot. Clicking manually on the context menu works and
      // the text can be inserted right away. Pressing ESC first on macOS circumvents this problem,
      // but is more a workaround.
      keyboard.pressShortcut(Keystrokes.ESC);
    }

    keyboard.typeText(annoValue, 10);
    keyboard.pressShortcut(Keystrokes.CR);
    bot.waitUntil(new TableCellEditorInactiveCondition(table));
  }


}
