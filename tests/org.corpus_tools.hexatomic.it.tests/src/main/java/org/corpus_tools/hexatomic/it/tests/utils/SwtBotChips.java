package org.corpus_tools.hexatomic.it.tests.utils;

import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;

public class SwtBotChips extends AbstractSWTBotControl<Chips> {

  public SwtBotChips(Chips w) throws WidgetNotFoundException {
    super(w);
  }

  @Override
  public AbstractSWTBot<Chips> click() {
    if (log.isDebugEnabled()) {
      log.debug("{}", MessageFormat.format("Clicking on {0}", SWTUtils.getText(widget)));
    }

    waitForEnabled();

    // Get coordinates of the right part of the chip, where the close button is located
    Point clickPoint = syncExec(() -> {
      Rectangle area = widget.getClientArea();
      return new Point(area.x + area.width - 20, Math.round(area.y + (area.height / 2.0f)));
    });

    // Use the local click point as parameter for the mouse events
    Event e = new Event();
    e.x = clickPoint.x;
    e.y = clickPoint.y;
    e.widget = widget;

    // This is the same event order as used for click buttons
    notify(SWT.MouseEnter, e);
    notify(SWT.MouseMove, e);
    notify(SWT.Activate, e);
    notify(SWT.FocusIn, e);
    notify(SWT.MouseDown, e);
    notify(SWT.MouseUp, e);
    notify(SWT.Selection, e);
    notify(SWT.MouseHover, e);
    notify(SWT.MouseMove, e);
    notify(SWT.MouseExit, e);
    notify(SWT.Deactivate, e);
    notify(SWT.FocusOut, e);
    if (log.isDebugEnabled()) {
      log.debug("{}", MessageFormat.format("Clicked on {0}", SWTUtils.getText(widget)));
    }
    return this;
  }

}
