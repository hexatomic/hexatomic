package org.corpus_tools.hexatomic.graph.internal;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Allow to move a graph by dragging with the mouse.
 * 
 * @author Thomas Krause
 *
 */
public class GraphDragMoveAdapter implements MouseMotionListener, MouseListener {

  private final Viewport viewport;

  private Point start = null;


  /**
   * Creates a new instance of this class and registers the required listeners.
   * 
   * @param graph The graph on which to enable dragging.
   */
  public static GraphDragMoveAdapter register(Graph graph) {
    GraphDragMoveAdapter adapter = new GraphDragMoveAdapter(graph.getViewport());


    graph.getLightweightSystem().getRootFigure().addMouseListener(adapter);
    graph.getLightweightSystem().getRootFigure().addMouseMotionListener(adapter);

    return adapter;
  }

  private GraphDragMoveAdapter(Viewport viewport) {
    this.viewport = viewport;


  }

  @Override
  public void mouseDragged(MouseEvent me) {
    if (this.start == null) {
      return;
    }

    Point loc = this.viewport.getViewLocation();

    Dimension diffDim = me.getLocation().getDifference(start);
    Point diff = new Point(diffDim.width(), diffDim.height()).getNegated();
    this.viewport.translateToParent(diff);
    loc.translate(diff);

    this.viewport.setViewLocation(loc);
    me.consume();

  }

  @Override
  public void mouseEntered(MouseEvent me) {

  }

  @Override
  public void mouseExited(MouseEvent me) {
 
  }

  @Override
  public void mouseHover(MouseEvent me) {

  }

  @Override
  public void mouseMoved(MouseEvent me) {

  }

  @Override
  public void mousePressed(MouseEvent me) {
    IFigure selectedFigure = this.viewport.findFigureAt(me.getLocation());
    if (selectedFigure == this.viewport) {
      this.start = me.getLocation();
      this.viewport.translateFromParent(this.start);
    }
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    this.start = null;
  }

  @Override
  public void mouseDoubleClicked(MouseEvent me) {

  }

}
