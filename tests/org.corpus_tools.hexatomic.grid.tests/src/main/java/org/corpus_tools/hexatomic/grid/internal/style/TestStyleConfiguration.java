package org.corpus_tools.hexatomic.grid.internal.style;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link StyleConfiguration}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestStyleConfiguration {

  private StyleConfiguration fixture = null;

  /**
   * Sets up the fixture.
   */
  @BeforeEach
  void setUp() {
    fixture = new StyleConfiguration();
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.style.StyleConfiguration#configureRegistry(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)}.
   */
  @Test
  void testConfigureRegistryIConfigRegistry() {
    ConfigRegistry registry = new ConfigRegistry();
    fixture.configureRegistry(registry);
    IStyle emptyCellStyle = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
        DisplayMode.NORMAL, StyleConfiguration.EMPTY_CELL_STYLE);
    IStyle spanAnnotationStyle = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
        DisplayMode.NORMAL, StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE);
    IStyle tokenTextStyle = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
        DisplayMode.NORMAL, StyleConfiguration.TOKEN_TEXT_CELL_STYLE);

    assertEquals(GUIHelper.getColor(236, 236, 236),
        emptyCellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
    assertEquals(GUIHelper.getColor(35, 124, 82),
        spanAnnotationStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
    Font tokenFont = tokenTextStyle.getAttributeValue(CellStyleAttributes.FONT);
    for (FontData fontDate : tokenFont.getFontData()) {
      assertEquals(SWT.ITALIC, fontDate.getStyle());
    }
  }

}
