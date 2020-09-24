package org.corpus_tools.hexatomic.grid.internal.style;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SelectionStyleConfiguration}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class TestSelectionStyleConfiguration {

  private static SelectionStyleConfiguration fixture = null;

  /**
   * Sets up the test fixture.
   * 
   * @throws java.lang.Exception Any old exception that may be thrown
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new SelectionStyleConfiguration();
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.style.SelectionStyleConfiguration#configureSelectionStyle(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)}.
   * 
   */
  @Test
  final void testConfigureSelectionStyleIConfigRegistry() {
    IConfigRegistry registry = new ConfigRegistry();
    fixture.configureSelectionStyle(registry);

    IStyle style = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE, DisplayMode.SELECT);
    for (FontData fontDate : style.getAttributeValue(CellStyleAttributes.FONT).getFontData()) {
      assertEquals(SWT.BOLD, fontDate.getStyle());
    }
    assertEquals(GUIHelper.COLOR_WIDGET_BACKGROUND,
        style.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
    assertEquals(GUIHelper.COLOR_BLACK,
        style.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.style.SelectionStyleConfiguration#configureSelectionAnchorStyle(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)}.
   */
  @Test
  final void testConfigureSelectionAnchorStyleIConfigRegistry() {
    IConfigRegistry registry = new ConfigRegistry();
    fixture.configureSelectionAnchorStyle(registry);


    IStyle normalStyle = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
        DisplayMode.NORMAL, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
    assertEquals(1, normalStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getThickness());
    assertEquals(GUIHelper.COLOR_DARK_GRAY,
        normalStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getColor());
    assertEquals(LineStyleEnum.SOLID,
        normalStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getLineStyle());

    IStyle selectionStyle = registry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
        DisplayMode.SELECT, SelectionStyleLabels.SELECTION_ANCHOR_STYLE);
    assertEquals(1,
        selectionStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getThickness());
    assertEquals(GUIHelper.COLOR_DARK_GRAY,
        selectionStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getColor());
    assertEquals(LineStyleEnum.SOLID,
        selectionStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE).getLineStyle());
    assertEquals(GUIHelper.COLOR_DARK_GRAY,
        selectionStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
    assertEquals(GUIHelper.COLOR_WHITE,
        selectionStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
  }

}
