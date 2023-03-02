package org.corpus_tools.hexatomic.core.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.FileChooserProvider;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.handlers.SaveAsHandler;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSaveAsHandler {


  private SaveAsHandler fixture;
  private URI exampleProjectUri;
  private Shell shell;
  private ProjectManager projectManager;
  private DirectoryDialog dialog;


  @BeforeEach
  public void setUp() {
    dialog = mock(DirectoryDialog.class);

    this.fixture = new SaveAsHandler();
    this.fixture.setFileChooserProvider(new FileChooserProvider() {
      @Override
      public DirectoryDialog createDirectoryDialog(Shell shell) {
        return dialog;
      }
    });
    File exampleProjectDirectory =
        new File("src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());

    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

    shell = mock(Shell.class);
    projectManager = mock(ProjectManager.class);


    this.fixture.setProjectManager(projectManager);

  }

  @Test
  public void testSaveAsOriginalLocation() {
    when(projectManager.getLocation()).thenReturn(Optional.of(exampleProjectUri));
    when(dialog.open()).thenReturn(exampleProjectUri.toFileString());
    
    this.fixture.execute(shell, null);
    
    // This command needs to open a dialog with the last path as initial location
    verify(projectManager).getLocation();
    verify(dialog).setFilterPath(eq(exampleProjectUri.toFileString()));
    verify(dialog).open();
    verify(projectManager).saveTo(eq(exampleProjectUri), eq(shell));

    verifyNoMoreInteractions(dialog);
    verifyNoMoreInteractions(projectManager);
  }

  @Test
  public void testSaveAsNoOriginalLocation() {
    when(projectManager.getLocation()).thenReturn(Optional.empty());
    when(dialog.open()).thenReturn(exampleProjectUri.toFileString());

    this.fixture.execute(shell, null);

    // This command needs to open a dialog with the last path but can't select any previous location
    verify(projectManager).getLocation();
    verify(dialog).open();
    verify(projectManager).saveTo(eq(exampleProjectUri), eq(shell));

    verifyNoMoreInteractions(dialog);
    verifyNoMoreInteractions(projectManager);
  }

}
