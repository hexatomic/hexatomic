package org.eclipse.wb.swt;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.junit.jupiter.api.Test;

class TestSwtResourceManager {

  /**
   * Tests correct caching and disposal of fonts.
   */
  @Test
  public void testCacheAndDisposeFonts() {
    assertEquals(0, SwtResourceManager.fontMap.size());

    // Add first font
    Font font1 = SwtResourceManager.getFont(getDefaultFontName(), 12, SWT.NONE);
    assertNotNull(font1);
    assertEquals(getDefaultFontName(), font1.getFontData()[0].getName());
    assertEquals(12, font1.getFontData()[0].getHeight());
    assertEquals(SWT.NONE, font1.getFontData()[0].getStyle());
    assertEquals(1, SwtResourceManager.fontMap.size());

    
    // Add second font (a bold version of the same font)
    Font font2 = SwtResourceManager.getFont(getDefaultFontName(), 12, SWT.BOLD);
    assertNotNull(font2);
    assertEquals(getDefaultFontName(), font2.getFontData()[0].getName());
    assertEquals(12, font2.getFontData()[0].getHeight());
    assertEquals(SWT.BOLD, font2.getFontData()[0].getStyle());
    assertEquals(2, SwtResourceManager.fontMap.size());
    
    // Add third font (a larger version of the same font)
    Font font3 = SwtResourceManager.getFont(getDefaultFontName(), 18, SWT.NONE);
    assertNotNull(font3);
    assertEquals(getDefaultFontName(), font3.getFontData()[0].getName());
    assertEquals(18, font3.getFontData()[0].getHeight());
    assertEquals(SWT.NONE, font3.getFontData()[0].getStyle());
    assertEquals(3, SwtResourceManager.fontMap.size());

    // Add font again, this should not change the cache size
    Font cachedFont = SwtResourceManager.getFont(getDefaultFontName(), 12, SWT.BOLD);
    assertEquals(font2, cachedFont);
    assertEquals(3, SwtResourceManager.fontMap.size());
    
    // Add a bold version of the font using the helper method
    assertEquals(0, SwtResourceManager.fontToBoldFontMap.size());
    Font boldFont = SwtResourceManager.getBoldFont(font1);
    assertNotNull(boldFont);
    assertEquals(SWT.BOLD, boldFont.getFontData()[0].getStyle());
    assertEquals(1, SwtResourceManager.fontToBoldFontMap.size());

    // Dispose font resources and check that the map is empty
    SwtResourceManager.dispose();
    assertEquals(0, SwtResourceManager.fontMap.size());
    assertEquals(0, SwtResourceManager.fontToBoldFontMap.size());
  }
  
  /**
   * Tests correct caching and disposal of colors.
   */
  @Test
  public void testCacheAndDisposeColors() {
    assertEquals(0, SwtResourceManager.colorMap.size());

    // Add first color
    Color color1 = SwtResourceManager.getColor(128, 50, 255);
    assertNotNull(color1);
    assertEquals(128, color1.getRed());
    assertEquals(50, color1.getGreen());
    assertEquals(255, color1.getBlue());
    assertEquals(1, SwtResourceManager.colorMap.size());
    
    // Add second color
    Color color2 = SwtResourceManager.getColor(new RGB(100, 128, 0));
    assertNotNull(color2);
    assertEquals(100, color2.getRed());
    assertEquals(128, color2.getGreen());
    assertEquals(0, color2.getBlue());
    assertEquals(2, SwtResourceManager.colorMap.size());
    
    // Get same color again
    Color cachedColor = SwtResourceManager.getColor(new RGB(128, 50, 255));
    assertEquals(color1, cachedColor);
    assertEquals(2, SwtResourceManager.colorMap.size());
        

    // Dispose color resources and check that the map is empty
    SwtResourceManager.dispose();
    assertEquals(0, SwtResourceManager.colorMap.size());
  }

  
  private String getDefaultFontName() {
    if(SystemUtils.IS_OS_MAC_OSX) {
      return ".AppleSystemUIFont";
    } else {
      return "Sans";
    }
  }
  
}
