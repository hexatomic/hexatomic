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
public interface Topics {
  
  public static final String PROJECT_LOADED = "PROJECT_LOADED";


  /** Send when there is a any change to the Salt annotations (including project structure). */
  public static final String ANNOTATION_ANY_UPDATE = "ANNOTATION_UPDATE/*";

  /**
   * Send when a Salt element has been added.
   */
  public static final String ANNOTATION_ADDED = "ANNOTATION_UPDATE/ADDED";

  /**
   * Send when a Salt element has been removed.
   */
  public static final String ANNOTATION_REMOVED = "ANNOTATION_UPDATE/REMOVED";

  /**
   * Send before a Salt element is modified (e.g. if a label gets a new value).
   */
  public static final String ANNOTATION_BEFORE_MODIFICATION =
      "ANNOTATION_UPDATE/BEFORE_MODIFICATION";

  /**
   * Send after a Salt element is modified (e.g. if a label gets a new value).
   */
  public static final String ANNOTATION_AFTER_MODIFICATION = "ANNOTATION_UPDATE/AFTER_MODIFICATION";

  /**
   * Send after a collection of Salt elements is modified and a consistent state is reached.
   */
  public static final String ANNOTATION_CHECKPOINT_CREATED = "ANNOTATION_UPDATE/CHECKPOINT/CREATED";

  /**
   * Send after a consistent state has been restored.
   */
  public static final String ANNOTATION_CHECKPOINT_RESTORED =
      "ANNOTATION_UPDATE/CHECKPOINT/RESTORED";

  public static final String DOCUMENT_CLOSED = "DOCUMENT_CLOSED";
  
  public static final String DOCUMENT_LOADED = "DOCUMENT_LOADED";
  
  public static final String STATUS_UPDATE = "STATUS_UPDATE";
  
}
