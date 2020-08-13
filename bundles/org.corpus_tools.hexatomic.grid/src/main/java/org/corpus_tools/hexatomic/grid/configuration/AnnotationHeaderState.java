/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
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

package org.corpus_tools.hexatomic.grid.configuration;

import org.corpus_tools.hexatomic.grid.internal.GridHelper;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;

/**
 * A menu item state which is active on column indices > 1 (i.e., excluding the index and token text
 * columns).
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class AnnotationHeaderState implements IMenuItemState {

  @Override
  public boolean isActive(NatEventData natEventData) {
    System.err.println("pos " + natEventData.getColumnPosition());
    boolean ret = !GridHelper.isTokenColumnAtPosition(natEventData.getNatTable(),
        natEventData.getColumnPosition(), true);
    System.err.println("EH? " + ret);
    return ret;
  }

}
