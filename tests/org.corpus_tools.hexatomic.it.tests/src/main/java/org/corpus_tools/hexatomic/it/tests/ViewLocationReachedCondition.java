package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

class ViewLocationReachedCondition extends DefaultCondition {

  private final Viewport viewport;
  private final Point expected;
  private Point lastChecked;

  private static final int TOLERANCE_IN_PIXELS = 2;

  public ViewLocationReachedCondition(Viewport viewport, Point expected) {
    super();
    this.viewport = viewport;
    this.expected = expected;
  }

  @Override
  public boolean test() throws Exception {
    lastChecked = viewport.getViewLocation();
    int differenceX = Math.abs(expected.x - viewport.getViewLocation().x);
    int differenceY = Math.abs(expected.y - viewport.getViewLocation().y);
    return differenceX <= TOLERANCE_IN_PIXELS && differenceY <= TOLERANCE_IN_PIXELS;
  }

  @Override
  public String getFailureMessage() {
    return "Viewport location " + expected + " not reached, was " + lastChecked + " instead";
  }
}
