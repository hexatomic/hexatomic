/*-
 * #%L
 * org.corpus_tools.hexatomic.core
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

package org.corpus_tools.hexatomic.core;

/**
 * Contains constants for preferences used by the core bundle.
 * 
 * @author Thomas Krause
 */
public final class Preferences {

  private Preferences() {
    // Private (but empty) constructor to avoid instantiation of this utility class.
  }

  /** Whether to automatically check for updates. **/
  public static final String AUTO_UPDATE = "autoUpdate";

  /** Set temporarily after an update was applied, so we know not check for newer updates. */
  public static final String JUST_UPDATED = "justUpdated";

  /** Location of the last opened project. */
  public static final String LAST_PROJECT_LOCATION = "lastProjectLocation";

}
