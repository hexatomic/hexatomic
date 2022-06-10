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

package org.corpus_tools.hexatomic.grid.internal.actions;

import java.util.ArrayList;
import java.util.List;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.GridHelper;
import org.corpus_tools.hexatomic.grid.internal.commands.MergeSpanCommand;
import org.corpus_tools.salt.common.SSpan;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * An {@link IContextFreeAction} that triggers a {@link MergeSpanCommand} for the currently selected
 * spans in an existing span column.
 * 
 * <p>
 * As the action is only being made available when there is more than one non-null {@link SSpan}
 * selected, this assumption can safely be operated upon.
 * </p>
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class MergeSpanSelectionAction implements IContextFreeAction, IKeyAction {

  @Override
  public void run(NatTable natTable) {
    SelectionLayer selectionLayer = GridHelper.getBodyLayer(natTable).getSelectionLayer();
    PositionCoordinate[] cellPositions = selectionLayer.getSelectedCellPositions();
    List<SSpan> spans = new ArrayList<>();
    if (GridHelper.areSelectedCellsInSingleSpanColumn(cellPositions, selectionLayer)) {
      for (PositionCoordinate position : cellPositions) {
        spans.add((SSpan) natTable.getDataValueByPosition(position.getColumnPosition() + 1,
            position.getRowPosition() + 1));
      }
    }
    for (SSpan span : spans) {
      if (span == null) {
        throw new HexatomicRuntimeException(
            "Expected only non-null spans to be selected, but this was not the case.");
      }
    }
    natTable.doCommand(new MergeSpanCommand(spans, cellPositions));
  }

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    run(natTable);
  }

}
