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

import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;

public interface NotifyingLabelableElement<T extends LabelableElement>
    extends NotifyingElement<T>, LabelableElement {

  /**
   * Applies an action that modifies this element but sends notifications before and after the
   * action.
   * 
   * @param action The action to execute
   */
  default void applyModification(GraphModificationAction action) {
    SaltNotificationFactory.sendEvent(Topics.ANNOTATION_BEFORE_MODIFICATION, this);
    action.apply();
    SaltNotificationFactory.sendEvent(Topics.ANNOTATION_AFTER_MODIFICATION, this);
  }

  /**
   * Applies an action that adds an annotation and sends a notifications after adding it.
   * 
   * @param action The action to execute
   * @param element The element that is added and will the the argument of the notification event
   */
  default void applyAdd(GraphModificationAction action, Object element) {
    action.apply();
    SaltNotificationFactory.sendEvent(Topics.ANNOTATION_ADDED, element);
  }

  /**
   * Applies an action that removes an annotation and sends a notifications before removing it.
   * 
   * @param action The action to execute
   * @param element The element that is removed and will the the argument of the notification event
   */
  default void applyRemove(GraphModificationAction action, Object element) {
    SaltNotificationFactory.sendEvent(Topics.ANNOTATION_REMOVED, element);
    action.apply();
  }


  /**
   * Applies an action that removes a label if the label exists and sends the event for removing it.
   * 
   * @param qname The qualified name of the label to remove.
   */
  default void applyRemoveLabelIfExisting(GraphModificationAction action, String qname) {
    if (qname != null) {
      Label label = getLabel(qname);
      SaltNotificationFactory.sendEvent(Topics.ANNOTATION_REMOVED, label);
      action.apply();
    }
  }


}
