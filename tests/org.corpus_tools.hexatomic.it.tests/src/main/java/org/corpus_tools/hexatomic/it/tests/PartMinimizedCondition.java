package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

public class PartMinimizedCondition extends DefaultCondition implements ICondition {

  private final MPart part;

  public PartMinimizedCondition(MPart part) {
    super();
    this.part = part;
  }

  @Override
  public boolean test() throws Exception {
    return part.getParent().getTags().contains(IPresentationEngine.MINIMIZED);
  }

  @Override
  public String getFailureMessage() {
    return "Could not minimize part!";
  }

}
