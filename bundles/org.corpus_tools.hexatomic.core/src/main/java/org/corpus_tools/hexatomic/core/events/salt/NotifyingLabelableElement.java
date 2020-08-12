/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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

package org.corpus_tools.hexatomic.core.events.salt;

import java.util.Collection;
import java.util.LinkedList;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.undo.operations.LabelAddOperation;
import org.corpus_tools.hexatomic.core.undo.operations.LabelRemoveOperation;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;

public interface NotifyingLabelableElement<T extends LabelableElement>
    extends NotifyingElement<T>, LabelableElement {


  /**
   * Applies an action that adds an label to a container and sends a notifications after adding it.
   * 
   * @param action The action to execute
   * @param container The container the label is added to
   * @param label The label that is added to the container
   */
  default void applyAddLabel(GraphModificationAction action, LabelableElement container,
      Label label) {
    action.apply();
    SaltNotificationFactory.sendEvent(Topics.UNDO_OPERATION_ADDED,
        new LabelAddOperation(this, label.getQName()));
  }


  /**
   * Applies an action that removes a label if the label exists and sends the event for removing it.
   * 
   * @param qname The qualified name of the label to remove.
   */
  default void applyRemoveLabelIfExisting(GraphModificationAction action, String qname) {
    if (qname != null) {
      Label label = getLabel(qname);
      if (label != null) {
        SaltNotificationFactory.sendEvent(Topics.UNDO_OPERATION_ADDED,
            new LabelRemoveOperation(label, label.getContainer()));
        action.apply();
      }
    }
  }

  /**
   * Applies an action that removes all label from the container and sends the undo operation
   * events.
   * 
   * @param container The container the labels are removed from.
   */
  default void applyRemoveAllLabels(GraphModificationAction action, LabelableElement container) {
    Collection<Label> labels = container.getLabels();
    if (labels != null) {
      labels = new LinkedList<>(labels);
    } else {
      labels = new LinkedList<>();
    }
    action.apply();
    for (Label l : labels) {
      SaltNotificationFactory.sendEvent(Topics.UNDO_OPERATION_ADDED,
          new LabelRemoveOperation(l, container));
    }
  }


}
