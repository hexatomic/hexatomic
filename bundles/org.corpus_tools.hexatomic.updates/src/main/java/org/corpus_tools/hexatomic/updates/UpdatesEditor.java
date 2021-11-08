/*-
 * #%L
 * org.corpus_tools.hexatomic.updates
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

package org.corpus_tools.hexatomic.updates;

import javax.annotation.PostConstruct;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class UpdatesEditor {
  
  /**
   * Creates a new text viewer.
   * 
   * @param parent The parent SWT object
   * @param part blabla44
   * @param projectManager blbalba
   */
  @PostConstruct
  public void postConstruct(Composite parent, MPart part, ProjectManager projectManager) {
    parent.setLayout(new FillLayout(SWT.HORIZONTAL));   
    System.out.println("Hello");
    
  }

}
