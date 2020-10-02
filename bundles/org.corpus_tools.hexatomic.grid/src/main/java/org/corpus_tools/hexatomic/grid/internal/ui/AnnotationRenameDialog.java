/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
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

package org.corpus_tools.hexatomic.grid.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.style.editor.AbstractStyleEditorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog providing the user with the old values for annotation namepsace and annotation name, and
 * lets them input a new namespace value and a new name value. To remain compatible with the
 * original column header rename logic and the respective dialog, this dialog splits the qualified
 * name into its components only for the front end, while logic happens on the compound qualified
 * name, a single string.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class AnnotationRenameDialog extends AbstractStyleEditorDialog {

  private final String oldQName;
  private String newQName;
  private AnnotationLabelPanel columnLabelPanel;

  private static final Logger log = LoggerFactory.getLogger(AnnotationRenameDialog.class);

  /**
   * Sets the old and new qualified annotation names to fields.
   * 
   * @param activeShell the active SWT shell widget
   * @param oldQName the old qualified annotation name
   * @param newQName the new qualified annotation name
   */
  public AnnotationRenameDialog(Shell activeShell, String oldQName) {
    super(activeShell);
    this.oldQName = oldQName;
  }

  @Override
  protected void initComponents(Shell shell) {
    GridLayout shellLayout = new GridLayout();
    shell.setLayout(shellLayout);
    shell.setText("Rename annotation"); //$NON-NLS-1$

    // Closing the window is the same as canceling the form
    shell.addShellListener(new ShellAdapter() {
      @Override
      public void shellClosed(ShellEvent e) {
        doFormCancel(shell);
      }
    });

    // Tabs panel
    Composite panel = new Composite(shell, SWT.NONE);
    panel.setLayout(new GridLayout());

    GridData fillGridData = new GridData();
    fillGridData.grabExcessHorizontalSpace = true;
    fillGridData.horizontalAlignment = GridData.FILL;
    panel.setLayoutData(fillGridData);

    this.columnLabelPanel = new AnnotationLabelPanel(panel, this.newQName);
    try {
      this.columnLabelPanel.edit(this.oldQName);
    } catch (Exception e) {
      log.warn("An error occurred!", e);
    }
  }

  @Override
  protected void doFormOK(Shell shell) {
    this.newQName = this.columnLabelPanel.getNewValue();
    shell.dispose();
  }

  @Override
  protected void doFormClear(Shell shell) {
    this.newQName = null;
    shell.dispose();
  }

  /**
   * Returns the new qualified annotation name.
   * 
   * @return the new qualified annotation name
   */
  public String getNewQName() {
    return this.newQName;
  }

  /**
   * Create OK and Cancel buttons, unlike in the super class, no Clear button is added.
   */
  @Override
  protected void createButtons(final Shell shell) {
    final Composite buttonPanel = new Composite(shell, SWT.NONE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    gridLayout.makeColumnsEqualWidth = false;
    gridLayout.horizontalSpacing = 2;
    buttonPanel.setLayout(gridLayout);

    GridDataFactory.fillDefaults().grab(true, true).applyTo(buttonPanel);

    Button okButton = new Button(buttonPanel, SWT.PUSH);
    okButton.setText("OK"); //$NON-NLS-1$
    okButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        doFormOK(shell);
      }
    });
    GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.BOTTOM).minSize(70, 25).grab(true, true)
        .applyTo(okButton);
    this.columnLabelPanel.setOkButton(okButton);

    Button cancelButton = new Button(buttonPanel, SWT.NONE);
    cancelButton.setText("Cancel"); //$NON-NLS-1$
    cancelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        doFormCancel(shell);
      }
    });
    GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.BOTTOM).minSize(80, 25).grab(false, false)
        .applyTo(cancelButton);

    shell.setDefaultButton(okButton);
  }


}
