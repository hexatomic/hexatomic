package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class PartMaximizedCondition extends DefaultCondition {

  private final MPart part;

  public PartMaximizedCondition(MPart part) {
    super();
    this.part = part;
  }

  @Override
  public boolean test() throws Exception {
    return part.getParent().getTags().contains(IPresentationEngine.MAXIMIZED);
  }

  @Override
  public String getFailureMessage() {
    return "Could not maximize part";
  }
}