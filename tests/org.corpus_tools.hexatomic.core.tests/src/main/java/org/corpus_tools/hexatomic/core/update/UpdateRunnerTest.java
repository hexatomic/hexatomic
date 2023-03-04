package org.corpus_tools.hexatomic.core.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.core.DummySync;
import org.corpus_tools.hexatomic.core.Preferences;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.BackingStoreException;

class UpdateRunnerTest {

  private static final IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");


  public interface MessageDialogMock {
    boolean openQuestionDialog(Shell parent, String title, String message);
  }

  private UpdateRunner fixture;

  private Shell shell;
  private MessageDialogMock dialog;


  @BeforeEach
  void setUp() throws Exception {
    // Always start with now preferences
    prefs.clear();

    shell = mock(Shell.class);
    dialog = mock(MessageDialogMock.class);

    fixture = new UpdateRunner() {
      @Override
      protected boolean openQuestionDialog(Shell parent, String title, String message) {
        return dialog.openQuestionDialog(parent, title, message);
      }
    };
    fixture.sync = new DummySync();
  }

  @AfterEach
  void cleanup() throws BackingStoreException {
    prefs.clear();
  }


  @Test
  void testUpdateDeniedAtStart() {
    when(dialog.openQuestionDialog(any(), any(), any())).thenReturn(false);
    
    assertEquals(false, fixture.autoUpdateAllowed(shell));
    
    verify(dialog).openQuestionDialog(any(), eq("Automatic update check configuration"), any());
    assertEquals(false, prefs.getBoolean(Preferences.AUTO_UPDATE, true));
  }

}
