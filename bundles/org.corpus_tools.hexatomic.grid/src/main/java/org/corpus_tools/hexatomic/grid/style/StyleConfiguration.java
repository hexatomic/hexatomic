package org.corpus_tools.hexatomic.grid.style;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

/**
 * A custom style configuration for the {@link NatTable}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class StyleConfiguration extends DefaultNatTableStyleConfiguration {

  static final String EMPTY_CELL_STYLE = "EMPTY_CELL_STYLE";

  @Override
  public void configureRegistry(IConfigRegistry configRegistry) {
    // Configure as per defaults
    super.configureRegistry(configRegistry);
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        GUIHelper.getColor(236, 236, 236));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.NORMAL, StyleConfiguration.EMPTY_CELL_STYLE);
  }


}
