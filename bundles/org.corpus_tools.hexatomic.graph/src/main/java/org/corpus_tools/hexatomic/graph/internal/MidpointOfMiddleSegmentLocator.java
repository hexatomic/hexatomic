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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;

public class MidpointOfMiddleSegmentLocator extends ConnectionLocator {

  private final int fontHeight;

  public MidpointOfMiddleSegmentLocator(Connection c, int fontHeight) {
    super(c);
    this.fontHeight = fontHeight;
  }

  /**
   * Returns the point of reference associated with this locator. This will first determine the
   * middle segment of a list of connection points and then return the midway between these two
   * points.
   * 
   * @return the reference point
   */

  @Override
  protected Point getReferencePoint() {
    Connection conn = getConnection();
    Point p = Point.SINGLETON;


    if (conn.getPoints().size() >= 2) {

      // Since there are at least two points, nrPoints / 2 is at least "1"
      // We need to select the point before (-1) and the following one
      int index = ((int) Math.floor(conn.getPoints().size() / 2.0) - 1);

      Point p1 = conn.getPoints().getPoint(index);
      Point p2 = conn.getPoints().getPoint(index + 1);
      conn.translateToAbsolute(p1);
      conn.translateToAbsolute(p2);
      p.x = (p2.x - p1.x) / 2 + p1.x;
      p.y = (p2.y - p1.y) / 2 + p1.y;

      // Use the text height as offset to draw the label above the line (if it is straight)
      p.y -= fontHeight / 2;
      return p;
    } else {
      // Return the only point as reference
      return conn.getPoints().getFirstPoint().getCopy();
    }
  }
}
