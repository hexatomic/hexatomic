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

package org.corpus_tools.hexatomic.grid.internal.bindings;

import org.corpus_tools.hexatomic.grid.internal.actions.ResolveAction;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.freeze.action.FreezeGridAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;

/**
 * Key bindings to toggle freeze of a section of the grid.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class FreezeGridBindings extends AbstractUiBindingConfiguration implements IConfiguration {

  @Override
  public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
    // Shift + Alt + F toggles freeze
    uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.ALT | SWT.MOD2, 'f'),
        new FreezeGridAction(true));
    uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.F5), new ResolveAction());
  }

}
