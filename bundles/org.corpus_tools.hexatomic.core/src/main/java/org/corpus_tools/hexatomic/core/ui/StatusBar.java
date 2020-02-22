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

package org.corpus_tools.hexatomic.core.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class StatusBar {

  @Inject
  UISynchronize sync;

  private ProgressBar progressBarIndeterminate;

  private Label lblMessage;
  private ProgressBar progressBar;

  private StackLayout progressLayout;

  @PostConstruct
  protected void createControls(Composite parent) {
    GridLayout glParent = new GridLayout(2, false);
    glParent.marginWidth = 0;
    glParent.marginHeight = 0;
    parent.setLayout(glParent);

    lblMessage = new Label(parent, SWT.NONE);
    lblMessage.setText("");
    lblMessage.setAlignment(SWT.RIGHT);
    lblMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));

    Composite composite = new Composite(parent, SWT.NONE);
    progressLayout = new StackLayout();
    composite.setLayout(progressLayout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

    progressBar = new ProgressBar(composite, SWT.SMOOTH);
    progressBarIndeterminate = new ProgressBar(composite, SWT.INDETERMINATE);

    progressLayout.topControl = null;

    final ProgressMonitor monitor = new ProgressMonitor();
    Job.getJobManager().setProgressProvider(new ProgressProvider() {
      @Override
      public IProgressMonitor createMonitor(Job job) {
        return monitor;
      }
    });

  }


  private final class ProgressMonitor extends NullProgressMonitor {

    private int runningTasksCounter = 0;
    private boolean unknownTotalWork = false;


    @Override
    public void beginTask(final String name, final int totalWork) {
      sync.syncExec(new Runnable() {

        @Override
        public void run() {

          if (totalWork == 0) {
            unknownTotalWork = true;
            progressLayout.topControl = progressBarIndeterminate;
            progressBarIndeterminate.requestLayout();
            progressBarIndeterminate.setMaximum(1);
            progressBarIndeterminate.setSelection(0);;


          } else if (runningTasksCounter <= 0) {
            unknownTotalWork = false;
            progressLayout.topControl = progressBar;
            progressBar.requestLayout();

            progressBar.setSelection(0);
            progressBar.setMaximum(totalWork);



          } else if (!unknownTotalWork) {
            progressLayout.topControl = progressBar;

            progressBar.setMaximum(progressBar.getMaximum() + totalWork);
          }

          runningTasksCounter++;
          if (runningTasksCounter > 1) {
            lblMessage.setText("Currently running: " + runningTasksCounter + " | " + name);
          } else {
            lblMessage.setText(name);
          }
        }
      });
    }

    @Override
    public void worked(final int work) {
      sync.syncExec(new Runnable() {

        @Override
        public void run() {
          progressBar.setSelection(progressBar.getSelection() + work);
        }
      });
    }

    @Override
    public void done() {
      sync.syncExec(() -> {
        runningTasksCounter--;
        if (runningTasksCounter > 0) {
          lblMessage.setText("Currently running: " + runningTasksCounter);
        } else {
          lblMessage.setText("");
          progressLayout.topControl = null;
          progressBar.requestLayout();
          unknownTotalWork = false;
          runningTasksCounter = 0;
        }
      });
    }
  }

}
