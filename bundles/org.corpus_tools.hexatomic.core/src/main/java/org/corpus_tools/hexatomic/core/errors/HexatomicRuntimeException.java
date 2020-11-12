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

package org.corpus_tools.hexatomic.core.errors;

/**
 * A generic runtime exception for errors thrown from the Hexatomic code base (as opposed to be
 * originated from external libraries).
 * 
 * @author Thomas Krause (krauseto@hu-berlin.de)
 *
 */
public class HexatomicRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a runtime exception with the given detailed message.
   * 
   * @param msg The message for this exception.
   */
  public HexatomicRuntimeException(String msg) {
    super(msg);
  }

  /**
   * Constructs a runtime exception with the given detailed message which passes on the cause for
   * this exception.
   * 
   * @param msg The message for this exception.
   * @param cause The cause for this exception.
   */
  public HexatomicRuntimeException(String msg, Throwable cause) {
    super(msg, cause);
  }


}
