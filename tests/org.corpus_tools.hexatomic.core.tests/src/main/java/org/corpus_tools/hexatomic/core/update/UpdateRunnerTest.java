package org.corpus_tools.hexatomic.core.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.core.DummySync;
import org.corpus_tools.hexatomic.core.Preferences;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.BackingStoreException;

class UpdateRunnerTest {


  public interface MessageDialogMock {
    boolean openQuestionDialog(Shell parent, String title, String message);
  }

  private UpdateRunner fixture;

  private Shell shell;
  private MessageDialogMock dialog;

  private IEclipsePreferences actualPrefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");

  @BeforeEach
  void setUp() throws BackingStoreException {
    // Always start with empty preferences
    actualPrefs.clear();


    shell = mock(Shell.class);
    dialog = mock(MessageDialogMock.class);

    fixture = new UpdateRunner() {
      @Override
      protected boolean openQuestionDialog(Shell parent, String title, String message) {
        return dialog.openQuestionDialog(parent, title, message);
      }
    };
    fixture.sync = new DummySync();
    // Use the actual map implementation to store state, but allow mocking e.g. for throwing
    // exceptions
    fixture.prefs = spy(actualPrefs);
    fixture.errorService = mock(ErrorService.class);

  }

  @AfterEach
  void cleanup() throws BackingStoreException {
    actualPrefs.clear();
  }

  @Test
  void testUpdateDeniedAtStart() {
    when(dialog.openQuestionDialog(any(), any(), any())).thenReturn(false);
    
    assertEquals(false, fixture.autoUpdateAllowed(shell));
    
    verify(dialog).openQuestionDialog(any(), eq("Automatic update check configuration"), any());
    assertEquals(false, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, true));
  }

  @Test
  void testUpdateAllowedAtStart() throws BackingStoreException {
    when(dialog.openQuestionDialog(any(), any(), any())).thenReturn(true);
    
    assertEquals(true, fixture.autoUpdateAllowed(shell));
    
    verify(dialog).openQuestionDialog(any(), eq("Automatic update check configuration"), any());
    assertEquals(true, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, false));
    verify(fixture.prefs).flush();
  }

  @Test
  void testCantStoreApproval() throws BackingStoreException {
    when(dialog.openQuestionDialog(any(), any(), any())).thenReturn(true);
    doThrow(BackingStoreException.class).when(fixture.prefs).flush();;
    
    fixture.autoUpdateAllowed(shell);
    
    verify(fixture.errorService).handleException(anyString(), any(), any());
  }

  @Test
  void testUpdateWasAllowedInPreferences() {
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, true);

    assertEquals(true, fixture.autoUpdateAllowed(shell));

    verifyNoInteractions(dialog);

    assertEquals(true, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, false));
  }

  @Test
  void testUpdateWasDeniedInPreferences() {
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, false);

    assertEquals(false, fixture.autoUpdateAllowed(shell));

    verifyNoInteractions(dialog);

    assertEquals(false, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, true));
  }

  @Test
  void testIgnoreJustAfterRestart() throws BackingStoreException {
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, true);
    fixture.prefs.putBoolean(Preferences.JUST_UPDATED, true);

    assertEquals(false, fixture.autoUpdateAllowed(shell));

    assertEquals(false, fixture.prefs.getBoolean(Preferences.JUST_UPDATED, true));
    verify(fixture.prefs).flush();
  }

  @Test
  void testCantStoreJustUpdated() throws BackingStoreException {
    doThrow(BackingStoreException.class).when(fixture.prefs).flush();;
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, true);
    fixture.prefs.putBoolean(Preferences.JUST_UPDATED, true);

    fixture.autoUpdateAllowed(shell);

    verify(fixture.errorService).handleException(anyString(), any(), any());
  }

}
