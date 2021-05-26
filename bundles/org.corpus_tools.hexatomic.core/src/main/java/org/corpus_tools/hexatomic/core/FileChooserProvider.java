/*-
 * #%L
 * org.corpus_tools.hexatomic.core
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

package org.corpus_tools.hexatomic.core;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A class that can be injected to create a {@link DirectoryDialog}.
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
public class FileChooserProvider {

  /**
   * Creates a new directory dialog.
   * 
   * @param shell The SWT shell to use as parent
   * @return The created dialog.
   */
  public DirectoryDialog createDirectoryDialog(Shell shell) {
    return new DirectoryDialog(shell);
  }
}
