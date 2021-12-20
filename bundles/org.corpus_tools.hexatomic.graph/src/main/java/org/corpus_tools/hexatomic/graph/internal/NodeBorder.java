/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.graph.internal;

import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

/**
 * A border to display the annotation nodes and tokens.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class NodeBorder extends CompoundBorder {


  private static final int MARGIN = 3;
  private final LineBorder line;

  /**
   * Constructor of this class.
   * 
   * @param caption The label to use as caption.
   * @param fontHeight Height of the used font in pixels, 
   *        used to determine the padding between the node name
   *        and the annotation values.
   */
  public NodeBorder(String caption, int fontHeight) {
    TitleBarBorder title = new TitleBarBorder(caption);
    title.setBackgroundColor(ColorConstants.white);
    title.setTextColor(ColorPalette.GRAY);
    // Add padding based on font height
    title.setPadding(new Insets(0, 0, Math.round(((float) fontHeight) * 0.4f), 0));

    line = new LineBorder(3);
    line.setStyle(Graphics.LINE_SOLID);
    
    org.eclipse.draw2d.MarginBorder margin = new org.eclipse.draw2d.MarginBorder(MARGIN);
    
    inner = new CompoundBorder(margin, title);
    outer = line;

  }

  /**
   * Sets the line type of this border.
   * 
   * @param style For the list of valid values, see {@link org.eclipse.draw2d.Graphics}.
   */
  public void setLineStyle(int style) {
    line.setStyle(style);
  }

  /**
   * Sets the line color for this border.
   * 
   * @param color The line color.
   */
  public void setLineColor(Color color) {
    line.setColor(color);
  }
}
