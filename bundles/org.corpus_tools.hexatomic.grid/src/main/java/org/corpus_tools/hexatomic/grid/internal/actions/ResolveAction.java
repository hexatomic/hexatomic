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

package org.corpus_tools.hexatomic.grid.internal.actions;

import org.corpus_tools.hexatomic.grid.GridHelper;
import org.corpus_tools.hexatomic.grid.internal.events.TriggerResolutionEvent;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * Resolves the model for a given {@link NatTable} from scratch.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ResolveAction implements IContextFreeAction, IKeyAction {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ResolveAction.class);

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    run(natTable);
  }

  @Override
  public void run(NatTable natTable) {
    log.trace("Firing event to trigger model resolution on body data layer");
    GridHelper.getBodyLayer(natTable).fireLayerEvent(new TriggerResolutionEvent());
  }

}
