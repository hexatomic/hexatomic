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

package org.corpus_tools.hexatomic.grid.internal.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.corpus_tools.hexatomic.grid.GridEditor;
import org.corpus_tools.hexatomic.grid.GridHelper;
import org.corpus_tools.hexatomic.grid.internal.actions.CreateSpanSelectionAction;
import org.corpus_tools.hexatomic.grid.internal.actions.ResolveAction;
import org.corpus_tools.hexatomic.grid.internal.commands.DisplayAnnotationRenameDialogOnCellsCommand;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.edit.action.DeleteSelectionAction;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Configures the context menu for the body region of the NatTable.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class BodyMenuConfiguration extends AbstractUiBindingConfiguration {

  private static final String DELETE_CELL_ITEM = "DELETE_CELL_ITEM"; //$NON-NLS-1$
  private Menu menu;
  private final NatTable table;
  private final SelectionLayer selectionLayer;

  private static final String CHANGE_CELL_ANNOTATION_NAME_ITEM = "CHNG_ANNO_NAME"; //$NON-NLS-1$
  private static final String CREATE_SPAN_ITEM = "CREATE_SPAN_ITEM";
  private static final String SPLIT_SPAN_ITEM = "SPLIT_SPAN_ITEM";

  /**
   * Constructor setting the table and selection layer fields, and creating the menu via
   * {@link #createMenu()}.
   * 
   * @param table The NatTable upon whose body region the menu should be constructed
   * @param selectionLayer The SelectionLayer to be used for configuring menu item states based on
   *        selection
   */
  public BodyMenuConfiguration(NatTable table, SelectionLayer selectionLayer) {
    this.table = table;
    this.selectionLayer = selectionLayer;
    this.menu = createMenu();
  }

  private Menu createMenu() {
    PopupMenuBuilder builder = new PopupMenuBuilder(this.table);
    builder.withMenuItemProvider(DELETE_CELL_ITEM, new DeleteItemProvider());
    ValidSelectionState validSelectionState = new ValidSelectionState();
    builder.withVisibleState(DELETE_CELL_ITEM, validSelectionState);
    builder.withMenuItemProvider(CHANGE_CELL_ANNOTATION_NAME_ITEM,
        new ChangeAnnotationNameItemProvider());
    builder.withVisibleState(CHANGE_CELL_ANNOTATION_NAME_ITEM, validSelectionState);
    builder.withMenuItemProvider(CREATE_SPAN_ITEM, new CreateSpanItemProvider());
    ValidSingleSpanColumnEmptySelectionState validSingleSpanColumnEmptySelectionState =
        new ValidSingleSpanColumnEmptySelectionState();
    builder.withVisibleState(CREATE_SPAN_ITEM, validSingleSpanColumnEmptySelectionState);
    builder.withMenuItemProvider(SPLIT_SPAN_ITEM, new SplitSpanItemProvider());
    ValidSingleSpanColumnSingleSelectionState validSingleSpanColumnSingleSelectionState =
        new ValidSingleSpanColumnSingleSelectionState();
    builder.withVisibleState(CREATE_SPAN_ITEM, validSingleSpanColumnSingleSelectionState);
    builder.withSeparator()
        .withMenuItemProvider(new AddAnnotationColumnMenuItemProvider(ColumnType.TOKEN_ANNOTATION));
    builder
        .withMenuItemProvider(new AddAnnotationColumnMenuItemProvider(ColumnType.SPAN_ANNOTATION));
    builder.withSeparator().withMenuItemProvider(new ResolveMenuItemProvider());
    return builder.build();
  }

  @Override
  public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
    uiBindingRegistry.registerMouseDownBinding(
        new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON),
        new PopupMenuAction(this.menu));

  }

  /**
   * Provides a menu item for resolving the data model.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private final class ResolveMenuItemProvider implements IMenuItemProvider {

    @Override
    public void addMenuItem(NatTable natTable, Menu popupMenu) {
      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText(GridEditor.REFRESH_POPUP_MENU_LABEL);
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          new ResolveAction().run(natTable, null);
        }
      });
    }

  }

  /**
   * Provides a menu item for deleting cells.
   * 
   * @author Stephan Druskat (mail@sdruskat.net)
   */
  private final class DeleteItemProvider implements IMenuItemProvider {

    @Override
    public void addMenuItem(NatTable natTable, Menu popupMenu) {
      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText(GridEditor.DELETE_CELLS_POPUP_MENU_LABEL);
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          new DeleteSelectionAction().run(natTable, null);
        }
      });
    }

  }

  /**
   * Provides a menu item for changing annotation names on selected cells.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private final class ChangeAnnotationNameItemProvider implements IMenuItemProvider {

    @Override
    public void addMenuItem(NatTable natTable, Menu popupMenu) {
      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText(GridEditor.CHANGE_ANNOTATION_NAME_POPUP_MENU_LABEL);
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          natTable.doCommand(
              new DisplayAnnotationRenameDialogOnCellsCommand(natTable, createCellMapByColumn()));
        }

        private Map<Integer, Set<Integer>> createCellMapByColumn() {
          // Map the selected cells by column.
          Map<Integer, Set<Integer>> cellMapByColumn = new HashMap<>();
          for (PositionCoordinate cellCoordinate : getSelectedNonTokenCells()) {
            Set<Integer> columnCells = cellMapByColumn.get(cellCoordinate.getColumnPosition());
            if (columnCells == null) {
              columnCells = new HashSet<>();
            }
            columnCells.add(cellCoordinate.getRowPosition());
            cellMapByColumn.put(cellCoordinate.getColumnPosition(), columnCells);
          }
          return cellMapByColumn;
        }
      });
    }

    private Set<PositionCoordinate> getSelectedNonTokenCells() {
      Set<PositionCoordinate> selectedNonTokenCells = new HashSet<>();
      PositionCoordinate[] selectedCellCoordinates = selectionLayer.getSelectedCellPositions();
      for (PositionCoordinate cellPosition : selectedCellCoordinates) {
        if (!isTokenCell(cellPosition)) {
          selectedNonTokenCells.add(cellPosition);
        }
      }
      return selectedNonTokenCells;
    }

  }

  /**
   * Provides a menu item for creating spans on selected cells.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  private final class CreateSpanItemProvider implements IMenuItemProvider {

    @Override
    public void addMenuItem(NatTable natTable, Menu popupMenu) {
      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText(GridEditor.CREATE_SPAN_POPUP_MENU_LABEL);
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          new CreateSpanSelectionAction().run(natTable);
        }
      });
    }
  }

  /**
   * Provides a menu item for splitting a selected span.
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  public class SplitSpanItemProvider implements IMenuItemProvider {

    @Override
    public void addMenuItem(NatTable natTable, Menu popupMenu) {
      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText(GridEditor.SPLIT_SPAN_POPUP_MENU_LABEL);
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          new CreateSpanSelectionAction().run(natTable);
        }
      });
    }

  }

  /**
   * A menu item state based on valid selection of cells.
   * 
   * <p>
   * {@link #isActive(NatEventData)} returns <code>true</code> only when cells are selected, and no
   * cells containing token text are selected.
   * </p>
   * 
   * @author Stephan Druskat (mail@sdruskat.net)
   */
  private class ValidSelectionState implements IMenuItemState {

    @Override
    public boolean isActive(NatEventData natEventData) {
      if (selectionLayer.getSelectedCells().isEmpty()) {
        return false;
      } else {
        PositionCoordinate[] selectedCellCoordinates = selectionLayer.getSelectedCellPositions();
        for (PositionCoordinate coord : selectedCellCoordinates) {
          // Check whether the column at the position is the token column
          if (isTokenCell(coord)) {
            return false;
          }
        }
        return true;
      }
    }
  }

  /**
   * A menu item state based on valid selection of cells.
   * 
   * <p>
   * {@link #isActive(NatEventData)} returns <code>true</code> only when all selected cells are
   * within a single span column, and all cells are empty.
   * </p>
   * 
   * @author Stephan Druskat (mail@sdruskat.net)
   */
  private class ValidSingleSpanColumnEmptySelectionState implements IMenuItemState {

    @Override
    public boolean isActive(NatEventData natEventData) {
      if (selectionLayer.getSelectedCells().isEmpty()) {
        return false;
      } else {
        PositionCoordinate[] selectedCellCoordinates = selectionLayer.getSelectedCellPositions();
        return GridHelper.areSelectedCellsInSingleSpanColumn(selectedCellCoordinates,
            selectionLayer);
      }
    }
  }

  /**
   * A menu item state based on valid selection of cells.
   * 
   * <p>
   * {@link #isActive(NatEventData)} returns <code>true</code> only when there is exactly one cell
   * selected within a single span column.
   * </p>
   * 
   * @author Stephan Druskat {@literal <mail@sdruskat.net>}
   */
  public class ValidSingleSpanColumnSingleSelectionState implements IMenuItemState {

    @Override
    public boolean isActive(NatEventData natEventData) {
      if (selectionLayer.getSelectedCells().isEmpty()) {
        return false;
      } else {
        PositionCoordinate[] selectedCellCoordinates = selectionLayer.getSelectedCellPositions();
        return GridHelper.areSelectedCellsInSingleSpanColumn(selectedCellCoordinates,
            selectionLayer) && selectedCellCoordinates.length == 1;
      }
    }

  }

  private boolean isTokenCell(PositionCoordinate cellPosition) {
    LabelStack configLabels = selectionLayer
        .getConfigLabelsByPosition(cellPosition.getColumnPosition(), cellPosition.getRowPosition());
    return configLabels.getLabels().contains(StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
  }
}
