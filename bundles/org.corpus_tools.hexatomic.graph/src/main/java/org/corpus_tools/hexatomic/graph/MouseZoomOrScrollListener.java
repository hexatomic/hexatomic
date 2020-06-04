/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.graph;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

/**
 * A helper class that listens to mouse wheel events and calls the corresponding actions on the
 * {@link GraphEditor}.
 * 
 * <p>
 * This is intentionally a seperate class because we can't test mouse wheel events with SWT Bot and
 * it is easier to exclude files from test code coverage than nested classes.
 * </p>
 * 
 * @author Thomas Krause {@literal krauseto@hu-berlin.de}
 */
final class MouseZoomOrScrollListener implements MouseWheelListener {

  private final GraphEditor graphEditor;

  MouseZoomOrScrollListener(GraphEditor graphEditor) {
    this.graphEditor = graphEditor;
  }

  private void doScroll(MouseEvent e) {
    if (e.stateMask == SWT.SHIFT) {
      // Mouse wheel scrolled down
      if (e.count < 0) {
        // Scroll down
        this.graphEditor.scrollGraphView(0, +GraphEditor.DEFAULT_DIFF);
      } else {
        // Scroll up
        this.graphEditor.scrollGraphView(0, -GraphEditor.DEFAULT_DIFF);
      }
    } else if (e.stateMask == SWT.CTRL) {
      if (e.count < 0) {
        // Scroll left
        this.graphEditor.scrollGraphView(+GraphEditor.DEFAULT_DIFF, 0);
      } else {
        // Scroll right
        this.graphEditor.scrollGraphView(-GraphEditor.DEFAULT_DIFF, 0);
      }
    }
  }

  private void doZoom(MouseEvent e) {
    Point originallyClicked = new Point(e.x, e.y);
    if (e.count < 0) {
      this.graphEditor.zoomGraphView(0.75, originallyClicked);
    } else {
      this.graphEditor.zoomGraphView(1.25, originallyClicked);
    }
  }

  @Override
  public void mouseScrolled(MouseEvent e) {

    // If Shift or Ctrl are pressed while scrolling the mouse wheel, scroll rather than zoom
    if (e.stateMask == SWT.SHIFT || e.stateMask == SWT.CTRL) {
      doScroll(e);
    } else {
      doZoom(e);
    }
  }
}
