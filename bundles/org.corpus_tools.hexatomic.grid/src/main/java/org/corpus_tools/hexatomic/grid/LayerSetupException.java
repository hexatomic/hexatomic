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

package org.corpus_tools.hexatomic.grid;

import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * A runtime exception for errors thrown when a bad setup of {@link ILayer}s in the {@link NatTable}
 * is detected.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class LayerSetupException extends HexatomicRuntimeException {

  private static final long serialVersionUID = 1L;

  public LayerSetupException(String layerDescription, ILayer offendingObject,
      Class<?> expectedClass) {
    super(getMessage(layerDescription, offendingObject, expectedClass));
  }

  private static String getMessage(String layerDescription, ILayer offendingObject,
      Class<?> expectedClass) {
    return layerDescription + " is not of type " + expectedClass.getSimpleName()
        + " as expected! Please report this as a bug.\nOffending layer: "
        + offendingObject.toString();
  }

}
