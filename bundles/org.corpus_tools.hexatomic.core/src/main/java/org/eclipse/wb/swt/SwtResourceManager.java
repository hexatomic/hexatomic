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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for managing OS resources associated with SWT controls such as colors, fonts,
 * images, etc.
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
public abstract class SwtResourceManager {
  ////////////////////////////////////////////////////////////////////////////
  //
  // Color
  //
  ////////////////////////////////////////////////////////////////////////////
  private static Map<RGB, Color> colorMap = new HashMap<>();

  /**
   * Returns the system {@link Color} matching the specific ID.
   * 
   * @param systemColorID the ID value for the color
   * @return the system {@link Color} matching the specific ID
   */
  public static Color getColor(int systemColorID) {
    Display display = Display.getCurrent();
    return display.getSystemColor(systemColorID);
  }

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
    Color color = colorMap.computeIfAbsent(rgb, key -> {
      Display display = Display.getCurrent();
      return new Color(display, key);
    });
    return color;
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
  // Image
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Maps image paths to images.
   */
  private static Map<String, Image> imageMap = new HashMap<>();

  /**
   * Returns an {@link Image} encoded by the specified {@link InputStream}.
   * 
   * @param stream the {@link InputStream} encoding the image data
   * @return the {@link Image} encoded by the specified input stream
   */
  protected static Image getImage(InputStream stream) throws IOException {
    try {
      Display display = Display.getCurrent();
      ImageData data = new ImageData(stream);
      if (data.transparentPixel > 0) {
        return new Image(display, data, data.getTransparencyMask());
      }
      return new Image(display, data);
    } finally {
      stream.close();
    }
  }

  /**
   * Returns an {@link Image} stored in the file at the specified path.
   * 
   * @param path the path to the image file
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(String path) {
    Image image = imageMap.get(path);
    if (image == null) {
      try {
        image = getImage(new FileInputStream(path));
        imageMap.put(path, image);
      } catch (Exception e) {
        image = getMissingImage();
        imageMap.put(path, image);
      }
    }
    return image;
  }

  /**
   * Returns an {@link Image} stored in the file at the specified path relative to the specified
   * class.
   * 
   * @param clazz the {@link Class} relative to which to find the image
   * @param path the path to the image file, if starts with <code>'/'</code>
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(Class<?> clazz, String path) {
    String key = clazz.getName() + '|' + path;
    Image image = imageMap.get(key);
    if (image == null) {
      try {
        image = getImage(clazz.getResourceAsStream(path));
        imageMap.put(key, image);
      } catch (Exception e) {
        image = getMissingImage();
        imageMap.put(key, image);
      }
    }
    return image;
  }

  private static final int MISSING_IMAGE_SIZE = 10;

  /**
   * Returns default placeholde missing image.
   * 
   * @return the small {@link Image} that can be used as placeholder for missing image.
   */
  private static Image getMissingImage() {
    Image image = new Image(Display.getCurrent(), MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
    //
    GC gc = new GC(image);
    gc.setBackground(getColor(SWT.COLOR_RED));
    gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
    gc.dispose();
    //
    return image;
  }

  /**
   * Style constant for placing decorator image in top left corner of base image.
   */
  public static final int TOP_LEFT = 1;
  /**
   * Style constant for placing decorator image in top right corner of base image.
   */
  public static final int TOP_RIGHT = 2;
  /**
   * Style constant for placing decorator image in bottom left corner of base image.
   */
  public static final int BOTTOM_LEFT = 3;
  /**
   * Style constant for placing decorator image in bottom right corner of base image.
   */
  public static final int BOTTOM_RIGHT = 4;
  /**
   * Internal value.
   */
  protected static final int LAST_CORNER_KEY = 5;


  private static void disposeLoadedImages() {
    for (Image image : imageMap.values()) {
      image.dispose();
    }
    imageMap.clear();
  }

  /**
   * Dispose all of the cached {@link Image}'s.
   */
  public static void disposeImages() {
    disposeLoadedImages();
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Font
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Maps font names to fonts.
   */
  private static Map<String, Font> fontMap = new HashMap<>();
  /**
   * Maps fonts to their bold versions.
   */
  private static Map<Font, Font> fontToBoldFontMap = new HashMap<>();

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
    Font font = fontMap.computeIfAbsent(fontName, fn -> {
      FontData fontData = new FontData(name, height, style);
      return new Font(Display.getCurrent(), fontData);
    });
    return font;
  }


  /**
   * Returns a bold version of the given {@link Font}.
   * 
   * @param baseFont the {@link Font} for which a bold version is desired
   * @return the bold version of the given {@link Font}
   */
  public static Font getBoldFont(Font baseFont) {
    Font font = fontToBoldFontMap.computeIfAbsent(baseFont, b -> {
      FontData[] fontDatas = b.getFontData();
      FontData data = fontDatas[0];
      return new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
    });

    return font;
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
  // Cursor
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Maps IDs to cursors.
   */
  private static Map<Integer, Cursor> idToCursorMap = new HashMap<>();

  /**
   * Returns the system cursor matching the specific ID.
   * 
   * @param id int The ID value for the cursor
   * @return Cursor The system cursor matching the specific ID
   */
  public static Cursor getCursor(int id) {
    return idToCursorMap.computeIfAbsent(Integer.valueOf(id),
        key -> new Cursor(Display.getDefault(), key));
  }

  /**
   * Dispose all of the cached cursors.
   */
  public static void disposeCursors() {
    for (Cursor cursor : idToCursorMap.values()) {
      cursor.dispose();
    }
    idToCursorMap.clear();
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
    disposeImages();
    disposeFonts();
    disposeCursors();
  }
}
