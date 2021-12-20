/*-
 * #%L
 * org.corpus_tools.hexatomic.core
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

package org.corpus_tools.hexatomic.core.undo;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.common.SDocumentGraph;

public class ChangeSet {

  private final List<ReversibleOperation> changes = new LinkedList<>();

  public ChangeSet(Collection<ReversibleOperation> changes) {
    this.changes.addAll(changes);
  }

  /**
   * Return a set of all containers directly affected by this change.
   * 
   * @return The container objects.
   */
  public Set<Object> getChangedContainers() {
    Set<Object> result = new LinkedHashSet<>();

    for (ReversibleOperation op : this.changes) {
      Object container = op.getChangedContainer();
      if (container != null) {
        result.add(container);
      }
    }

    return result;
  }

  /**
   * Return a set of all elements directly affected by this change.
   * 
   * @return The elements.
   */
  public Set<Object> getChangedElements() {
    Set<Object> result = new LinkedHashSet<>();

    for (ReversibleOperation op : this.changes) {
      Object container = op.getChangedElement();
      if (container != null) {
        result.add(container);
      }
    }

    return result;
  }

  /**
   * Check if the given document is affected by this change set.
   * 
   * @param documentID The ID of the document to check.
   * @return True if is contained in the document.
   */
  public boolean containsDocument(String documentID) {
    if (documentID != null) {
      for (Object element : getChangedContainers()) {
        Optional<SDocumentGraph> graph =
            SaltHelper.getGraphForObject(element, SDocumentGraph.class);
        if (graph.isPresent() && graph.get().getDocument() != null
            && documentID.equals(graph.get().getDocument().getId())) {
          return true;
        }
      }
    }
    return false;
  }

  public List<ReversibleOperation> getChanges() {
    return Collections.unmodifiableList(changes);
  }

}
