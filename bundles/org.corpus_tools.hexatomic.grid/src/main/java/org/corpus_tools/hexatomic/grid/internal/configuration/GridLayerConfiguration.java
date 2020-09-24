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

package org.corpus_tools.hexatomic.grid.internal.configuration;

import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.edit.action.DeleteSelectionAction;
import org.eclipse.nebula.widgets.nattable.edit.action.KeyEditAction;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.swt.SWT;

/**
 * Customization configuration for a {@link CompositeLayer}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GridLayerConfiguration extends DefaultGridLayerConfiguration {

  public GridLayerConfiguration(CompositeLayer gridLayer) {
    super(gridLayer);
  }

  @Override
  protected void addAlternateRowColoringConfig(CompositeLayer gridLayer) {
    // Not implemented to avoid alternating white/grey layout
  }

  /**
   * Overrides default behaviour for starting cell edits with mouse and keyboard, mainly disabling
   * (by omission) starting to edit on single click on a cell.
   */
  @Override
  protected void addEditingUIConfig() {
    addConfiguration(new AbstractUiBindingConfiguration() {
      @Override
      public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
        // Start editing on press of space key
        uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, 32),
            new KeyEditAction());
        // Start editing when user starts typing
        uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(),
            new KeyEditAction());
        // Start editing when user starts typing with Shift pressed
        uiBindingRegistry.registerKeyBinding(new LetterOrDigitKeyEventMatcher(SWT.SHIFT),
            new KeyEditAction());
        // Start typing on double-click (single click only selects
        uiBindingRegistry.registerFirstDoubleClickBinding(
            new CellEditorMouseEventMatcher(GridRegion.BODY), new MouseEditAction());

        // Delete annotation when user presses Delete key
        uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.DEL),
            new DeleteSelectionAction());
      }
    });
  }

}
