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

package org.corpus_tools.hexatomic.grid.internal.handlers;

import java.util.Set;
import org.corpus_tools.hexatomic.grid.internal.commands.SplitSpanCommand;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles {@link SplitSpanCommand}s, i.e., the
 * splitting of spans into single cell spans in an existing span column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class SplitSpanCommandHandler extends AbstractLayerCommandHandler<SplitSpanCommand> {

  private final GridFreezeLayer layer;
  private Logger log = LoggerFactory.getLogger(SplitSpanCommandHandler.class);

  /**
   * Creates a {@link SplitSpanCommandHandler} and sets the {@link #layer} field.
   * 
   * @param gridFreezeLayer the {@link GridFreezeLayer} where this handler is registered.
   */
  public SplitSpanCommandHandler(GridFreezeLayer gridFreezeLayer) {
    this.layer = gridFreezeLayer;
  }

  @Override
  public Class<SplitSpanCommand> getCommandClass() {
    return SplitSpanCommand.class;
  }

  @Override
  protected boolean doCommand(SplitSpanCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    Set<PositionCoordinate> selectedCoordinates = command.getSelectedNonTokenCells();
    // Delegate the actual span splitting to the layer.
    layer.splitAnnotationSpans(selectedCoordinates);
    return true;
  }

}
