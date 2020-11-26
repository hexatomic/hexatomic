/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
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

package org.corpus_tools.hexatomic.grid.internal.ui;

import java.util.Set;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * A dialog informing the user that annotations have not been renamed, although they have triggered
 * an action to rename the annotations. The default case where this dialog would be used is when an
 * annotation with the new qualified name already exists on one or more node.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class UnrenamedAnnotationsDialog {

  private static final String SOME_ANNOTATIONS_WERE_NOT_RENAMED =
      "Some annotations were not renamed!";

  private UnrenamedAnnotationsDialog() {
    // Constructor should not be called
  }

  /**
   * Opens the dialog with a message listing the unchanged nodes by the text they are representing
   * (in case they are tokens) or covering (in case they are spans).
   * 
   * @param namespace The namespace that the user wanted to set the annotation to.
   * @param name The name the user wanted to set the annotation to.
   * @param unchangedNodes The set of nodes that have remained unchanged, as an annotation by the
   *        qualified target annotation name already exists on them.
   */
  public static void open(String namespace, String name, Set<SStructuredNode> unchangedNodes) {
    MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
        SOME_ANNOTATIONS_WERE_NOT_RENAMED,
        constructMessageForNodes(unchangedNodes, namespace, name));
  }

  private static String constructMessageForNodes(Set<SStructuredNode> unchangedNodes,
      String namespace, String name) {
    StringBuilder sb = new StringBuilder(
        "Could not rename some annotations, as annotations with the qualified target name '"
            + (namespace != null ? "" : namespace + ":") + name
            + "' already exist on the respective nodes:\n");
    for (SStructuredNode node : unchangedNodes) {
      if (node instanceof SToken) {
        SToken token = (SToken) node;
        sb.append("- Token with text '" + token.getGraph().getText(token) + "'\n");
      } else if (node instanceof SSpan) {
        SSpan span = (SSpan) node;
        SDocumentGraph graph = span.getGraph();
        sb.append("- Span covering the tokens ");
        for (SToken token : graph.getOverlappedTokens(span)) {
          sb.append(" '" + graph.getText(token) + "' ");
        }
        sb.append("\n");
      }
    }
    return sb.toString().trim();
  }

}
