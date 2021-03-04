package org.corpus_tools.hexatomic.formats.exporter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.core.handlers.SaveAsHandler;
import org.corpus_tools.hexatomic.formats.WizardDialogProvider;
import org.corpus_tools.hexatomic.it.tests.TestHelper;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the several conditions of the {@link ExportHandler}. The handler is used as a fixture with
 * mocked dependencies instead of the real ones from the context to make it easier to access the
 * state. We still need to do these tests in the integration test package, since we need access to
 * the message dialogs (which are created using static methods and not easy to mock).
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
class TestExportHandler {

  private static final String SAVE_PROJECT_BEFORE_EXPORT = "Save project before export?";

  private SWTWorkbenchBot bot;

  private URI exampleProjectUri;
  private ProjectManager projectManager;
  private WizardDialog wizardDialog;
  private ErrorService errorService;
  private SaltNotificationFactory notificationFactory;
  private UISynchronize sync;
  private SaveAsHandler saveAsHandler;

  private ExportHandler fixture;

  @BeforeEach
  private void setUp() {

    wizardDialog = mock(WizardDialog.class);
    projectManager = mock(ProjectManager.class);
    errorService = mock(ErrorService.class);
    saveAsHandler = mock(SaveAsHandler.class);

    WizardDialogProvider wizardProvider = mock(WizardDialogProvider.class);
    when(wizardProvider.createDialog(any(), any())).thenReturn(wizardDialog);

    IEclipseContext ctx = TestHelper.getEclipseContext();

    bot = new SWTWorkbenchBot(ctx);
    // Get non-mocked instances from context
    notificationFactory = ContextInjectionFactory.make(SaltNotificationFactory.class, ctx);
    sync = ctx.get(UISynchronize.class);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    SaltProject project = SaltUtil.loadCompleteSaltProject(exampleProjectUri);
    when(projectManager.getProject()).thenReturn(project);

    fixture = new ExportHandler();
    fixture.setSaveAsHandler(saveAsHandler);
    fixture.setWizardDialogProvider(wizardProvider);
  }

  private void executeHandler() {
    sync.asyncExec(() -> fixture.execute(bot.activeShell().widget, errorService, projectManager,
        notificationFactory, sync));
    sync.syncExec(() -> {
    });
  }

  @Test
  void testExecuteSaveDefault() {
    when(projectManager.isDirty()).thenReturn(false);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    executeHandler();

    verify(wizardDialog).open();
    verifyZeroInteractions(saveAsHandler, errorService);
  }

  @Test
  void testExecuteSaveDirty() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    executeHandler();

    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("OK").click();

    verify(wizardDialog).open();

    verifyZeroInteractions(saveAsHandler, errorService);
  }

  @Test
  void testExecuteSaveDirtyAbort() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));

    executeHandler();

    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("Cancel").click();
    verifyZeroInteractions(wizardDialog, saveAsHandler, errorService);
  }


  @Test
  void testExecuteSaveAsDirty() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());
    when(saveAsHandler.execute(any(), any())).thenReturn(true);

    executeHandler();

    SWTBotShell askSaveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(askSaveDialog);

    askSaveDialog.bot().button("OK").click();

    verify(saveAsHandler).execute(any(), any());
    verify(wizardDialog).open();

    verifyZeroInteractions(errorService);
  }

  @Test
  void testExecuteSaveAsDirtyAbort() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());

    executeHandler();

    SWTBotShell saveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(saveDialog);

    saveDialog.bot().button("Cancel").click();
    verifyZeroInteractions(wizardDialog, saveAsHandler, errorService);
  }

  @Test
  void testExecuteSaveAsDirtyDontChooseFile() {
    when(projectManager.isDirty()).thenReturn(true);
    when(projectManager.getLocation()).thenReturn(Optional.empty());
    when(saveAsHandler.execute(any(), any())).thenReturn(false);

    executeHandler();

    SWTBotShell saveDialog = bot.shell(SAVE_PROJECT_BEFORE_EXPORT);
    assertNotNull(saveDialog);


    // Say OK to saving the project first
    saveDialog.bot().button("OK").click();

    verify(saveAsHandler).execute(any(), any());

    // Wizard should not be opened
    verifyZeroInteractions(wizardDialog, errorService);
  }

}
