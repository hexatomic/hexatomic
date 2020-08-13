/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal;

import org.corpus_tools.hexatomic.grid.data.GraphDataProvider;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;

/**
 * Helper class to access grid information.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class GridHelper {

  /**
   * @param natTable
   * @param columnPosition
   * @param columnPositionDerivedFromNatEventData Whether the column position passed into this
   *        method is directly derived from a {@link NatEventData} object. If <code>true</code>, the
   *        column position can be used without adding 1, if not, 1 must be added to it.
   * @return
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
