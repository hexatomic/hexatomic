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

package org.corpus_tools.hexatomic.grid.internal;

import org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;

/**
 * Helper class to access grid information.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class GridHelper {

  private GridHelper() {
    // Should not be initiated.
  }

  /**
   * Determines whether the column in the {@link NatTable} as determined per the passed
   * columnPosition integer is a column containing token text values.
   * 
   * <p>
   * Note that if the column position is not got from a {@link NatEventData} object, it must be
   * incremented by 1, as the row header column at 0) is counted in this case.
   * </p>
   * 
   * @param natTable The NatTable for which the column position is passed
   * @param columnPosition The column position to determine for whether it contains token text
   *        values
   * @param columnPositionDerivedFromNatEventData Whether the column position passed into this
   *        method is directly derived from a {@link NatEventData} object. If <code>true</code>, the
   *        column position can be used without adding 1, if not, 1 must be added to it.
   * @return whether the given column position covers a token column
   */
  public static boolean isTokenColumnAtPosition(NatTable natTable, int columnPosition,
      boolean columnPositionDerivedFromNatEventData) {
    int positionToCheck = -1;
    if (columnPositionDerivedFromNatEventData) {
      positionToCheck = columnPosition;
    } else {
      positionToCheck = columnPosition + 1;
    }
    ILayerCell cell = natTable.getCellByPosition(positionToCheck, 0);
    if (!cell.getDataValue().equals(GraphDataProvider.TOKEN_TEXT_COLUMN_LABEL)) {
      return false;
    }
    return true;
  }

}
