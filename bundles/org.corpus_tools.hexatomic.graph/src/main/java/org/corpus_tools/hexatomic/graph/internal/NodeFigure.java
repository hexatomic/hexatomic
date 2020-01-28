package org.corpus_tools.hexatomic.graph.internal;

import java.util.TreeMap;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.widgets.Display;

public class NodeFigure extends Figure {


  private final String caption;

  /**
   * Creates a new node figure for a given salt node.
   * 
   * @param item The salt node.
   */
  public NodeFigure(SNode item) {

    setFont(Display.getDefault().getSystemFont());
    caption = item.getName();


    FlowLayout layout = new FlowLayout(false);
    setLayoutManager(layout);
    GroupBoxBorder border = new GroupBoxBorder(caption);
    border.setTextColor(ColorConstants.gray);
    setBorder(border);
    setOpaque(true);

    if (item instanceof SToken) {
      setBackgroundColor(ColorConstants.lightGreen);
    } else {
      setBackgroundColor(ColorConstants.white);

    }

    if (item instanceof SToken) {
      SToken token = (SToken) item;
      if (token.getGraph() != null) {
        String coveredText = token.getGraph().getText(token);
        Label l = new Label(coveredText);
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
