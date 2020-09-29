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

import org.corpus_tools.hexatomic.grid.internal.data.DataUtil;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.nebula.widgets.nattable.style.editor.AbstractEditorPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

  private Text namespaceField;
  private Text nameField;
  private Button okButton;

  /**
   * Passing the parent widget to the super class constructor, retrieving namespace and name values
   * from the compound string parameter and setting them to fields, and initializing the
   * construction of the panel.
   * 
   * @param parent The parent widget
   * @param oldQName the old qualified annotation name
   * @param newQName the new qualified annotation name
   */
  public AnnotationLabelPanel(Composite parent, String newQName) {
    super(parent, SWT.NONE);
    init();
  }

  private void init() {
    GridLayout gridLayout = new GridLayout(2, false);
    setLayout(gridLayout);

    // Namespace
    Label namespaceLabel = new Label(this, SWT.NONE);
    namespaceLabel.setText("Namespace:"); //$NON-NLS-1$

    this.namespaceField = new Text(this, SWT.BORDER);
    GridData gridData = new GridData(200, 15);
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    this.namespaceField.setLayoutData(gridData);

    // Name
    Label nameLabel = new Label(this, SWT.NONE);
    nameLabel.setText("Name:"); //$NON-NLS-1$

    this.nameField = new Text(this, SWT.BORDER);
    this.nameField.setLayoutData(gridData);

    this.nameField.addModifyListener(new DecoratedModifyListener());
  }

  @Override
  public void edit(String oldQName) throws Exception {
    if (oldQName != null && oldQName.length() > 0) {
      this.namespaceField.setText(DataUtil.splitNamespaceFromQNameString(oldQName));
      this.nameField.setText(DataUtil.splitNameFromQNameString(oldQName));
      this.nameField.setFocus();
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
        && this.namespaceField.getText() != null) {
      return DataUtil.buildQName(this.namespaceField.getText(), this.nameField.getText());
    }
    return null;
  }

  void setOkButton(Button okButton) {
    this.okButton = okButton;
  }

  private void setOkButtonActive(boolean enableButton) {
    if (this.okButton != null) {
        this.okButton.setEnabled(enableButton);
      }
    }
  }

  /**
   * A modify listener controlling a field decoration.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private final class DecoratedModifyListener implements ModifyListener {

    private final ControlDecoration decoration;

    /**
     * Creates a modify listener with a field decoration.
     */
    public DecoratedModifyListener() {
      decoration = new ControlDecoration(nameField, SWT.LEFT | SWT.TOP);
      decoration.setDescriptionText("Please enter a valid annotation name.");
      FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
          .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
      decoration.setImage(fieldDecoration.getImage());
    }

    @Override
    public void modifyText(ModifyEvent e) {
      Text source = (Text) e.getSource();
      String name = source.getText();
      if (name.isEmpty()) {
        decoration.setDescriptionText("Annotation name can not be empty!");
        decoration.show();
        setOkButtonActive(false);
      } else if (name.contains(" ")) {
        decoration.setDescriptionText("Annotation name can not contain whitespaces!");
        decoration.show();
        setOkButtonActive(false);
      } else {
        decoration.hide();
        setOkButtonActive(true);
      }
    }
  }
}
