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

import org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider;
import org.corpus_tools.hexatomic.grid.internal.data.GridDisplayConverter;
import org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.command.DeleteSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

/**
 * Configures editing on the grid editor.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class EditConfiguration extends AbstractRegistryConfiguration {

  private static final String CELL_EDITOR = "CELL_EDITOR";
  private static final String TOKEN_COLUMN_CONFIG_LABEL = "TOKEN_COLUMN_CONFIG_LABEL";
  private static final String CONVERTED_COLUMN_LABEL = "CONVERTED_COLUMN_LABEL";
  private final LabelAccumulator labelAccumulator;
  private final SelectionLayer selectionLayer;
  private final GraphDataProvider bodyDataProvider;

  /**
   * Constructor setting fields.
   * 
   * @param bodyDataProvider The current data provider for the table being configured
   * @param labelAccumulator The current label accumulator
   * @param selectionLayer The current selection layer
   */
  public EditConfiguration(GraphDataProvider bodyDataProvider, LabelAccumulator labelAccumulator,
      SelectionLayer selectionLayer) {
    this.bodyDataProvider = bodyDataProvider;
    this.labelAccumulator = labelAccumulator;
    this.selectionLayer = selectionLayer;
  }

  @Override
  public void configureRegistry(IConfigRegistry configRegistry) {
    // Make cells editable
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
        IEditableRule.ALWAYS_EDITABLE);
    registerCellEditor(configRegistry);
    // Disable token text editing
    disableTokenTextEditing(configRegistry);
    // Tag all columns with a label to mark them for value conversion
    labelAccumulator.registerOverrides(CONVERTED_COLUMN_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
        new GridDisplayConverter(this.bodyDataProvider), DisplayMode.NORMAL,
        CONVERTED_COLUMN_LABEL);
  }

  private void disableTokenTextEditing(IConfigRegistry configRegistry) {
    // Tag token column with label
    labelAccumulator.registerOverrides(0, TOKEN_COLUMN_CONFIG_LABEL);
    // Disable editing
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
        IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, TOKEN_COLUMN_CONFIG_LABEL);
  }

  private void registerCellEditor(IConfigRegistry configRegistry) {
    TextCellEditor textCellEditor = new TextCellEditor();
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, textCellEditor,
        DisplayMode.NORMAL, CELL_EDITOR);
  }

  @Override
  public void configureLayer(ILayer layer) {
    layer.registerCommandHandler(new DeleteSelectionCommandHandler(this.selectionLayer));
  }



}
