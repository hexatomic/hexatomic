/**
 * 
 */
package org.corpus_tools.hexatomic.grid.configuration;

import org.corpus_tools.hexatomic.grid.internal.GridHelper;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.events.MouseEvent;

/**
 * A custom UI binding configuration for the column header.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ColumnHeaderUiBindingConfiguration extends AbstractUiBindingConfiguration {

  private final SelectionLayer selectionLayer;

  public ColumnHeaderUiBindingConfiguration(SelectionLayer selectionLayer) {
    this.selectionLayer = selectionLayer;
  }

  @Override
  public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {

    // Bind mouse double-clicks to renaming the annotation name
    uiBindingRegistry.registerDoubleClickBinding(MouseEventMatcher.columnHeaderLeftClick(0),
        new IMouseAction() {
          @Override
          public void run(NatTable natTable, MouseEvent event) {
            PositionCoordinate[] selectedCellCoordinates =
                selectionLayer.getSelectedCellPositions();
            // Get column and check if it's the token column
            for (PositionCoordinate coord : selectedCellCoordinates) {
              if (GridHelper.isTokenColumnAtPosition(natTable, coord.getColumnPosition(), false)) {
                System.err.println("TOKEN, IGNORING");
              } else {
                System.out.println("Double Click Detected on " + coord.getColumnPosition());
              }
            }
          }
        });
  }

}
