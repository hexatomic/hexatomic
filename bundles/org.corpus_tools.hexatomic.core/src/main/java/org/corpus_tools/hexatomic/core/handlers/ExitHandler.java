package org.corpus_tools.hexatomic.core.handlers;

import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;

public class ExitHandler implements IWindowCloseHandler {


  @Inject
  @Optional
  public void startupComplete(
      @UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) MApplication application,
      EModelService modelService) {
    MWindow window = (MWindow) modelService.find("org.eclipse.e4.window.main", application);
    window.getContext().set(IWindowCloseHandler.class, this);
  }

  @Execute
  public void execute(IWorkbench workbench, MWindow window) {
    if (this.close(window)) {
      workbench.close();
    }
  }

  @Override
  public boolean close(MWindow window) {
    // TODO: Check if the project has been saved
    return true;
  }
}
