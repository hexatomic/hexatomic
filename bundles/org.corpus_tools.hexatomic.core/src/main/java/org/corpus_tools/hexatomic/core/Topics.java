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

package org.corpus_tools.hexatomic.core;

/**
 * Contains constants for topics use by the core bundle.
 * 
 * @author Thomas Krause
 */
public final class Topics {
  
  private Topics() {
    // Private (but empty) constructor to avoid instantiation of this utility class.
  }

  public static final String PROJECT_LOADED = "PROJECT_LOADED";


  /**
   * Send after a collection of Salt elements is modified and a consistent state is reached.
   */
  public static final String ANNOTATION_CHECKPOINT_CREATED = "ANNOTATION/CHECKPOINT/CREATED";

  /**
   * Send after a consistent state has been restored.
   */
  public static final String ANNOTATION_CHECKPOINT_RESTORED =
      "ANNOTATION/CHECKPOINT/RESTORED";

  /**
   * An alias for all events where a checkpoint is created or restored and thus the annotations
   * changed.
   */
  public static final String ANNOTATION_CHANGED = "ANNOTATION/CHECKPOINT/*";

  /**
   * Send as event when the annotation graph is modified but this single change can be reversed with
   * an undo operation.
   */
  public static final String ANNOTATION_OPERATION_ADDED =
      "ANNOTATION/OPERATION/ADDED";


  public static final String DOCUMENT_CLOSED = "DOCUMENT_CLOSED";
  
  public static final String DOCUMENT_LOADED = "DOCUMENT_LOADED";
  
  /**
   * Send when the {@link UiStatusReport} has an update that should be shown.
   */
  public static final String STATUS_UPDATE = "STATUS_UPDATE";
  
  /**
   * Use this to send subliminal but permanent status messages that should be shown in the status
   * toolbar at the bottom. Only the last message will be displayed and newer messages will
   * overwrite it. The argument is the message as string.
   */
  public static final String TOOLBAR_STATUS_MESSAGE = "TOOLBAR_STATUS_MESSAGE";
  
}
