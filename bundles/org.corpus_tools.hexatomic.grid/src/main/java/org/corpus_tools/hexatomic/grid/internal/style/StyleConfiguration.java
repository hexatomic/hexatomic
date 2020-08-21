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

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * A custom style configuration for the {@link NatTable}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class StyleConfiguration extends DefaultNatTableStyleConfiguration {

  public static final String EMPTY_CELL_STYLE = "EMPTY_CELL_STYLE";
  public static final String SPAN_ANNOTATION_CELL_STYLE = "SPAN_ANNOTATION_CELL_STYLE";
  public static final String TOKEN_TEXT_CELL_STYLE = "TOKEN_TEXT_CELL_STYLE";

  @Override
  public void configureRegistry(IConfigRegistry configRegistry) {
    // Configure as per defaults
    super.configureRegistry(configRegistry);

    // Create a new style to apply to empty cells
    Style emptyCellStyle = new Style();
    emptyCellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
        GUIHelper.getColor(236, 236, 236));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, emptyCellStyle,
        DisplayMode.NORMAL, StyleConfiguration.EMPTY_CELL_STYLE);

    // Create a new style to apply to cells containing span annotation values (green text color)
    Style spanAnnotationCellStyle = new Style();
    spanAnnotationCellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        GUIHelper.getColor(35, 124, 82));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, spanAnnotationCellStyle,
        DisplayMode.NORMAL, StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE);

    // Create a new style to apply to cells containing token text (italic font)
    Style tokenTextCellStyle = new Style();
    FontData[] defaultFontData = GUIHelper.DEFAULT_FONT.getFontData();
    for (FontData fontDate : defaultFontData) {
      fontDate.setStyle(SWT.ITALIC);
    }
    tokenTextCellStyle.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(defaultFontData));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, tokenTextCellStyle,
        DisplayMode.NORMAL, StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
  }


}
