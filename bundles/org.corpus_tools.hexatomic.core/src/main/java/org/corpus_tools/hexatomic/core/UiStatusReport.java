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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
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
public class UiStatusReport extends NullProgressMonitor {

  private static final String MAIN_WINDOW_ID = "org.eclipse.e4.window.main";

  private static final String BASE_TITLE = "Hexatomic";

  @Inject
  EModelService modelService;

  @Inject
  IEventBroker events;

  @Inject
  MApplication app;

  @Inject
  UISynchronize sync;

  private String location;

  private boolean isDirty;

  private int runningJobs = 0;
  private int totalJobsWorkload = 0;
  private int totalJobProgress = 0;
  private String lastJobName = "";

  @PostConstruct
  protected void postConstruct() {
    Job.getJobManager().setProgressProvider(new ProgressProvider() {
      @Override
      public IProgressMonitor createMonitor(Job job) {
        return UiStatusReport.this;
      }
    });
  }

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

    events.send(Topics.STATUS_UPDATE, "");
  }

  /**
   * Return an overall string representation of the currently executed jobs.
   * 
   * @return The message.
   */
  public String getExecutedJobStatusMessage() {
    if (runningJobs == 0) {
      return "";
    } else if (runningJobs > 1) {
      return "Currently running: " + runningJobs + " | " + lastJobName;
    } else {
      return lastJobName;
    }
  }

  /**
   * Get the sum of the progress of all currently executed jobs.
   * 
   * @return The progress.
   */
  public int getExecutedJobsProgress() {
    return totalJobProgress;
  }

  /**
   * Get the sum of the workload of all currently executed jobs. If any executed job has no given
   * workload, this returns 0
   * 
   * @return Workload or 0 .
   */
  public int getExecutedJobsWorkload() {
    return totalJobsWorkload;
  }

  /**
   * Get the number of currently running jobs.
   * 
   * @return The positive number.
   */
  public int getNumberOfExecutedJobs() {
    return runningJobs;
  }

  /**
   * Returns true when there is at least one job with an indetermined workload.
   * 
   * @return
   */
  public boolean hasIndeterminedJobWorkload() {
    return runningJobs > 0 && totalJobsWorkload == 0;
  }

  @Override
  public void beginTask(final String name, final int totalWork) {
    sync.syncExec(() -> {
      if (totalWork >= 1 && !hasIndeterminedJobWorkload()) {
        totalJobsWorkload += totalWork;
      } else {
        totalJobsWorkload = 0;
      }

      runningJobs++;
      lastJobName = name;

      update();
    });
  }


  @Override
  public void worked(final int work) {
    sync.syncExec(() -> {
      totalJobProgress++;

      update();
    });
  }

  @Override
  public void done() {
    sync.syncExec(() -> {
      runningJobs--;
      if (runningJobs <= 0) {
        runningJobs = 0;
        totalJobProgress = 0;
        totalJobsWorkload = 0;
        lastJobName = "";
      }

      update();
    });
  }

}
