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

import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeColumnCommand;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeRowCommand;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

/**
 * A configuration class for header popup menus restricting the possible actions on columns, and
 * disabling all non-column menu items.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class ColumnHeaderMenuConfiguration extends AbstractHeaderMenuConfiguration {

  private static final String COLUMN = "column"; //$NON-NLS-1$
  private static final String ROW = "row"; //$NON-NLS-1$

  /**
   * Generic constructor calling
   * {@link AbstractHeaderMenuConfiguration#AbstractHeaderMenuConfiguration(NatTable)}.
   * 
   * @param natTable The NatTable to configure
   */
  public ColumnHeaderMenuConfiguration(NatTable natTable) {
    super(natTable);
  }

  @Override
  protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
    // Restrict to useful menu items
    return super.createColumnHeaderMenu(natTable).withHideColumnMenuItem("Hide column(s)")
        .withShowAllColumnsMenuItem("Show all columns").withSeparator()
        .withAutoResizeSelectedColumnsMenuItem("Auto-resize column(s)").withSeparator()
        .withFreezeColumnMenuItem("Set column freeze")
        .withMenuItemProvider(withToggleColumnFreezeMenuItemProvider(COLUMN)).withSeparator()
        .withColumnRenameDialog("Change annotation name")
        .withVisibleState(PopupMenuBuilder.COLUMN_RENAME_MENU_ITEM_ID, new AnnotationHeaderState())
        .withSeparator()
        .withMenuItemProvider(new AddAnnotationColumnMenuItemProvider(ColumnType.TOKEN_ANNOTATION))
        .withMenuItemProvider(new AddAnnotationColumnMenuItemProvider(ColumnType.SPAN_ANNOTATION));
  }

  @Override
  protected PopupMenuBuilder createRowHeaderMenu(NatTable natTable) {
    return super.createRowHeaderMenu(natTable)
        .withAutoResizeSelectedRowsMenuItem("Auto-resize row(s)").withSeparator()
        .withFreezeRowMenuItem("Set row freeze")
        .withMenuItemProvider(withToggleColumnFreezeMenuItemProvider(ROW));
  }

  /**
   * Statically provides an {@link IMenuItemProvider} which provides a menu item to toggle the
   * freeze state for columns.
   * 
   * @param typeSwitch The type fo freeze (column, row) to toggle
   * 
   * @return IMenuItemProvider An item provider for a menu item which toggles the freeze state
   */
  public static IMenuItemProvider withToggleColumnFreezeMenuItemProvider(final String typeSwitch) {
    return (natTable, popupMenu) -> {
      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
      menuItem.setText("Toggle freeze");
      menuItem.setEnabled(true);

      switch (typeSwitch) {
        case COLUMN:
          menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              int columnPosition = MenuItemProviders.getNatEventData(e).getColumnPosition();
              natTable.doCommand(new FreezeColumnCommand(natTable, columnPosition, true, false));
            }
          });
          break;

        case ROW:
          menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              int rowPosition = MenuItemProviders.getNatEventData(e).getRowPosition();
              natTable.doCommand(new FreezeRowCommand(natTable, rowPosition, true, false));
            }
          });
          break;

        default:
          break;
      }
    };
  }
}
