package org.corpus_tools.hexatomic.formats.exporter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.FileChooserProvider;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.formats.WizardDialogProvider;
import org.corpus_tools.hexatomic.it.tests.TestHelper;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestExportHandler {

  private static final String EXPORT = "Export";
  private static final String SAVE_PROJECT_BEFORE_EXPORT = "Save project before export?";

  private SWTWorkbenchBot bot;

  private URI exampleProjectUri;
  private ProjectManager projectManager;
  private WizardDialog wizardDialog;
  private ErrorService errorService;
  DirectoryDialog directoryChooser;


  @BeforeEach
  private void setUp() throws IOException {

    wizardDialog = mock(WizardDialog.class);
    projectManager = mock(ProjectManager.class);
    errorService = mock(ErrorService.class);
    directoryChooser = mock(DirectoryDialog.class);
    FileChooserProvider fileChooserProvider = mock(FileChooserProvider.class);
    when(fileChooserProvider.createDirectoryDialog(any())).thenReturn(directoryChooser);

    WizardDialogProvider wizardProvider = mock(WizardDialogProvider.class);
    when(wizardProvider.createDialog(any(), any())).thenReturn(wizardDialog);

    IEclipseContext ctx = TestHelper.getEclipseContext();

    bot = new SWTWorkbenchBot(ctx);

    // Inject mock objects in order to be able to test the executed methods
    ctx.set(FileChooserProvider.class, fileChooserProvider);
    ctx.set(WizardDialogProvider.class, wizardProvider);
    ctx.set(ProjectManager.class, projectManager);
    ctx.set(ErrorService.class, errorService);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    SaltProject project = SaltUtil.loadCompleteSaltProject(exampleProjectUri);
    when(projectManager.getProject()).thenReturn(project);
  }

  @Test
  void testExecuteSaveDefault() {
    when(projectManager.isDirty()).thenReturn(false);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    bot.menu(EXPORT).click();

    verify(wizardDialog).open();
    verifyZeroInteractions(directoryChooser, errorService);
  }

  @Test
  void testExecuteSaveDirty() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    bot.menu(EXPORT).click();
    
    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("OK").click();

    verify(wizardDialog).open();

    verifyZeroInteractions(directoryChooser, errorService);
  }

  @Test
  void testExecuteSaveDirtyAbort() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    bot.menu(EXPORT).click();

    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("Cancel").click();
    verifyZeroInteractions(wizardDialog, directoryChooser, errorService);
  }


  @Test
  void testExecuteSaveAsDirty() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());
    when(directoryChooser.open()).thenReturn(exampleProjectUri.toFileString());

    bot.menu(EXPORT).click();

    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("OK").click();

    verify(directoryChooser).open();
    verify(wizardDialog).open();

    verifyZeroInteractions(errorService);
  }

  @Test
  void testExecuteSaveAsDirtyAbort() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());

    bot.menu(EXPORT).click();

    SWTBotShell saveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(saveDialog);

    saveDialog.bot().button("Cancel").click();
    verifyZeroInteractions(wizardDialog, directoryChooser, errorService);
  }

  @Test
  void testExecuteSaveAsDirtyDontChooseFile() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());
    
    bot.menu(EXPORT).click();

    SWTBotShell saveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(saveDialog);

    // Don't actually choose a directory
    when(directoryChooser.open()).thenReturn(null);

    // Say OK to saving the project first
    saveDialog.bot().button("OK").click();
    
    verify(directoryChooser).open();
    
    // Wizard should not be opened
    verifyZeroInteractions(wizardDialog, errorService);
  }

}
