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

package org.corpus_tools.hexatomic.grid.internal.style;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * Style configutation for selected cells in the grid editor.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class SelectionStyleConfiguration extends DefaultSelectionStyleConfiguration {

  /**
   * Constructs a new instance with default values.
   */
  public SelectionStyleConfiguration() {
    this.selectionBgColor = GUIHelper.COLOR_WIDGET_BACKGROUND;
    this.anchorBgColor = GUIHelper.COLOR_DARK_GRAY;
    this.anchorFgColor = GUIHelper.COLOR_WHITE;
  }


  @Override
  protected void configureSelectionStyle(IConfigRegistry configRegistry) {
    Style cellStyle = new Style();
    FontData[] defaultFontData = GUIHelper.DEFAULT_FONT.getFontData();
    for (FontData fontDate : defaultFontData) {
      fontDate.setStyle(SWT.BOLD);
    }
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(defaultFontData));
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.selectionBgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.selectionFgColor);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.SELECT);
  }

  @Override
  protected void configureSelectionAnchorStyle(IConfigRegistry configRegistry) {
    // Selection anchor style in cell style for normal display mode
    Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.NORMAL, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);

    // Set complete cell style for select display mode
    cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.anchorBgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.anchorFgColor);
    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.anchorBorderStyle);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
        DisplayMode.SELECT, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
  }

}
