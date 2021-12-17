package org.corpus_tools.hexatomic.graph.internal;

import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.swt.graphics.Color;

/**
 * A border to display the annotation nodes and tokens.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 *
 */
public class NodeBorder extends CompoundBorder {


  private final LineBorder line;

  /**
   * Constructor
   * 
   * @param caption The label to use as caption.
   */
  public NodeBorder(String caption) {
    TitleBarBorder title = new TitleBarBorder(caption);
    title.setBackgroundColor(ColorConstants.white);
    title.setTextColor(ColorPalette.GRAY);

    line = new LineBorder();
    line.setStyle(Graphics.LINE_SOLID);

    inner = title;
    outer = line;

  }

  /**
   * Sets the line type of this border.
   * 
   * @param style For the list of valid values, see {@link org.eclipse.draw2d.Graphics}
   */
  public void setLineStyle(int style) {
    line.setStyle(style);
  }

  /**
   * Sets the line color for this border.
   * 
   * @param color The line color
   */
  public void setLineColor(Color color) {
    line.setColor(color);
  }
}
