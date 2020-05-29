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

package org.corpus_tools.hexatomic.grid.commands;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * Command to display an annotation rename dialog.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class DisplayAnnotationRenameDialogCommand implements ILayerCommand {

  public DisplayAnnotationRenameDialogCommand(NatTable natTable,
      PositionCoordinate[] selectedCellPositions) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean convertToTargetLayer(ILayer targetLayer) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ILayerCommand cloneCommand() {
    // TODO Auto-generated method stub
    return null;
  }

}
