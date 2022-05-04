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

import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.UiStatusReport;
import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class StatusBar {

  @Inject
  UISynchronize sync;

  @Inject
  UiStatusReport uiStatus;

  private ProgressBar progressBarIndeterminate;

  private Label lblPermanentMessage;
  private Label lblProgressMessage;
  private ProgressBar progressBar;

  private StackLayout progressLayout;

  private final Timer timer = new Timer();

  @PostConstruct
  protected void createControls(Composite parent) {
    GridLayout glParent = new GridLayout(3, false);
    glParent.marginWidth = 0;
    glParent.marginHeight = 0;
    parent.setLayout(glParent);

    lblPermanentMessage = new Label(parent, SWT.NONE);
    lblPermanentMessage.setText("");
    lblPermanentMessage.setAlignment(SWT.LEFT);
    lblPermanentMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

    lblProgressMessage = new Label(parent, SWT.NONE);
    lblProgressMessage.setText("");
    lblProgressMessage.setAlignment(SWT.RIGHT);
    lblProgressMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));

    Composite composite = new Composite(parent, SWT.NONE);
    progressLayout = new StackLayout();
    composite.setLayout(progressLayout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

    progressBar = new ProgressBar(composite, SWT.SMOOTH);
    progressBarIndeterminate = new ProgressBar(composite, SWT.INDETERMINATE);

    progressLayout.topControl = null;

  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void onStatusUpdate(@UIEventTopic(Topics.STATUS_UPDATE) String ignore) {

    lblProgressMessage.setText(uiStatus.getExecutedJobStatusMessage());

    if (uiStatus.getNumberOfExecutedJobs() == 0) {
      // Hide progress bar completely
      progressLayout.topControl = null;
      progressBar.requestLayout();
    } else if (uiStatus.hasIndeterminedJobWorkload()) {
      // Show the indetermined progress bar
      progressLayout.topControl = progressBarIndeterminate;
      progressBarIndeterminate.setMaximum(1);
      progressBarIndeterminate.setSelection(0);
      progressBarIndeterminate.requestLayout();
    } else {
      // Show the progress as total number
      progressLayout.topControl = progressBar;
      progressBar.setMaximum(uiStatus.getExecutedJobsWorkload());
      progressBar.setSelection(uiStatus.getExecutedJobsProgress());
      progressBar.requestLayout();
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void onStatusMessage(@UIEventTopic(Topics.TOOLBAR_STATUS_MESSAGE) String message) {
    lblPermanentMessage.setText(message);
    Color oldColor = lblPermanentMessage.getForeground();
    // Highlight the message by changing the color and changing it back later
    lblPermanentMessage.setForeground(ColorPalette.VERMILLION);
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        sync.syncExec(() -> lblPermanentMessage.setForeground(oldColor));
      }
    }, 500);

  }
}
