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

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;

/**
 * Helper class to distribute events.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
class NotificationHelper {

  private final IEventBroker events;
  private final ProjectManager projectManager;
  private final UISynchronize sync;

  /**
   * Create a new notification helper.
   * 
   * @param events The event broker to distribute the events.
   * @param projectManager The project manager, used to check if sending events is enabled.
   * @param sync UI synchronization object to make sure the events are send from the UI thread.
   */
  NotificationHelper(IEventBroker events,
      ProjectManager projectManager,
      UISynchronize sync) {
    super();
    this.events = events;
    this.projectManager = projectManager;
    this.sync = sync;
  }

  void sendEvent(String topic, Object element) {
    // We have to assume that other thread can modify the Salt graph.
    // Since we are sending the events synchronously and receivers are likely using the UI thread
    // for receiving the event, synchronizing here and not in the event bus makes sure the event
    // handler is always executed before this function returns.
    sync.syncExec(() -> {
      if (!projectManager.isSuppressingEvents()) {
        Object resolvedElement = SaltHelper.resolveDelegation(element);
        events.send(topic, resolvedElement);
      }
    });
  }
}
