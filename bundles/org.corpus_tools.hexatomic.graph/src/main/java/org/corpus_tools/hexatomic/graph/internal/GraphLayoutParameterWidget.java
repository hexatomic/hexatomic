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

import org.corpus_tools.hexatomic.graph.GraphDisplayConfiguration;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.wb.swt.ResourceManager;


/**
 * A composite that allows to define filter critera.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class GraphLayoutParameterWidget extends Composite {


  private static final String HORIZONTAL_MARGIN_TOOLTIP = "Horizontal margin between token.\n"
      + "This is measured in \"times of the average token width\".  "
      + "So for \"0\" there is no margin, for \"1\" the margin has "
      + "the same width as the average token node, and for \"2\"  "
      + "the margin is twice as high as the average token node width.";
  private static final String VERTICAL_MARGIN_TOOLTIP = "Vertical margin between nodes.\n"
      + "This is measure in \"times of the node height\".  "
      + "So for \"0\" there is no margin, for \"1\" the margin has the same height as the node, "
      + "and for \"2\"  the margain is twice as high as the node height.";
  private static final String VERTICAL_TOKEN_MARGIN_TOOLTIP =
      "Vertical margin between token and non-token.\n"
          + "Token are grouped in the lowest rank (at the bottom). "
          + "To allow space for pointing relations, you can add a margin "
          + "between the token row and the lowest annotation nodes. "
          + "A margin of \"1\" means there is one level left empty, an offset of \"0\" "
          + "means there is no additional space except for the regular vertical margin.";

  private static final String ORG_CORPUS_TOOLS_HEXATOMIC_CORE = "org.corpus_tools.hexatomic.core";


  private final class ConfigChangedListener extends SelectionAdapter {
    private final IEventBroker eventBroker;

    private ConfigChangedListener(IEventBroker eventBroker) {
      this.eventBroker = eventBroker;
    }

    private void updateConfigFromInterface() {
      config.setVerticalNodeMargin((scaleVerticalMargin.getSelection()) / 10.0);
      config.setHorizontalTokenMargin((scaleHorizontalTokenMargin.getSelection()) / 10.0);
      config.setTokenRankOffset(scaleVerticalTokenMargin.getSelection());
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      updateConfigFromInterface();
      // Update all the labels
      lblVerticalMargin.setText("" + config.getVerticalNodeMargin());
      lblHorizontalTokenMargin.setText("" + config.getHorizontalTokenMargin());
      lblVerticalTokenMargin.setText("" + config.getTokenRankOffset());

      // Send the event that the config has changed
      if (eventBroker != null) {
        eventBroker.post(PARAM_CHANGED_TOPIC, config);
      }
    }
  }

  public static final String PARAM_CHANGED_TOPIC = "GRAPH_EDITOR/GRAPH_LAYOUT_PARAMETER/CHANGED";

  private GraphDisplayConfiguration config;

  private Scale scaleHorizontalTokenMargin;
  private Label lblHorizontalTokenMargin;
  private Label lblVerticalMargin;
  private Scale scaleVerticalMargin;
  private Scale scaleVerticalTokenMargin;
  private Label lblVerticalTokenMargin;

  /**
   * Create a graph layout manipulation widget.
   * 
   * @param parentComposite The SWT parent composite
   * @param eventBroker Is used to post events whenever there is an update on the parameters.
   */
  public GraphLayoutParameterWidget(Composite parentComposite, final IEventBroker eventBroker) {
    super(parentComposite, SWT.NONE);
    setLayout(new GridLayout(3, false));

    config = new GraphDisplayConfiguration();

    Label captionHorizontalTokenMargin = new Label(this, SWT.WRAP);
    captionHorizontalTokenMargin.setImage(ResourceManager
        .getPluginImage(ORG_CORPUS_TOOLS_HEXATOMIC_CORE, "icons/fontawesome/arrows-alt-h.png"));

    scaleHorizontalTokenMargin = new Scale(this, SWT.NONE);
    scaleHorizontalTokenMargin.setToolTipText(HORIZONTAL_MARGIN_TOOLTIP);
    scaleHorizontalTokenMargin.setPageIncrement(1);
    scaleHorizontalTokenMargin.setMaximum(20);
    scaleHorizontalTokenMargin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    scaleHorizontalTokenMargin
        .setSelection((int) Math.round(config.getHorizontalTokenMargin() * 10.0));

    lblHorizontalTokenMargin = new Label(this, SWT.NONE);
    lblHorizontalTokenMargin.setToolTipText(HORIZONTAL_MARGIN_TOOLTIP);
    lblHorizontalTokenMargin.setText("" + config.getHorizontalTokenMargin());


    Label captionVerticalMargin = new Label(this, SWT.WRAP);
    captionVerticalMargin.setImage(ResourceManager.getPluginImage(ORG_CORPUS_TOOLS_HEXATOMIC_CORE,
        "icons/fontawesome/arrows-alt-v.png"));

    scaleVerticalMargin = new Scale(this, SWT.NONE);
    scaleVerticalMargin.setToolTipText(VERTICAL_MARGIN_TOOLTIP);
    scaleVerticalMargin.setPageIncrement(1);
    scaleVerticalMargin.setMaximum(20);
    scaleVerticalMargin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    scaleVerticalMargin.setSelection((int) Math.round(config.getVerticalNodeMargin() * 10.0));

    lblVerticalMargin = new Label(this, SWT.NONE);
    lblVerticalMargin.setToolTipText(VERTICAL_MARGIN_TOOLTIP);
    lblVerticalMargin.setText("" + config.getVerticalNodeMargin());

    Label captionVerticalTokenMargin = new Label(this, SWT.WRAP);
    captionVerticalTokenMargin.setImage(ResourceManager
        .getPluginImage(ORG_CORPUS_TOOLS_HEXATOMIC_CORE, "icons/fontawesome/text-height.png"));

    scaleVerticalTokenMargin = new Scale(this, SWT.NONE);
    scaleVerticalTokenMargin.setToolTipText(VERTICAL_TOKEN_MARGIN_TOOLTIP);
    scaleVerticalTokenMargin.setPageIncrement(1);
    scaleVerticalTokenMargin.setMaximum(5);
    scaleVerticalTokenMargin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    scaleVerticalTokenMargin.setSelection(config.getTokenRankOffset());

    lblVerticalTokenMargin = new Label(this, SWT.NONE);
    lblVerticalTokenMargin.setToolTipText(VERTICAL_TOKEN_MARGIN_TOOLTIP);
    lblVerticalTokenMargin.setText("" + config.getTokenRankOffset());

    ConfigChangedListener selectionListener = new ConfigChangedListener(eventBroker);
    scaleVerticalMargin.addSelectionListener(selectionListener);
    scaleHorizontalTokenMargin.addSelectionListener(selectionListener);
    scaleVerticalTokenMargin.addSelectionListener(selectionListener);
  }


}
