package org.corpus_tools.hexatomic.it.tests;

import org.corpus_tools.hexatomic.core.UiStatusReport;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

/**
 * A condition that wait until there are no active background jobs.
 * 
 * @author Thomas Krause
 *
 */
class NoBackgroundJobsCondition extends DefaultCondition {

  private final UiStatusReport status;

  public NoBackgroundJobsCondition(UiStatusReport status) {
    super();
    this.status = status;
  }

  @Override
  public boolean test() throws Exception {
    return status.getNumberOfExecutedJobs() == 0;
  }

  @Override
  public String getFailureMessage() {
    return "Background jobs did not finish";
  }
}