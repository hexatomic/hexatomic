package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class ViewLocationReachedCondition extends DefaultCondition {

  private final Viewport viewport;
  private final Point expected;
  private Point lastChecked;

  public ViewLocationReachedCondition(Viewport viewport, Point expected) {
    super();
    this.viewport = viewport;
    this.expected = expected;
  }

  @Override
  public boolean test() throws Exception {
    lastChecked = viewport.getViewLocation();
    return viewport.getViewLocation().equals(expected.x, expected.y);
  }

  @Override
  public String getFailureMessage() {
    return "Viewport location " + expected + " not reached, was " + lastChecked + " instead";
  }
}
