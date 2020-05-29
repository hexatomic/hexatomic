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

package org.corpus_tools.hexatomic.grid.ui;

import org.corpus_tools.hexatomic.grid.data.DataUtil;
import org.eclipse.nebula.widgets.nattable.style.editor.AbstractEditorPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provides a panel with two input fields, one for the new namespace value, and one for the new name
 * value. Despite this, it works on a single string, the compund qualified annotation name.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class AnnotationLabelPanel extends AbstractEditorPanel<String> {

  private final String oldQName;
  private Text namespaceField;
  private String newNamespace;
  private Text nameField;
  private String newName;

  /**
   * Passing the parent widget to the super class constructor, retrieving namespace and name values
   * from the compound string parameter and setting them to fields, and initializing the
   * construction of the panel.
   * 
   * @param parent The parent widget
   * @param oldQName the old qualified annotation name
   * @param newQName the new qualified annotation name
   */
  public AnnotationLabelPanel(Composite parent, String oldQName, String newQName) {
    super(parent, SWT.NONE);
    this.oldQName = oldQName;
    this.newNamespace = DataUtil.splitNamespaceFromQNameString(newQName);
    this.newName = DataUtil.splitNameFromQNameString(newQName);
    init();
  }

  private void init() {
    GridLayout gridLayout = new GridLayout(2, false);
    setLayout(gridLayout);

    String oldNamespace = DataUtil.splitNamespaceFromQNameString(this.oldQName);
    String oldName = DataUtil.splitNameFromQNameString(this.oldQName);
    if (oldNamespace == null) {
      oldNamespace = "";
    }
    if (oldName == null) {
      oldName = "";
    }

    // Namespace
    Label namespaceLabel = new Label(this, SWT.NONE);
    namespaceLabel.setText("Namespace: "); //$NON-NLS-1$

    this.namespaceField = new Text(this, SWT.BORDER);
    GridData gridData = new GridData(200, 15);
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    this.namespaceField.setLayoutData(gridData);
    this.namespaceField.setText(oldNamespace);

    // Name
    Label nameLabel = new Label(this, SWT.NONE);
    nameLabel.setText("Name: "); //$NON-NLS-1$

    this.nameField = new Text(this, SWT.BORDER);
    this.nameField.setLayoutData(gridData);
    this.nameField.setText(oldName);
    this.nameField.setFocus();
    this.nameField.selectAll();
  }

  @Override
  public void edit(String newQName) throws Exception {
    if (newQName != null && newQName.length() > 0) {
      this.namespaceField.setText(DataUtil.splitNamespaceFromQNameString(newQName));
      this.nameField.setText(DataUtil.splitNameFromQNameString(newQName));
      this.nameField.selectAll();
    }
  }

  @Override
  public String getEditorName() {
    return "Grid editor"; //$NON-NLS-1$
  }

  /**
   * Builds a new value string from the values of the text fields for namespace and name.
   */
  @Override
  public String getNewValue() {
    // Both fields are enabled, and neither field is null (i.e., contains at least the empty string)
    if (this.namespaceField.isEnabled() && this.nameField.isEnabled()
        && this.namespaceField.getText() != null && this.nameField != null) {
      return DataUtil.buildQName(this.namespaceField.getText(), this.nameField.getText());
    }
    return null;
  }
}
