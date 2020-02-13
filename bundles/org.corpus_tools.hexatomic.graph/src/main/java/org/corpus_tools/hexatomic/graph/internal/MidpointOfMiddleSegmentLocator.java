package org.corpus_tools.hexatomic.graph.internal;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;

public class MidpointOfMiddleSegmentLocator extends ConnectionLocator {

  public MidpointOfMiddleSegmentLocator(Connection c) {
    super(c);
  }

  /**
   * Returns the point of reference associated with this locator. This will first determine the
   * middle segment of a list of connection points and then return the midway between these two
   * points.
   * 
   * @return the reference point
   */
  protected Point getReferencePoint() {
    Connection conn = getConnection();
    Point p = Point.SINGLETON;

    if (conn.getPoints().size() >= 2) {

      // Since there are at least two points, nrPoints / 2 is at least "1"
      // We need to select the point before (-1) and the following one
      int index = ((int) Math.floor((double) conn.getPoints().size() / 2.0) - 1);

      Point p1 = conn.getPoints().getPoint(index);
      Point p2 = conn.getPoints().getPoint(index + 1);
      conn.translateToAbsolute(p1);
      conn.translateToAbsolute(p2);
      p.x = (p2.x - p1.x) / 2 + p1.x;
      p.y = (p2.y - p1.y) / 2 + p1.y;
      return p;
    } else {
      // Return the only point as reference
      return conn.getPoints().getFirstPoint().getCopy();
    }
  }
}
