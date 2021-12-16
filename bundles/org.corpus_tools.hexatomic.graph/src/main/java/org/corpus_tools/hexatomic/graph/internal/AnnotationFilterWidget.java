package org.corpus_tools.hexatomic.graph.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that allows to define filter critera.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class AnnotationFilterWidget extends Composite {

  private final Text txtSegmentFilter;


  /**
   * Create a new filter widget.
   * 
   * @param parent The SWT parent composite
   */
  public AnnotationFilterWidget(Composite parent) {
    super(parent, SWT.NONE);
    this.setLayout(new FillLayout(SWT.VERTICAL));

    txtSegmentFilter = new Text(this, SWT.BORDER);
    txtSegmentFilter.setMessage("Filter by node annotation name");
  }

  /**
   * Add listener for modifications of the filtered annotation names.
   * 
   * @param listener The listener which is called when the value is modified.
   */
  public void addModifyListener(ModifyListener listener) {
    txtSegmentFilter.addModifyListener(listener);
  }

  /**
   * Get the current annotation name to filter for.
   * 
   * @return The filtered annotation name (or part of it).
   */
  public String getFilterText() {
    return txtSegmentFilter.getText();
  }
}
