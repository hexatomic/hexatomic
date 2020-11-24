package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SuppressWarnings("restriction")
@TestMethodOrder(OrderAnnotation.class)
class TestImportExport {

  private static final String EXMARALDA_FORMAT_EXB = "EXMARaLDA format (*.exb)";
  private static final String PAULA_FORMAT = "PAULA format";
  private static final String FINISH = "Finish";
  private static final String NEXT = "Next >";
  private static final String EXPORT = "Export";
  private static final String IMPORT = "Import";
  private static final String ADD_SPACES_BETWEEN_TOKEN = "Add spaces between token";

  private final class WizardClosedCondition extends DefaultCondition {
    private final SWTBotShell wizard;

    private WizardClosedCondition(SWTBotShell exportWizard) {
      this.wizard = exportWizard;
    }

    @Override
    public boolean test() throws Exception {
      return wizard.widget.isDisposed();
    }

    @Override
    public String getFailureMessage() {
      return "Wizard was not closed";
    }
  }


  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private ErrorService errorService;
  private ProjectManager projectManager;


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
  }


  /**
   * This test first opens the default sample in the Salt format and then export and imports it
   * again. Using the exporter allows us to test the import without storing the exmaralda files as
   * test resource.
   * 
   * @throws IOException Might throw an exception when there are no temporary directories to create
   */
  @Test
  void testExportAndImportExmaralda() throws IOException {
    // Check that export is disabled for empty default project (which has no location on disk)
    assertFalse(bot.menu(EXPORT).isEnabled());

    // Open example corpus
    openDefaultExample();
    Optional<SDocument> doc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      // This document should have some pointing relations
      assertFalse(doc1.get().getDocumentGraph().getPointingRelations().isEmpty());
    }

    assertTrue(bot.menu(EXPORT).isEnabled());

    // Click on the export menu add fill out the wizard
    bot.menu(EXPORT).click();

    SWTBotShell wizard = bot.shell("Export a corpus project to a different file format");
    assertNotNull(wizard);
    assertTrue(wizard.isOpen());

    Path tmpDir = Files.createTempDirectory("hexatomic-export-test");
    wizard.bot().text().setText(tmpDir.toAbsolutePath().toString());
    wizard.bot().button(NEXT).click();

    wizard.bot().radio(EXMARALDA_FORMAT_EXB).click();
    wizard.bot().button(FINISH).click();

    // Wait until wizard is finished
    bot.waitUntil(new WizardClosedCondition(wizard), 30000);

    // Check no errors have been handled
    assertFalse(errorService.getLastException().isPresent());

    // Check that the exmaralda files have been created
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc1.exb").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc2.exb").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc3.exb").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc4.exb").toFile().isFile());

    // Re-import the just created exb files
    assertTrue(bot.menu(IMPORT).isEnabled());
    bot.menu(IMPORT).click();
    wizard = bot.shell("Import a corpus project from a different file format");
    assertNotNull(wizard);
    assertTrue(wizard.isOpen());
    wizard.bot().text().setText(tmpDir.resolve("rootCorpus").toAbsolutePath().toString());
    wizard.bot().button(NEXT).click();

    wizard.bot().radio(EXMARALDA_FORMAT_EXB).click();
    wizard.bot().button(NEXT).click();

    // Uncheck the add spaces option
    assertTrue(wizard.bot().checkBox(ADD_SPACES_BETWEEN_TOKEN).isChecked());
    wizard.bot().checkBox(ADD_SPACES_BETWEEN_TOKEN).click();
    assertFalse(wizard.bot().checkBox(ADD_SPACES_BETWEEN_TOKEN).isChecked());

    wizard.bot().button(FINISH).click();
    // Wait until wizard is finished
    bot.waitUntil(new WizardClosedCondition(wizard), 30000);

    // Check all documents exist again
    assertEquals(1, projectManager.getProject().getCorpusGraphs().size());
    assertEquals(4, projectManager.getProject().getCorpusGraphs().get(0).getDocuments().size());

    doc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      // Exporting to Exmaralda should have removed pointing annotations
      assertEquals(0, doc1.get().getDocumentGraph().getPointingRelations().size());
      // Because of the import configuration, the text should not contain artificially generated
      // spaces
      assertEquals("Isthisexamplemorecomplicatedthanitappearstobe?",
          doc1.get().getDocumentGraph().getTextualDSs().get(0).getText());
    }

    // Import again, but this time with spaces
    bot.menu(IMPORT).click();
    wizard = bot.shell("Import a corpus project from a different file format");
    assertNotNull(wizard);
    assertTrue(wizard.isOpen());
    wizard.bot().text().setText(tmpDir.resolve("rootCorpus").toAbsolutePath().toString());
    wizard.bot().button(NEXT).click();

    wizard.bot().radio(EXMARALDA_FORMAT_EXB).click();
    wizard.bot().button(NEXT).click();

    assertTrue(wizard.bot().checkBox(ADD_SPACES_BETWEEN_TOKEN).isChecked());

    wizard.bot().button(FINISH).click();
    // Wait until wizard is finished
    bot.waitUntil(new WizardClosedCondition(wizard), 30000);

    doc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      // Because of the import configuration, the text should contain spaces between all tokens
      assertEquals("Is this example more complicated than it appears to be ? ",
          doc1.get().getDocumentGraph().getTextualDSs().get(0).getText());
    }

  }


  /**
   * This test first opens the default sample in the Salt format and then export and imports it
   * again. Using the exporter allows us to test the import without storing the PAULA XML files as
   * test resource.
   * 
   * @throws IOException Might throw an exception when there are no temporary directories to create
   */
  @Test
  void testExportImportPaula() throws IOException {
    // Check that export is disabled for empty default project (which has no location on disk)
    assertFalse(bot.menu(EXPORT).isEnabled());

    // Open example corpus
    openDefaultExample();
    SaltProject p = projectManager.getProject();
    assertEquals(1, p.getCorpusGraphs().size());
    assertEquals(4, p.getCorpusGraphs().get(0).getDocuments().size());
    assertTrue(bot.menu(EXPORT).isEnabled());

    Optional<SDocument> doc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(doc1.isPresent());
    int numberOfPointingRelations = 0;
    if (doc1.isPresent()) {
      // This document should have some pointing relations
      numberOfPointingRelations = doc1.get().getDocumentGraph().getPointingRelations().size();
      assertTrue(numberOfPointingRelations > 0);
    }

    // Click on the export menu add fill out the wizard
    bot.menu(EXPORT).click();

    SWTBotShell wizard = bot.shell("Export a corpus project to a different file format");
    assertNotNull(wizard);
    assertTrue(wizard.isOpen());

    Path tmpDir = Files.createTempDirectory("hexatomic-export-test");
    wizard.bot().text().setText(tmpDir.toAbsolutePath().toString());
    wizard.bot().button(NEXT).click();

    wizard.bot().radio(PAULA_FORMAT).click();
    wizard.bot().button(FINISH).click();

    // Wait until wizard is finished
    bot.waitUntil(new WizardClosedCondition(wizard), 30000);

    assertEquals(1, p.getCorpusGraphs().size());
    assertEquals(4, p.getCorpusGraphs().get(0).getDocuments().size());

    // Check no errors have been handled
    assertFalse(errorService.getLastException().isPresent());

    // Check that the exmaralda files have been created
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc1/doc1.text.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc1/doc1.tok.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc1/doc1.tok_pos.xml").toFile().isFile());

    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc2/doc2.text.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc2/doc2.tok.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus1/doc2/doc2.tok_pos.xml").toFile().isFile());

    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc3/doc3.text.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc3/doc3.tok.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc3/doc3.tok_pos.xml").toFile().isFile());

    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc4/doc4.text.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc4/doc4.tok.xml").toFile().isFile());
    assertTrue(tmpDir.resolve("rootCorpus/subCorpus2/doc4/doc4.tok_pos.xml").toFile().isFile());

    // Import the just exported corpus
    bot.menu(IMPORT).click();

    wizard = bot.shell("Import a corpus project from a different file format");
    assertNotNull(wizard);
    assertTrue(wizard.isOpen());
    wizard.bot().text().setText(tmpDir.resolve("rootCorpus").toAbsolutePath().toString());
    wizard.bot().button(NEXT).click();

    wizard.bot().radio(PAULA_FORMAT).click();
    wizard.bot().button(FINISH).click();
    bot.waitUntil(new WizardClosedCondition(wizard), 30000);

    doc1 = projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1", true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      // Exporting to PAULA should keep the pointing relations
      assertEquals(numberOfPointingRelations,
          doc1.get().getDocumentGraph().getPointingRelations().size());
    }

  }

}
