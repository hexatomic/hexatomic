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

package org.corpus_tools.hexatomic.grid.internal.data;

import java.util.Objects;
import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * A spanning data provider which spans automatically based on the condition that the underlying
 * data object is the same for the adjacent cells to be spanned. This differs from the default
 * {@link AutomaticSpanningDataProvider} in that adjacent cells with <code>null</code> values are
 * treated as being unequal rather than equal.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class NodeSpanningDataProvider extends AutomaticSpanningDataProvider {

  public NodeSpanningDataProvider(IDataProvider underlyingDataProvider) {
    super(underlyingDataProvider, false, true);
    if (!(underlyingDataProvider instanceof GraphDataProvider)) {
      throw new IllegalArgumentException("Error setting underlying data provider to an instance of "
          + underlyingDataProvider.getClass().getCanonicalName() + ". Underlying data provider in "
          + this.getClass().getCanonicalName() + " must be of type "
          + GraphDataProvider.class.getCanonicalName() + ".");
    }
  }

  /**
   * Checks if the given values are not equal. This method is <code>null</code> safe. In contrast to
   * the overridden method, this method returns <code>true</code> if both values are
   * <code>null</code>! This is because two adjacent cells containing <code>null</code> should not
   * be spanned, but kept separate for separate targeting of editing actions.
   * 
   * @param value1 The first value to check for equality with the second value
   * @param value2 The second value to check for equality with the first value.
   * @return <code>true</code> if the given values are not equal, or if both values are
   *         <code>null</code>
   */
  @Override
  protected boolean valuesNotEqual(Object value1, Object value2) {
    if (value1 == null && value2 == null) {
      return true;
    } else {
      return !Objects.equals(value1, value2);
    }
  }

}
