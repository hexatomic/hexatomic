package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ExitHandler implements IWindowCloseHandler {

  @Inject
  private ProjectManager projectManager;

  @Inject
  @Optional
  public void startupComplete(
      @UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) MApplication application,
      EModelService modelService) {
    MWindow window = (MWindow) modelService.find("org.eclipse.e4.window.main", application);
    window.getContext().set(IWindowCloseHandler.class, this);
  }

  @Execute
  protected void execute(IWorkbench workbench, MWindow window,
      @Optional @Named(CommandParams.FORCE_CLOSE) String forceCloseRaw) {

    boolean forceClose = Boolean.parseBoolean(forceCloseRaw);

    if (forceClose || this.close(window)) {
      workbench.close();
    }
  }

  @Override
  public boolean close(MWindow window) {
    // TODO: Check if the project has been saved
    if (projectManager.isDirty()) {
      // Ask user if project should be closed even with unsaved changes
      boolean confirmed = MessageDialog.openConfirm((Shell) window.getWidget(),
          "Unsaved changes in project",
          "There are unsaved changes in the project that whill be lost if you close the "
          + "application. Do you really want to close Hexatomic?");
      if (confirmed) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }
}
