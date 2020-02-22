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
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.UiStatusReport;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.UIEventTopic;
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

  @Inject
  UiStatusReport uiStatus;

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

  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void unloadDocumentGraphWhenClosed(@UIEventTopic(Topics.STATUS_UPDATE) String ignore) {

    lblMessage.setText(uiStatus.getExecutedJobStatusMessage());

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
}
