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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
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

  private final Composite facetFilterComposite;

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
    this.setLayout(GridLayoutFactory.swtDefaults().create());


    facetFilterComposite = new Composite(this, SWT.NONE);
    facetFilterComposite
        .setLayout(RowLayoutFactory.swtDefaults().type(SWT.HORIZONTAL).wrap(true).create());
    facetFilterComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

    txtAddAnnotatioName =
        text(SWT.BORDER)
            .layoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BOTTOM).grab(true, false)
                .create())
            .create(this);
    txtAddAnnotatioName.setMessage("Search");

    proposalProvider = new AnnotationNameProposalProvider(saltGraph);
    ContentProposalAdapter adapter = new ContentProposalAdapter(txtAddAnnotatioName,
        new TextContentAdapter(), proposalProvider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.addContentProposalListener(new IContentProposalListener() {

      @Override
      public void proposalAccepted(IContentProposal proposal) {

        Chips chip = new Chips(facetFilterComposite, SWT.CLOSE);
        chip.setLayoutData(RowDataFactory.swtDefaults().create());
        chip.setText(proposal.getContent());
        chip.addCloseListener(event -> {
          activeChips.remove(chip);
          chip.setVisible(false);
          chip.dispose();

          facetFilterComposite.layout();
          eventBroker.post(ANNO_FILTER_CHANGED_TOPIC, AnnotationFilterWidget.this);

        });
        activeChips.add(chip);
        facetFilterComposite.layout();
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
