/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.graph.internal;

import static org.eclipse.jface.widgets.WidgetFactory.text;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.nebula.widgets.chips.Chips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that allows to define filter critera.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class AnnotationFilterWidget extends Composite
    implements IContentProposalListener, AnnotationFilter {

  public static final String ANNO_FILTER_CHANGED_TOPIC = "GRAPH_EDITOR/ANNOTATION_FILTER/CHANGED";

  private final Text txtAddAnnotatioName;

  private final AnnotationNameProposalProvider proposalProvider;

  private final List<Chips> activeChips = new LinkedList<>();
  private final Set<String> filteredNames = new TreeSet<>();

  private final Composite facetFilterComposite;


  private final IEventBroker eventBroker;

  private ScrolledComposite scroll;

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
    this.setLayout(GridLayoutFactory.swtDefaults().create());

    txtAddAnnotatioName = text(SWT.BORDER)
        .layoutData(
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, false).create())
        .create(this);
    txtAddAnnotatioName.setMessage("Search");


    scroll = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL);
    scroll.setExpandHorizontal(true);
    scroll.setExpandVertical(true);
    scroll.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
    scroll.setAlwaysShowScrollBars(true);


    facetFilterComposite = new Composite(scroll, SWT.NONE);
    facetFilterComposite
        .setLayout(RowLayoutFactory.swtDefaults().type(SWT.HORIZONTAL).wrap(true).create());
    scroll.setContent(facetFilterComposite);

    adjustScrollSize();
    scroll.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        adjustScrollSize();
      }
    });

    proposalProvider = new AnnotationNameProposalProvider(saltGraph);
    ContentProposalAdapter adapter = new ContentProposalAdapter(txtAddAnnotatioName,
        new TextContentAdapter(), proposalProvider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.addContentProposalListener(this);
  }


  @Override
  public Optional<Set<String>> getFilter() {
    if (filteredNames.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(filteredNames);
    }
  }

  private void adjustScrollSize() {
    // If there is any chip with a larger width, use it to ensure the close button is always
    // visible. Fallback to the scroll area size otherwise.
    int maxChipWidth = activeChips.stream().map(c -> c.computeSize(SWT.DEFAULT, SWT.DEFAULT).x)
        .max(Integer::compare).orElse(1);
    int width = Math.max(maxChipWidth, scroll.getClientArea().width);

    Point computedSize = facetFilterComposite.computeSize(width, SWT.DEFAULT);
    scroll.setMinSize(computedSize);

  }

  @Override
  public void proposalAccepted(IContentProposal proposal) {

    final String newAnnoString = proposal.getContent();

    txtAddAnnotatioName.setText("");

    Optional<Chips> existing =
        activeChips.stream().filter(c -> Objects.equals(newAnnoString, c.getText())).findAny();
    // Only add an annotation once and highlight the existing chip otherwise
    if (existing.isPresent()) {
      existing.get().setChipsBackground(ColorPalette.REDDISH_PURPLE);
      existing.get().redraw();
      Display.getCurrent().timerExec(1000, () -> {
        existing.get().setChipsBackground(ColorPalette.GRAY);
        existing.get().redraw();
      });

      return;
    }



    Chips chip = new Chips(facetFilterComposite, SWT.CLOSE);
    chip.setLayoutData(RowDataFactory.swtDefaults().create());
    chip.setText(newAnnoString);
    chip.setChipsBackground(ColorPalette.GRAY);
    chip.setForeground(ColorConstants.white);
    chip.addCloseListener(event -> {
      activeChips.remove(chip);
      chip.setVisible(false);
      chip.dispose();
      filteredNames.remove(newAnnoString);

      adjustScrollSize();

      facetFilterComposite.layout();
      eventBroker.post(ANNO_FILTER_CHANGED_TOPIC, AnnotationFilterWidget.this);
    });
    activeChips.add(chip);
    filteredNames.add(newAnnoString);
    facetFilterComposite.layout();

    adjustScrollSize();

    eventBroker.post(ANNO_FILTER_CHANGED_TOPIC, AnnotationFilterWidget.this);
  }

}
