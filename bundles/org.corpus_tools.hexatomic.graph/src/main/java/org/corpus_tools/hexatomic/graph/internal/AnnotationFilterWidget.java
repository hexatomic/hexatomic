package org.corpus_tools.hexatomic.graph.internal;

import static org.eclipse.jface.layout.GridDataFactory.fillDefaults;
import static org.eclipse.jface.widgets.WidgetFactory.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
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

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(AnnotationFilterWidget.class);

  private final Text txtSegmentFilter;

  private final SDocumentGraph saltGraph;
  private final SimpleContentProposalProvider proposalProvider;

  /**
   * Create a new filter widget.
   * 
   * @param parent The SWT parent composite
   * @param saltGraph The document graph that will be filtered. Needed to extract the annotation
   *        names.
   */
  public AnnotationFilterWidget(Composite parent, SDocumentGraph saltGraph) {
    super(parent, SWT.NONE);
    this.saltGraph = saltGraph;
    this.setLayout(new GridLayout(1, false));

    txtSegmentFilter =
        text(SWT.BORDER).layoutData(fillDefaults().grab(true, true).create()).create(this);

    proposalProvider =
        new SimpleContentProposalProvider(getAllAnnotationNames().toArray(new String[0]));
    char[] autoActivationCharacters = new char[] { '.', ' ' };
    try {
      new ContentProposalAdapter(txtSegmentFilter,
          new TextContentAdapter(),
          proposalProvider, KeyStroke.getInstance("Ctrl+Space"),
          autoActivationCharacters);
    } catch(ParseException ex) {
      log.error("Invalid auto completion keystroke defined in anotation filter widget", ex);
    }
  }

  private List<String> getAllAnnotationNames() {
    Set<String> qnames =
        saltGraph.getNodes().parallelStream().flatMap(n -> n.getAnnotations().stream())
            .map(a -> a.getQName()).collect(Collectors.toSet());
    return new ArrayList<String>(qnames);
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
