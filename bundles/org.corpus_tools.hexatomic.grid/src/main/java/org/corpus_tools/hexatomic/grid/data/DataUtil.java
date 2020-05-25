/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
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

package org.corpus_tools.hexatomic.grid.data;

import org.corpus_tools.salt.util.SaltUtil;

/**
 * Helper class providing utility functions for the underlying data.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class DataUtil {

  /**
   * Checks whether the passed string can be split at Salt's namespace separator and returns the
   * namespace part of the original string, i.e., the string preceding the separator.
   * 
   * @param qualifiedNameString The string to extract the namespace from, this should be a valid
   *        qualified annotation name in Salt
   * @return the part of the passed string preceding the Salt namespace separator, or
   *         <code>null</code>.
   */
  public static String splitNamespaceFromQNameString(String qualifiedNameString) {
    String[] splitValue = qualifiedNameString.split(SaltUtil.NAMESPACE_SEPERATOR);
    if (splitValue.length == 2) {
      return splitValue[0];
    } else {
      return null;
    }
  }

  /**
   * Checks whether the passed string can be split at Salt's namespace separator and returns the
   * name part of the original string, i.e., the string following the first separator instance.
   * 
   * @param qualifiedNameString The string to extract the namespace from, this should be a valid
   *        qualified annotation name in Salt
   * @return the part of the passed string that follows the first instance of the Salt namespace
   *         operator, or the original parameter if it doesn't contain the Salt namespace operator
   */
  public static String splitNameFromQNameString(String qualifiedNameString) {
    String[] splitValue = qualifiedNameString.split(SaltUtil.NAMESPACE_SEPERATOR);
    if (splitValue.length == 2) {
      return splitValue[1];
    } else {
      return splitValue[0];
    }
  }

}
