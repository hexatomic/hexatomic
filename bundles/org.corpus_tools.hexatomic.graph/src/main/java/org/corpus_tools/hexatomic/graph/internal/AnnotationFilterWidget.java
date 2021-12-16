package org.corpus_tools.hexatomic.graph.internal;

import static org.eclipse.jface.layout.GridDataFactory.fillDefaults;
import static org.eclipse.jface.widgets.WidgetFactory.text;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that allows to define filter critera.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class AnnotationFilterWidget extends Composite {

  private final Text txtSegmentFilter;

  private final AnnotationNameProposalProvider proposalProvider;

  /**
   * Create a new filter widget.
   * 
   * @param parent The SWT parent composite
   * @param saltGraph The document graph that will be filtered. Needed to extract the annotation
   *        names.
   */
  public AnnotationFilterWidget(Composite parent, SDocumentGraph saltGraph) {
    super(parent, SWT.NONE);
    this.setLayout(new GridLayout(1, false));

    txtSegmentFilter =
        text(SWT.BORDER).layoutData(fillDefaults().grab(true, true).create()).create(this);

    proposalProvider = new AnnotationNameProposalProvider(saltGraph);
    ContentProposalAdapter adapter = new ContentProposalAdapter(txtSegmentFilter,
        new TextContentAdapter(), proposalProvider, null,
        null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
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
