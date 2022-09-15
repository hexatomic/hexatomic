/*******************************************************************************
 * Copyright (c) 2011 Google, Inc. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * <p>
 * Contributors: Google, Inc. - initial API and implementation
 * </p>
 *******************************************************************************/

package org.eclipse.wb.swt;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for managing OS resources associated with SWT controls such as colors, fonts, etc.
 * 
 * <p>
 * !!! IMPORTANT !!! Application code must explicitly invoke the <code>dispose()</code> method to
 * release the operating system resources managed by cached objects when those objects and OS
 * resources are no longer needed (e.g. on application shutdown)
 * </p>
 * 
 * <p>
 * This class may be freely distributed as part of any application or plugin.
 * </p>
 * 
 * @author scheglov_ke
 * @author Dan Rubel
 */
public class SwtResourceManager {

  private SwtResourceManager() {
    throw new IllegalStateException("Utility class that should not be instantiated");
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Color
  //
  ////////////////////////////////////////////////////////////////////////////
  protected static Map<RGB, Color> colorMap = new HashMap<>();

  /**
   * Returns a {@link Color} given its red, green and blue component values.
   * 
   * @param r the red component of the color
   * @param g the green component of the color
   * @param b the blue component of the color
   * @return the {@link Color} matching the given red, green and blue component values
   */
  public static Color getColor(int r, int g, int b) {
    return getColor(new RGB(r, g, b));
  }

  /**
   * Returns a {@link Color} given its RGB value.
   * 
   * @param rgb the {@link RGB} value of the color
   * @return the {@link Color} matching the RGB value
   */
  public static Color getColor(RGB rgb) {
    return colorMap.computeIfAbsent(rgb, key -> new Color(Display.getCurrent(), key));
  }

  /**
   * Dispose of all the cached {@link Color}'s.
   */
  public static void disposeColors() {
    for (Color color : colorMap.values()) {
      color.dispose();
    }
    colorMap.clear();
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Font
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Maps font names to fonts.
   */
  protected static Map<String, Font> fontMap = new HashMap<>();
  /**
   * Maps fonts to their bold versions.
   */
  protected static Map<Font, Font> fontToBoldFontMap = new HashMap<>();

  /**
   * Returns a {@link Font} based on its name, height and style.
   * 
   * @param name the name of the font
   * @param height the height of the font
   * @param style the style of the font
   * @return {@link Font} The font matching the name, height and style
   */
  public static Font getFont(String name, int height, int style) {
    String fontName = name + '|' + height + '|' + style;
    return fontMap.computeIfAbsent(fontName,
        fn -> new Font(Display.getCurrent(), new FontData(name, height, style)));
  }


  /**
   * Returns a bold version of the given {@link Font}.
   * 
   * @param baseFont the {@link Font} for which a bold version is desired
   * @return the bold version of the given {@link Font}
   */
  public static Font getBoldFont(Font baseFont) {
    return fontToBoldFontMap.computeIfAbsent(baseFont, b -> {
      FontData[] fontDatas = b.getFontData();
      FontData data = fontDatas[0];
      return new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
    });
  }

  /**
   * Dispose all of the cached {@link Font}'s.
   */
  public static void disposeFonts() {
    // clear fonts
    for (Font font : fontMap.values()) {
      font.dispose();
    }
    fontMap.clear();
    // clear bold fonts
    for (Font font : fontToBoldFontMap.values()) {
      font.dispose();
    }
    fontToBoldFontMap.clear();
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // General
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Dispose of cached objects and their underlying OS resources. This should only be called when
   * the cached objects are no longer needed (e.g. on application shutdown).
   */
  public static void dispose() {
    disposeColors();
    disposeFonts();
  }
}
