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
package org.corpus_tools.hexatomic.core;

import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

/**
 * A service to display status changes (e.g. unsaved changes, opened projects) in the UI.
 * 
 * @author Thomas Krause
 *
 */
@Creatable
@Singleton
public class UiStatusReport {

  private static final String MAIN_WINDOW_ID = "org.eclipse.e4.window.main";

  private static final String BASE_TITLE = "Hexatomic";

  @Inject
  EModelService modelService;

  @Inject
  MApplication app;

  private String location;

  private boolean isDirty;

  /**
   * Set a location which will be displayed in the title of the window.
   * 
   * @param location The location or null to unset it.
   */
  public void setLocation(String location) {
    if (!Objects.equals(this.location, location)) {
      this.location = location;
      update();
    }
  }

  /**
   * Highlight if the project is in a changed state.
   * 
   * @param isDirty True if project changed
   */
  public void setDirty(boolean isDirty) {
    if (this.isDirty != isDirty) {
      this.isDirty = isDirty;
      update();
    }
  }

  private void update() {
    MUIElement mainWindowRaw = modelService.find(MAIN_WINDOW_ID, app);
    if (mainWindowRaw instanceof MWindow) {
      MWindow mainWindow = (MWindow) mainWindowRaw;
      StringBuilder sb = new StringBuilder(BASE_TITLE);
      if (location != null && !location.isEmpty()) {
        sb.append(" - ");
        sb.append(location);
      }

      if (isDirty) {
        sb.append(" *");
      }

      mainWindow.setLabel(sb.toString());

    }
  }

}
