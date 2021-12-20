/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
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

package org.corpus_tools.hexatomic.graph.internal;

import java.util.TreeMap;
import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.widgets.Display;

public class NodeFigure extends Figure {


  private final String caption;
  private final Font boldFont;

  /**
   * Creates a new node figure for a given salt node.
   * 
   * @param item The salt node.
   * @param fontMetrics Metrics describing the font used to render the text.
   */
  public NodeFigure(SNode item, FontMetrics fontMetrics) {

    Font font = Display.getCurrent().getSystemFont();
    FontDescriptor boldDescriptor = FontDescriptor.createFrom(font).setStyle(SWT.BOLD);
    this.boldFont = boldDescriptor.createFont(Display.getCurrent());

    setFont(font);
    caption = item.getName();


    FlowLayout layout = new FlowLayout(false);
    setLayoutManager(layout);
    NodeBorder border = new NodeBorder(caption, fontMetrics.getHeight());

    setBorder(border);
    setOpaque(true);
    setBackgroundColor(ColorConstants.white);
    setForegroundColor(ColorConstants.black);

    if (item instanceof SSpan) {
      setForegroundColor(ColorPalette.BLUISH_GREEN);
      border.setLineStyle(Graphics.LINE_DOT);
    } else if (item instanceof SStructure) {
      setForegroundColor(ColorPalette.VERMILLION);
      border.setLineStyle(Graphics.LINE_SOLID);
    } else if (item instanceof SToken) {
      setForegroundColor(ColorConstants.black);
      border.setLineColor(ColorPalette.GRAY);
    }

    if (item instanceof SToken) {
      SToken token = (SToken) item;
      if (token.getGraph() != null) {
        String coveredText = "";
        try {
          coveredText = token.getGraph().getText(token);
        } catch (StringIndexOutOfBoundsException ex) {
          // Ignore: this can happen if the textual relation is already added,
          // but the actual text is not updated yet
        }
        Label l = new Label(coveredText);
        l.setFont(boldFont);
        add(l);
      }
    }

    TreeMap<String, String> labelsByQName = new TreeMap<>();
    for (SAnnotation l : item.getAnnotations()) {
      String qname = SaltUtil.createQName(l.getNamespace(), l.getName());
      labelsByQName.put(qname, qname + "=" + l.getValue());
    }


    for (String annoDesc : labelsByQName.values()) {
      add(new Label(annoDesc));
    }
  }

}
