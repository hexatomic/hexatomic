/*-
 * #%L
 * org.corpus_tools.hexatomic.core
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

package org.corpus_tools.hexatomic.core.update;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AppStartupCompleteEventHandler implements EventHandler {

  private final UpdateRunner updateRunner;
  private final IEventBroker eventBroker;
  private final Shell shell;

  /**
   * Create instance of AppStartupCompleteEventHandler.
   * 
   * @param updateRunner Service for starting update check jobs.
   * @param eventBroker Event broker service to unsubscribe from subscribed event
   * @param shell The user interface shell.
   */
  public AppStartupCompleteEventHandler(UpdateRunner updateRunner, IEventBroker eventBroker,
      Shell shell) {
    this.updateRunner = updateRunner;
    this.eventBroker = eventBroker;
    this.shell = shell;
  }


  @Override
  public void handleEvent(Event event) {
    eventBroker.unsubscribe(this);
    updateRunner.scheduleUpdateJob(false, shell);
  }
}
