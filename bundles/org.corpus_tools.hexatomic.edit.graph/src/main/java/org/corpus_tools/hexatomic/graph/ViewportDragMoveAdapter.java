package org.corpus_tools.hexatomic.graph;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * Allow to move a viewport by dragging with the mouse.
 * 
 * @author Thomas Krause
 *
 */
public class ViewportDragMoveAdapter implements MouseMotionListener, MouseListener {

  private final Viewport viewport;

  private Point start = null;


  /**
   * Creates a new instance of this class and registers the required listeners.
   * 
   * @param viewport The viewport on which to enable dragging.
   */
  public static ViewportDragMoveAdapter register(Viewport viewport) {
    ViewportDragMoveAdapter adapter = new ViewportDragMoveAdapter(viewport);


    viewport.addMouseListener(adapter);
    viewport.addMouseMotionListener(adapter);

    return adapter;
  }

  private ViewportDragMoveAdapter(Viewport viewport) {
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
    this.start = me.getLocation();
    this.viewport.translateFromParent(this.start);
  }

  @Override
  public void mouseExited(MouseEvent me) {
    this.start = null;
  }

  @Override
  public void mouseHover(MouseEvent me) {

  }

  @Override
  public void mouseMoved(MouseEvent me) {

  }

  @Override
  public void mousePressed(MouseEvent me) {
    this.start = me.getLocation();
    this.viewport.translateFromParent(this.start);
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    this.start = null;
  }

  @Override
  public void mouseDoubleClicked(MouseEvent me) {

  }

}
