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

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A class that can be injected to create a {@link WizardDialog}.
 * 
 * <p>
 * Having this as an extra class allows to inject custom dialog providers, e.g. for mocked dialogs
 * in tests.
 * </p>
 *
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
public class WizardDialogProvider {

  /**
   * Creates a new wizard dialog.
   * 
   * @param shell The SWT shell to use as parent
   * @param exportWizard The acual export wizard instance.
   * @return The created dialog.
   */
  public WizardDialog createDialog(Shell shell, IWizard exportWizard) {
    return new WizardDialog(shell, exportWizard);
  }

}
