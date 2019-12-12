/*-
 * #%L
 * org.corpus_tools.hexatomic.corpusstructureeditor
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.corpusedit.dnd;

import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

public class SaltObjectTreeDragSource extends DragSourceAdapter {

  private final TreeViewer treeViewer;

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(SaltObjectTreeDragSource.class);


  public SaltObjectTreeDragSource(TreeViewer treeViewer) {
    this.treeViewer = treeViewer;
  }

  @Override
  public void dragStart(DragSourceEvent event) {
    event.doit = false;
    if (this.treeViewer.getSelection() instanceof StructuredSelection) {
      StructuredSelection selection = (StructuredSelection) this.treeViewer.getSelection();
      if (selection.getFirstElement() instanceof SDocument) {
        log.debug("Drag started for {}",
            ((IdentifiableElement) selection.getFirstElement()).getId());
        event.doit = true;
      }
    }

  }

  @Override
  public void dragSetData(DragSourceEvent event) {
    if (this.treeViewer.getSelection() instanceof StructuredSelection) {
      StructuredSelection selection = (StructuredSelection) this.treeViewer.getSelection();
      if (selection.getFirstElement() instanceof IdentifiableElement) {
        IdentifiableElement elem = (IdentifiableElement) selection.getFirstElement();
        log.debug("Drag data set for {}", elem.getId());
        event.data = elem.getId();
      }
    }
  }

  @Override
  public void dragFinished(DragSourceEvent event) {
    log.debug("Drag source finished event");

  }
}
