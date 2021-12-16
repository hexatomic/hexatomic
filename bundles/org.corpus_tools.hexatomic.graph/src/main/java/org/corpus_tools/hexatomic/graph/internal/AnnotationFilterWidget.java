package org.corpus_tools.hexatomic.graph.internal;

import static org.eclipse.jface.widgets.WidgetFactory.text;

import java.util.LinkedList;
import java.util.List;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
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

  private final List<Chips> activeChips = new LinkedList<>();

  private final Composite chipComposite;

  private ScrolledComposite chipScroll;


  /**
   * Create a new filter widget.
   * 
   * @param parent The SWT parent composite
   * @param saltGraph The document graph that will be filtered. Needed to extract the annotation
   *        names.
   */
  public AnnotationFilterWidget(Composite parent, SDocumentGraph saltGraph) {
    super(parent, SWT.BORDER);
    this.setLayout(new GridLayout(1, false));

    // chipScroll = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);

    chipComposite = new Composite(this, SWT.BORDER);
    chipComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    chipComposite.setLayout(new RowLayout());


    // chipScroll.setContent(chipComposite);
    // chipScroll.setExpandHorizontal(true);
    // chipScroll.setExpandVertical(true);
    // chipScroll.setMinSize(100, 60);

    txtSegmentFilter =
        text(SWT.BORDER).layoutData(new GridData(SWT.FILL, SWT.TOP, true, false))
            .create(this);

    proposalProvider = new AnnotationNameProposalProvider(saltGraph);
    ContentProposalAdapter adapter = new ContentProposalAdapter(txtSegmentFilter,
        new TextContentAdapter(), proposalProvider, null,
        null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.addContentProposalListener(new IContentProposalListener() {

      @Override
      public void proposalAccepted(IContentProposal proposal) {
        Chips chip = new Chips(chipComposite, SWT.CLOSE);
        chip.setText(proposal.getContent());
        chip.addCloseListener(event -> {
          chip.setVisible(false);
          activeChips.remove(chip);
          chip.dispose();
          chipComposite.layout();
        });
        activeChips.add(chip);
        chipComposite.layout();
        AnnotationFilterWidget.this.layout();
      }
    });
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
