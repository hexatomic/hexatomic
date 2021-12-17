package org.corpus_tools.hexatomic.graph.internal;

import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.TitleBarBorder;

public class NodeBorder extends CompoundBorder {


  public NodeBorder(String caption) {
    TitleBarBorder title = new TitleBarBorder(caption);
    title.setBackgroundColor(ColorConstants.white);
    title.setTextColor(ColorPalette.GRAY);

    inner = title;
    outer = new LineBorder();
  }


}
