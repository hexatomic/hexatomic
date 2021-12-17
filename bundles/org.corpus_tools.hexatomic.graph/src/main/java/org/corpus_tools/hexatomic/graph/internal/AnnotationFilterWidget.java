package org.corpus_tools.hexatomic.graph.internal;

import static org.eclipse.jface.widgets.WidgetFactory.text;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

  public static final String ANNO_FILTER_CHANGED_TOPIC = "GRAPH_EDITOR/ANNOTATION_FILTER/CHANGED";

  private final Text txtAddAnnotatioName;

  private final AnnotationNameProposalProvider proposalProvider;

  private final List<Chips> activeChips = new LinkedList<>();

  private final Composite chipComposite;

  private ScrolledComposite chipScroll;

  private final IEventBroker eventBroker;

  /**
   * Create a new filter widget.
   * 
   * @param parent The SWT parent composite
   * @param saltGraph The document graph that will be filtered. Needed to extract the annotation
   *        names.
   * @param eventBroker Is used to post events whenever there is an update on the filter model.
   */
  public AnnotationFilterWidget(Composite parent, SDocumentGraph saltGraph,
      IEventBroker eventBroker) {
    super(parent, SWT.BORDER);
    this.eventBroker = eventBroker;
    this.setLayout(new GridLayout(1, false));

    chipScroll = new ScrolledComposite(this, SWT.V_SCROLL);
    chipScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    chipScroll.setLayout(new GridLayout(1, false));


    chipComposite = new Composite(chipScroll, SWT.BORDER);
    chipComposite.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, true, true));
    chipComposite.setLayout(new RowLayout());


    chipScroll.setAlwaysShowScrollBars(true);
    chipScroll.setExpandHorizontal(true);
    chipScroll.setExpandVertical(true);
    chipScroll.setContent(chipComposite);

    txtAddAnnotatioName =
        text(SWT.BORDER).layoutData(new GridData(SWT.FILL, SWT.TOP, true, false))
            .create(this);

    proposalProvider = new AnnotationNameProposalProvider(saltGraph);
    ContentProposalAdapter adapter = new ContentProposalAdapter(txtAddAnnotatioName,
        new TextContentAdapter(), proposalProvider, null,
        null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.addContentProposalListener(new IContentProposalListener() {

      @Override
      public void proposalAccepted(IContentProposal proposal) {
        Chips chip = new Chips(chipComposite, SWT.CLOSE);
        chip.setText(proposal.getContent());
        chip.addCloseListener(event -> {
          activeChips.remove(chip);
          chip.setVisible(false);
          chip.dispose();
          chipComposite.layout();
          eventBroker.post(ANNO_FILTER_CHANGED_TOPIC, AnnotationFilterWidget.this);
        });
        activeChips.add(chip);
        chipComposite.layout();

        // TODO find a better way to calculate the height
        chipScroll.setMinHeight(activeChips.size() * 20);
        chipScroll.layout();
        AnnotationFilterWidget.this.layout();
        
        eventBroker.post(ANNO_FILTER_CHANGED_TOPIC, AnnotationFilterWidget.this);
      }
    });
  }

  /**
   * Get allowed annotation names to include in the view.
   * 
   * @return The the list of names or {@link Optional#empty()} when no filter should be applied.
   */
  public Optional<Set<String>> getFilter() {
    if (activeChips.isEmpty()) {
      return Optional.empty();
    } else {
      Set<String> activeFilter =
          activeChips.stream().map(c -> c.getText()).collect(Collectors.toSet());
      return Optional.of(activeFilter);
    }

  }
}
