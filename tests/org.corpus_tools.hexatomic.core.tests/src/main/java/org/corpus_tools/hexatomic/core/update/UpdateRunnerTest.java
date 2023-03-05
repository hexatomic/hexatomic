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
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.update.UpdateRunner.UpdateFinishedListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.BackingStoreException;

class UpdateRunnerTest {


  private static final String UPDATES_INSTALLED_RESTART = "Updates installed, restart?";


  public interface UpdateRunnerMock {
    boolean openQuestionDialog(Shell parent, String title, String message);

    UpdateOperation createUpdateOperation();
  }

  private UpdateRunner fixture;

  private Shell shell;
  private UpdateRunnerMock updateRunner;

  private IEclipsePreferences actualPrefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");

  @BeforeEach
  void setUp() throws BackingStoreException {
    // Always start with empty preferences
    actualPrefs.clear();


    shell = mock(Shell.class);
    updateRunner = mock(UpdateRunnerMock.class);

    fixture = new UpdateRunner() {
      @Override
      protected boolean openQuestionDialog(Shell parent, String title, String message) {
        return updateRunner.openQuestionDialog(parent, title, message);
      }

      @Override
      protected UpdateOperation createUpdateOperation() {
        return updateRunner.createUpdateOperation();
      }
    };

    fixture.sync = new DummySync();
    // Use the actual map implementation to store state, but allow mocking e.g. for throwing
    // exceptions
    fixture.prefs = spy(actualPrefs);
    fixture.errorService = mock(ErrorService.class);
    fixture.agent = mock(IProvisioningAgent.class);
    fixture.events = mock(IEventBroker.class);
    fixture.context = mock(IEclipseContext.class);

  }

  @AfterEach
  void cleanup() throws BackingStoreException {
    actualPrefs.clear();
  }

  @Test
  void testUpdateDeniedAtStart() {
    when(updateRunner.openQuestionDialog(any(), any(), any())).thenReturn(false);
    
    assertEquals(false, fixture.autoUpdateAllowed(shell));
    
    verify(updateRunner).openQuestionDialog(any(), 
        eq("Automatic update check configuration"), any());
    assertEquals(false, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, true));
  }

  @Test
  void testUpdateAllowedAtStart() throws BackingStoreException {
    when(updateRunner.openQuestionDialog(any(), any(), any())).thenReturn(true);
    
    assertEquals(true, fixture.autoUpdateAllowed(shell));
    
    verify(updateRunner).openQuestionDialog(any(), 
        eq("Automatic update check configuration"), any());
    assertEquals(true, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, false));
    verify(fixture.prefs).flush();
  }

  @Test
  void testCantStoreApproval() throws BackingStoreException {
    when(updateRunner.openQuestionDialog(any(), any(), any())).thenReturn(true);
    doThrow(BackingStoreException.class).when(fixture.prefs).flush(); 
    fixture.autoUpdateAllowed(shell);
    
    verify(fixture.errorService).handleException(anyString(), any(), any());
  }

  @Test
  void testUpdateWasAllowedInPreferences() {
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, true);

    assertEquals(true, fixture.autoUpdateAllowed(shell));

    verifyNoInteractions(updateRunner);

    assertEquals(true, fixture.prefs.getBoolean(Preferences.AUTO_UPDATE, false));
  }

  @Test
  void testUpdateWasDeniedInPreferences() {
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, false);

    assertEquals(false, fixture.autoUpdateAllowed(shell));

    verifyNoInteractions(updateRunner);

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
    doThrow(BackingStoreException.class).when(fixture.prefs).flush();
    fixture.prefs.putBoolean(Preferences.AUTO_UPDATE, true);
    fixture.prefs.putBoolean(Preferences.JUST_UPDATED, true);

    fixture.autoUpdateAllowed(shell);

    verify(fixture.errorService).handleException(anyString(), any(), any());
  }

  @Test
  void testNoUpdateAvailable() {
    IStatus updateStatus = mock(IStatus.class);
    when(updateStatus.getCode()).thenReturn(UpdateOperation.STATUS_NOTHING_TO_UPDATE);
    UpdateOperation op = mock(UpdateOperation.class);
    when(op.resolveModal(any(IProgressMonitor.class))).thenReturn(updateStatus);
    when(updateRunner.createUpdateOperation()).thenReturn(op);

    IStatus result = fixture.checkForUpdates(false, shell);

    assertEquals(Status.CANCEL_STATUS, result);
    verify(fixture.events).send(eq(Topics.TOOLBAR_STATUS_MESSAGE), eq("Hexatomic is up to date"));
  }

  @Test
  void testUpdateAvailableAccepted() {
    IStatus updateStatus = mock(IStatus.class);
    when(updateStatus.getCode()).thenReturn(0);

    ProvisioningJob job = mock(ProvisioningJob.class);

    UpdateOperation op = mock(UpdateOperation.class);
    when(op.resolveModal(any(IProgressMonitor.class))).thenReturn(updateStatus);
    when(op.getProvisioningJob(any())).thenReturn(job);

    when(updateRunner.createUpdateOperation()).thenReturn(op);

    when(updateRunner.openQuestionDialog(any(), eq("Update available"), any())).thenReturn(true);

    IStatus result = fixture.checkForUpdates(false, shell);

    assertEquals(Status.OK_STATUS, result);
  }

  @Test
  void testUpdateFinishedListenerWithRestart() {
    IWorkbench workbench = mock(IWorkbench.class);
    IJobChangeEvent event = mock(IJobChangeEvent.class);
    when(event.getResult()).thenReturn(Status.OK_STATUS);

    when(updateRunner.openQuestionDialog(any(), eq(UPDATES_INSTALLED_RESTART), any()))
        .thenReturn(true);

    UpdateFinishedListener listener = fixture.new UpdateFinishedListener(workbench, shell);

    listener.done(event);

    verify(workbench).restart();
    assertEquals(true, fixture.prefs.getBoolean(Preferences.JUST_UPDATED, false));
  }

  @Test
  void testUpdateFinishedListenerWithRestartPreferenceStorageFailing()
      throws BackingStoreException {
    IWorkbench workbench = mock(IWorkbench.class);
    IJobChangeEvent event = mock(IJobChangeEvent.class);
    when(event.getResult()).thenReturn(Status.OK_STATUS);
    doThrow(BackingStoreException.class).when(fixture.prefs).flush();


    when(updateRunner.openQuestionDialog(any(), eq(UPDATES_INSTALLED_RESTART), any()))
        .thenReturn(true);

    UpdateFinishedListener listener = fixture.new UpdateFinishedListener(workbench, shell);

    listener.done(event);

    verify(fixture.errorService).handleException(anyString(), any(), any());
  }



  @Test
  void testUpdateFinishedListenerNoRestart() {
    IWorkbench workbench = mock(IWorkbench.class);
    IJobChangeEvent event = mock(IJobChangeEvent.class);
    when(event.getResult()).thenReturn(Status.OK_STATUS);

    when(updateRunner.openQuestionDialog(any(), eq(UPDATES_INSTALLED_RESTART), any()))
        .thenReturn(false);

    UpdateFinishedListener listener = fixture.new UpdateFinishedListener(workbench, shell);

    listener.done(event);

    verifyNoInteractions(workbench);
  }

  @Test
  void testUpdateListenerNotFinished() {
    IWorkbench workbench = mock(IWorkbench.class);
    IJobChangeEvent event = mock(IJobChangeEvent.class);
    when(event.getResult()).thenReturn(Status.CANCEL_STATUS);

    UpdateFinishedListener listener = fixture.new UpdateFinishedListener(workbench, shell);

    listener.done(event);

    verifyNoInteractions(workbench);
  }

  @Test
  void testUpdateAvailableCancelled() {
    IStatus updateStatus = mock(IStatus.class);
    when(updateStatus.getCode()).thenReturn(0);

    ProvisioningJob job = mock(ProvisioningJob.class);

    UpdateOperation op = mock(UpdateOperation.class);
    when(op.resolveModal(any(IProgressMonitor.class))).thenReturn(updateStatus);
    when(op.getProvisioningJob(any())).thenReturn(job);

    when(updateRunner.createUpdateOperation()).thenReturn(op);

    when(updateRunner.openQuestionDialog(any(), eq("Update available"), any())).thenReturn(false);

    IStatus result = fixture.checkForUpdates(false, shell);

    assertEquals(Status.CANCEL_STATUS, result);
  }

  @Test
  void testUpdateAvailableErrorBackground() {
    IStatus updateStatus = mock(IStatus.class);
    // Return the status code for "missing requirements"
    when(updateStatus.getCode()).thenReturn(10053);


    UpdateOperation op = mock(UpdateOperation.class);
    when(op.resolveModal(any(IProgressMonitor.class))).thenReturn(updateStatus);
    when(op.getProvisioningJob(any())).thenReturn(null);
    when(updateRunner.createUpdateOperation()).thenReturn(op);

    IStatus result = fixture.checkForUpdates(false, shell);

    assertEquals(Status.CANCEL_STATUS, result);
    verify(fixture.events).send(eq(Topics.TOOLBAR_STATUS_MESSAGE),
        eq("Update check failed (error code 10053)."));
  }


  @Test
  void testUpdateAvailableErrorForeground() {
    IStatus updateStatus = mock(IStatus.class);
    // Return the status code for "missing requirements"
    when(updateStatus.getCode()).thenReturn(10053);


    UpdateOperation op = mock(UpdateOperation.class);
    when(op.resolveModal(any(IProgressMonitor.class))).thenReturn(updateStatus);
    when(op.getProvisioningJob(any())).thenReturn(null);
    when(updateRunner.createUpdateOperation()).thenReturn(op);

    IStatus result = fixture.checkForUpdates(true, shell);

    assertEquals(Status.CANCEL_STATUS, result);
    verify(fixture.errorService).showError(eq("Update check failed (code 10053)"), anyString(),
        any());
  }
}
