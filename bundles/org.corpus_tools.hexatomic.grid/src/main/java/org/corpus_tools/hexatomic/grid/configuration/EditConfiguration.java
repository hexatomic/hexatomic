package org.corpus_tools.hexatomic.grid.configuration;

import org.corpus_tools.hexatomic.grid.style.LabelAccumulator;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
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
  private LabelAccumulator labelAccumulator;

  public EditConfiguration(LabelAccumulator labelAccumulator) {
    this.labelAccumulator = labelAccumulator;
  }

  /**
   * TODO
   */
  @Override
  public void configureRegistry(IConfigRegistry configRegistry) {
    // Make cells editable
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
        IEditableRule.ALWAYS_EDITABLE);
    registerCellEditor(configRegistry);
    // Disable token text editing
    disableTokenTextEditing(configRegistry);
  }

  private void disableTokenTextEditing(IConfigRegistry configRegistry) {
    labelAccumulator.registerOverrides(0, TOKEN_COLUMN_CONFIG_LABEL);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
        IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, TOKEN_COLUMN_CONFIG_LABEL);
  }

  private void registerCellEditor(IConfigRegistry configRegistry) {
    TextCellEditor textCellEditor = new TextCellEditor();
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, textCellEditor,
        DisplayMode.NORMAL, CELL_EDITOR);
  }

}
