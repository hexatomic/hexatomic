/*-
 * #%L
 * [bundle] Hexatomic Grid Editor
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

package org.corpus_tools.hexatomic.grid.internal.events;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;

/**
 * Signals that a full model resolution should be triggered.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class TriggerResolutionEvent implements ILayerEvent {

  @Override
  public boolean convertToLocal(ILayer localLayer) {
    // Unimplemented
    return false;
  }

  @Override
  public ILayerEvent cloneEvent() {
    return this;
  }

}
