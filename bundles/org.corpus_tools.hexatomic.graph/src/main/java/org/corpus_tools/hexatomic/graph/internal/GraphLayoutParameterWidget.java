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

/**
 * A composite that allows to define filter critera.
 * 
 * @author Thomas Krause {@literal thomas.krause@hu-berlin.de}
 */
public class GraphLayoutParameterWidget extends Composite {

  private final class ConfigChangedListener extends SelectionAdapter {
    private final IEventBroker eventBroker;

    private ConfigChangedListener(IEventBroker eventBroker) {
      this.eventBroker = eventBroker;
    }

    private void updateConfigFromInterface() {
      config.setVerticalNodeMargin(((double) scalePercentMargin.getSelection()) / 10.0);
      config.setHorizontalTokenMargin(((double) scaleTokenMargin.getSelection()) / 10.0);
      config.setTokenRankOffset(scaleTokenRank.getSelection());
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      updateConfigFromInterface();
      // Update all the labels
      lblPercentMargin.setText("" + config.getVerticalNodeMargin());
      lblTokenMargin.setText("" + config.getHorizontalTokenMargin());
      lblTokenRank.setText("" + config.getTokenRankOffset());

      // Send the event that the config has changed
      if (eventBroker != null) {
        eventBroker.post(PARAM_CHANGED_TOPIC, config);
      }
    }
  }

  public static final String PARAM_CHANGED_TOPIC = "GRAPH_EDITOR/GRAPH_LAYOUT_PARAMETER/CHANGED";
  private Label lblPercentMargin;
  private Scale scalePercentMargin;

  private GraphDisplayConfiguration config;
  private Label lblTokenMarginCaption;
  private Scale scaleTokenMargin;
  private Label lblTokenMargin;
  private Label lblTokenRankCaption;
  private Scale scaleTokenRank;
  private Label lblTokenRank;

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

    Label lblPercentMarginCaption = new Label(this, SWT.WRAP);
    GridData lblPercentMarginCaptionGridData =
        new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    lblPercentMarginCaptionGridData.widthHint = 80;
    lblPercentMarginCaption.setLayoutData(lblPercentMarginCaptionGridData);
    lblPercentMarginCaption.setToolTipText("Vertical margin between nodes.\n"
        + "This is measure in \"times of the node height\".  "
        + "So for \"0\" there is no margin, for \"1\" the margin has the same height as the node, "
        + "and for \"2\"  the margin is twice as high as the node height.");
    lblPercentMarginCaption.setText("Vertical Margin");

    scalePercentMargin = new Scale(this, SWT.NONE);
    scalePercentMargin.setPageIncrement(1);
    scalePercentMargin.setMaximum(20);
    scalePercentMargin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    scalePercentMargin.setSelection((int) Math.round(config.getVerticalNodeMargin() * 10.0));

    lblPercentMargin = new Label(this, SWT.NONE);
    lblPercentMargin.setText("" + config.getVerticalNodeMargin());

    lblTokenMarginCaption = new Label(this, SWT.WRAP);
    lblTokenMarginCaption.setToolTipText("Horizontal margin between token.\n"
        + "This is measured in \"times of the average token width\".  "
        + "So for \"0\" there is no margin, for \"1\" the margin has "
        + "the same width as the average token node, and for \"2\"  "
        + "the margin is twice as high as the average token node width.");
    GridData lblTokenMarginCaptionGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    lblTokenMarginCaptionGridData.widthHint = 80;
    lblTokenMarginCaption.setLayoutData(lblTokenMarginCaptionGridData);
    lblTokenMarginCaption.setText("Token Margin");

    scaleTokenMargin = new Scale(this, SWT.NONE);
    scaleTokenMargin.setPageIncrement(1);
    scaleTokenMargin.setMaximum(20);
    scaleTokenMargin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    scaleTokenMargin.setSelection((int) Math.round(config.getHorizontalTokenMargin() * 10.0));

    lblTokenMargin = new Label(this, SWT.NONE);
    lblTokenMargin.setToolTipText("");
    lblTokenMargin.setText("" + config.getHorizontalTokenMargin());

    lblTokenRankCaption = new Label(this, SWT.WRAP);
    lblTokenRankCaption.setToolTipText("Offset between token and non-token.\n"
        + "Token are grouped in the lowest rank (at the bottom). "
        + "To allow space for pointing relations, you can add an offset "
        + "between the token row and the lowest annotation nodes. "
        + "An offset of \"1\" means there is one level left empty, an offset of \"0\" "
        + "means there is no additional empty space except for the regular vertical margin.");
    GridData lblTokenRankCaptionGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    lblTokenRankCaptionGridData.widthHint = 80;
    lblTokenRankCaption.setLayoutData(lblTokenRankCaptionGridData);
    lblTokenRankCaption.setText("Token Rank Offset");

    scaleTokenRank = new Scale(this, SWT.NONE);
    scaleTokenRank.setPageIncrement(1);
    scaleTokenRank.setMaximum(5);
    scaleTokenRank.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    scaleTokenRank.setSelection(config.getTokenRankOffset());

    lblTokenRank = new Label(this, SWT.NONE);
    lblTokenRank.setText("" + config.getTokenRankOffset());

    ConfigChangedListener selectionListener = new ConfigChangedListener(eventBroker);
    scalePercentMargin.addSelectionListener(selectionListener);
    scaleTokenMargin.addSelectionListener(selectionListener);
    scaleTokenRank.addSelectionListener(selectionListener);
  }

}
