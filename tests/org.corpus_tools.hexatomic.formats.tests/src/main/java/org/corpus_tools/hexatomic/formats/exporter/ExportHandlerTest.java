package org.corpus_tools.hexatomic.formats.exporter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.core.handlers.SaveAsHandler;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExportHandlerTest {

  private ExportHandler fixture;
  private URI exampleProjectUri;
  private Shell shell;
  private ProjectManager projectManager;
  private DirectoryDialog saveDialog;
  private WizardDialog wizardDialog;
  private ErrorService errorService;
  private UISynchronize sync;
  private SaltNotificationFactory notificationFactory;

  @BeforeEach
  private void setUp() throws IOException {

    saveDialog = mock(DirectoryDialog.class);
    wizardDialog = mock(WizardDialog.class);
    shell = mock(Shell.class);
    projectManager = mock(ProjectManager.class);
    errorService = mock(ErrorService.class);
    notificationFactory = mock(SaltNotificationFactory.class);
    sync = mock(UISynchronize.class);

    this.fixture = new ExportHandler() {
      @Override
      protected WizardDialog createDialog(Shell shell, IWizard exportWizard) {
        // return the mocked wizard dialog
        return wizardDialog;
      }
    };


    this.fixture.saveAsHandler = new SaveAsHandler() {
      protected DirectoryDialog createDialog(Shell shell) {
        return saveDialog;
      }
    };


    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    SaltProject project = SaltUtil.loadCompleteSaltProject(exampleProjectUri);
    when(projectManager.getProject()).thenReturn(project);
  }

  @Test
  void testExecuteSaveAsDefault() {
    when(projectManager.isDirty()).thenReturn(false);
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));
    fixture.execute(shell, errorService, projectManager, notificationFactory, sync);

    verify(wizardDialog).open();
    
    verifyZeroInteractions(errorService, saveDialog);
  }

}
