/*-
 * #%L
 * [bundle] Hexatomic Grid Editor
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

import org.corpus_tools.hexatomic.grid.GridEditor;
import org.corpus_tools.hexatomic.grid.internal.actions.AddColumnAction;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * An {@link IMenuItemProvider} to provide a menu item for the addition of annotation columns.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class AddAnnotationColumnMenuItemProvider implements IMenuItemProvider {

  private final ColumnType columnType;

  public AddAnnotationColumnMenuItemProvider(ColumnType columnType) {
    this.columnType = columnType;
  }

  @Override
  public void addMenuItem(NatTable natTable, Menu popupMenu) {
    MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
    switch (this.columnType) {
      case TOKEN_ANNOTATION:
        item.setText(GridEditor.ADD_TOK_ANNO_COL_POPUP_MENU_LABEL);
        break;

      case SPAN_ANNOTATION:
        item.setText(GridEditor.ADD_SPAN_ANNO_COL_POPUP_MENU_LABEL);
        break;

      default:
        break;
    }
    item.setEnabled(true);
    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        NatEventData natEventData = MenuItemProviders.getNatEventData(event);
        int columnPosition = natEventData.getColumnPosition();
        int rowPosition = natEventData.getRowPosition();
        ILayer layer = natTable.getUnderlyingLayerByPosition(columnPosition, rowPosition);
        int colIndex = layer.getColumnIndexByPosition(columnPosition);
        int rowIndex = layer.getRowIndexByPosition(rowPosition);
        new AddColumnAction(AddAnnotationColumnMenuItemProvider.this.columnType, colIndex, rowIndex)
            .run(natTable);
      }
    });
  }
}
