/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
    if (projectManager.isDirty()) {
      // Ask user if project should be closed even with unsaved changes
      boolean confirmed = MessageDialog.openConfirm((Shell) window.getWidget(),
          "Discard unsaved changes?",
          "There are unsaved changes in the project that will be lost if you close the "
          + "application. Do you really want to close Hexatomic?");
      return confirmed;
    }
    return true;
  }
}
