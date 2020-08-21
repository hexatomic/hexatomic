/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
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

package org.corpus_tools.hexatomic.grid.data;

import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * Converts the call value from {@link SToken} to the token text.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class TokenTextDisplayConverter extends DisplayConverter {

  @Override
  public Object canonicalToDisplayValue(Object canonicalValue) {
    if (canonicalValue instanceof SToken) {
      SToken token = (SToken) canonicalValue;
      return token.getGraph().getText(token);
    } else {
      throw new RuntimeException("Found a non-token in a column that is reserved for tokens!");
    }
  }

  @Override
  public Object displayToCanonicalValue(Object displayValue) {
    // Not implemented
    return null;
  }

}
