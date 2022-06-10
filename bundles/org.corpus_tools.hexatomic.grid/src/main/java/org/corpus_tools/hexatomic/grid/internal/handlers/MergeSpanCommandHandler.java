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

import java.util.List;
import org.corpus_tools.hexatomic.grid.internal.commands.MergeSpanCommand;
import org.corpus_tools.hexatomic.grid.internal.events.ColumnsChangedEvent;
import org.corpus_tools.hexatomic.grid.internal.layers.GridFreezeLayer;
import org.corpus_tools.salt.common.SSpan;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles {@link MergeSpanCommand}s, i.e., the merging
 * of spans into a single cell span in an existing span column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class MergeSpanCommandHandler extends AbstractLayerCommandHandler<MergeSpanCommand> {

  private final GridFreezeLayer layer;
  private Logger log = LoggerFactory.getLogger(MergeSpanCommandHandler.class);

  /**
   * Creates a {@link MergeSpanCommandHandler} and sets the {@link #layer} field.
   * 
   * @param gridFreezeLayer the {@link GridFreezeLayer} where this handler is registered.
   */
  public MergeSpanCommandHandler(GridFreezeLayer gridFreezeLayer) {
    this.layer = gridFreezeLayer;
  }

  @Override
  public Class<MergeSpanCommand> getCommandClass() {
    return MergeSpanCommand.class;
  }

  @Override
  protected boolean doCommand(MergeSpanCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    PositionCoordinate[] coordinates = command.getSelectedCoordinates();
    List<SSpan> spans = command.getSpans();
    // Delegate the actual span merging to the layer.
    layer.mergeAnnotationSpans(spans, coordinates);
    layer.fireLayerEvent(new ColumnsChangedEvent());
    return true;
  }

}
