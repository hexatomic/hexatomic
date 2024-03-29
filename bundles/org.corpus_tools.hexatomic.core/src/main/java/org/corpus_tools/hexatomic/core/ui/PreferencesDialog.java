/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.Preferences;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

public class PreferencesDialog extends Dialog {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(PreferencesDialog.class);
  IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  Button checkbox;
  @Inject
  ErrorService errorService;

  /**
   * Create the dialog.
   * 
   * @param parentShell The parent.
   */
  public PreferencesDialog(Shell parentShell) {
    super(parentShell);
  }


  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Preferences");
  }

  /**
   * Create contents of the dialog.
   * 
   * @param parent The parent
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);

    checkbox = new Button(area, SWT.CHECK);
    checkbox.setText("Enable automatic update checks");
    checkbox.setSelection(prefs.getBoolean(Preferences.AUTO_UPDATE, true));
    Label label = new Label(area, SWT.BORDER | SWT.WRAP);
    label.setText("When checked, Hexatomic will automatically check for updates at each start.\n"
        + "For this function to work, it needs to establish a  network connection\n "
        + "to hexatomic.github.io (hosted by GitHub, Inc.) at every startup. ");
    return area;
  }

  @Override
  protected void okPressed() {
    if (prefs != null) {
      prefs.putBoolean(Preferences.AUTO_UPDATE, checkbox.getSelection());
      try {
        prefs.flush();
      } catch (BackingStoreException ex) {
        errorService.handleException("Couldn't update preferences", ex, PreferencesDialog.class);
      }
    } else {
      log.info("Path to preferences not found");

    }
    super.okPressed();
  }
}

