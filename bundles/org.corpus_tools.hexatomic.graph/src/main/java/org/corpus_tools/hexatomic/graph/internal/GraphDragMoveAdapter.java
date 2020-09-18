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
   * 
   * @return The GraphDragMoveAdapter instance.
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
    // Do nothing when the mouse is entered
  }

  @Override
  public void mouseExited(MouseEvent me) {
    // Do nothing when the mouse is exited
  }

  @Override
  public void mouseHover(MouseEvent me) {
    // Do nothing when the mouse is hovering over the viewport
  }

  @Override
  public void mouseMoved(MouseEvent me) {
    // Do nothing when the mouse is moved over the viewport
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
    // Do nothing when there is a mouse double click
  }

}
