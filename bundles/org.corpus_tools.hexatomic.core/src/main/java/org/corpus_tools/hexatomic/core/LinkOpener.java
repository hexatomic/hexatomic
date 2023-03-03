/*-
 * #%L
 * [bundle] Hexatomic Core Plugin
 * %%
 * Copyright (C) 2018 - 2023 Stephan Druskat, Thomas Krause
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
import org.eclipse.swt.program.Program;

/**
 * Service that allows to open links to web resources in the browser of the system.
 * 
 * 
 * <p>
 * Having this as an extra class allows to inject custom implementations, e.g. in integration tests
 * where no actual browser window should be opened.
 * </p>
 * *
 * 
 * @author Thomas Krause
 */
@Creatable
public class LinkOpener {


  /**
   * Opens a link in the browser.
   * 
   * @param url The url to the web resource. Must start with "http(s)://".
   */
  public void open(String url) {
    if (url.startsWith("http://") || url.startsWith("https://")) {
      Program.launch(url);
    }
  }

}
