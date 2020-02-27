package org.corpus_tools.hexatomic.grid.style;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * Style configutation for selected cells in the grid editor.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class SelectionStyleConfiguration extends DefaultSelectionStyleConfiguration {

  private final Font selectionFont;
  private final Color selectionBgColor = GUIHelper.COLOR_GRAY;
  private final Color anchorBgColor = GUIHelper.COLOR_DARK_GRAY;
  private final Color anchorFgColor = GUIHelper.COLOR_WHITE;

  public SelectionStyleConfiguration() {
    FontData[] defaultFontData = GUIHelper.DEFAULT_FONT.getFontData();
    for (FontData fontDate : defaultFontData) {
      fontDate.setStyle(SWT.BOLD);
    }
    selectionFont = GUIHelper.getFont(defaultFontData);
  }

  @Override
  protected void configureSelectionStyle(IConfigRegistry configRegistry) {
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, this.selectionFont);
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.selectionBgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.selectionFgColor);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.SELECT);
  }

  @Override
  protected void configureSelectionAnchorStyle(IConfigRegistry configRegistry) {
    // Selection anchor style for normal display mode
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.NORMAL, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);

    // Selection anchor style for select display mode
    cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.anchorBgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.anchorFgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.SELECT, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
  }

}
