/*-
 * #%L
 * org.corpus_tools.hexatomic.formats
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.util.Optional;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

public class CorpusPathSelectionPage extends WizardPage implements IWizardPage {
  private Text txtDirectoryPath;

  public enum Type {
    Import, Export
  }

  /**
   * Creates a new wizard page to select a corpus path.
   * 
   * @param type Choose whether this is an import or export wizard.
   */
  public CorpusPathSelectionPage(Type type) {
    super("Select corpus directory");
    switch (type) {
      case Import:
        setTitle("Select the directory that contains the corpus you want to import");
        setDescription(
            "Corpora are normally organized as collection of files and "
                + "sub-directories of a parent directory.");
        break;
      case Export:
        setTitle("Select the directory to where you want to export the corpus to");
        break;
      default:
        setTitle("Invalid page");
        break;
    }
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    setControl(container);
    container.setLayout(new GridLayout(2, false));

    txtDirectoryPath = new Text(container, SWT.BORDER);
    txtDirectoryPath.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        setPageComplete(getCorpusPath().isPresent());
      }
    });
    GridData txtDirectoryPathGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    txtDirectoryPathGridData.widthHint = 474;
    txtDirectoryPath.setLayoutData(txtDirectoryPathGridData);

    Button btnSelectDirectory = new Button(container, SWT.NONE);
    btnSelectDirectory.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DirectoryDialog corpusDir = new DirectoryDialog(parent.getShell());
        corpusDir.setText("Select the corpus directory");
        String result = corpusDir.open();
        if (result != null) {
          txtDirectoryPath.setText(result);
        }
      }
    });
    btnSelectDirectory.setText("...");
    setPageComplete(false);
  }

  /**
   * Get the directory selected by the user.
   * 
   * @return The path or {{@link Optional#empty()} if the selected path is not a directory.
   */
  public Optional<File> getCorpusPath() {
    if (!txtDirectoryPath.getText().isEmpty()) {
      // Check if the path exists and is a directory
      File f = new File(txtDirectoryPath.getText());
      if (f.isDirectory()) {
        return Optional.of(f);
      }
    }
    return Optional.empty();
  }
}
