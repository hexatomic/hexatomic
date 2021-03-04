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
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

public class CorpusPathSelectionPage extends WizardPage implements IWizardPage {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(CorpusPathSelectionPage.class);


  private Text txtDirectoryPath;
  private File presetCorpusPath;

  public enum Type {
    IMPORT, EXPORT
  }

  private final Type type;

  /**
   * Creates a new wizard page to select a corpus path.
   * 
   * @param type Choose whether this is an import or export wizard.
   */
  public CorpusPathSelectionPage(Type type) {
    super("Select corpus directory");
    this.type = type;
    switch (type) {
      case IMPORT:
        setTitle("Select the directory that contains the corpus you want to import");
        setDescription("Corpora are normally organized as collection of files and "
            + "sub-directories of a parent directory.");
        break;
      case EXPORT:
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
    setPageComplete(false);

    txtDirectoryPath = new Text(container, SWT.BORDER);
    txtDirectoryPath.addModifyListener(e -> setPageComplete(getCorpusPath().isPresent()));

    if (presetCorpusPath != null) {
      txtDirectoryPath.setText(presetCorpusPath.getAbsolutePath());
      setPageComplete(getCorpusPath().isPresent());
    }

    GridData txtDirectoryPathGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    txtDirectoryPathGridData.widthHint = 474;
    txtDirectoryPath.setLayoutData(txtDirectoryPathGridData);

    Button btnSelectDirectory = new Button(container, SWT.NONE);
    btnSelectDirectory.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DirectoryDialog corpusDir = new DirectoryDialog(parent.getShell());
        corpusDir.setText("Select the corpus directory");
        Optional<File> path = getCorpusPath();
        if (path.isPresent()) {
          corpusDir.setFilterPath(path.get().getAbsolutePath());
        }
        String result = corpusDir.open();
        if (result != null) {
          txtDirectoryPath.setText(result);
        }
      }
    });
    btnSelectDirectory.setText("...");
  }

  /**
   * Sets a pre-selected corpus path from the "corpus-origin" feature annotation if it exists.
   * 
   * @param saltProject The Salt project to get the feature annotation from.
   */
  public void setCorpusPathFromProject(SaltProject saltProject) {
    Optional<String> originalLocation =
        SaltHelper.getOriginalCorpusLocation(saltProject);

    if (originalLocation.isPresent()) {
      File f = new File(originalLocation.get());
      if (f.exists()) {

        if (type == Type.EXPORT) {
          // We use the parent folder when exporting, since the root corpus name is a sub-folder of
          // this path
          f = f.getParentFile();
        }

        log.debug("Setting {} as pre-set original corpus location in wizard",
            f.getAbsolutePath());
        presetCorpusPath = f.getAbsoluteFile();
        if (txtDirectoryPath != null) {
          txtDirectoryPath.setText(f.getAbsolutePath());
        }
      } else {
        log.debug("Non-existing original location ignored as pre-set wizard location {}",
            f.getAbsolutePath());
      }
    } else {
      log.debug("No original location set for current corpus");
    }
  }



  /**
   * Get the directory selected by the user.
   * 
   * @return The path or {{@link Optional#empty()} if the selected path is not a directory.
   */
  public Optional<File> getCorpusPath() {
    if (!txtDirectoryPath.getText().isEmpty()) {
      File f = new File(txtDirectoryPath.getText());
      // For export we can choose any (non-existing) path, otherwise the file should exit
      if (type == Type.EXPORT || f.exists()) {
        return Optional.of(f);
      }
    }
    return Optional.empty();
  }
}
