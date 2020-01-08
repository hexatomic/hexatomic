
package org.corpus_tools.hexatomic.edit.grid;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.swt.widgets.Composite;

public class GridEditor {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GridEditor.class);

  @Inject
  public GridEditor() {

  }

  @PostConstruct
  public void postConstruct(Composite parent) {
    log.debug("Starting Grid Editor.");
  }

}
