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

package org.corpus_tools.hexatomic.grid.internal.commands;

import org.corpus_tools.salt.common.SSpan;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link AbstractContextFreeCommand} that is used to split annotated spans into single cell
 * spans in an existing span column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class SplitSpanCommand extends AbstractContextFreeCommand {

  private final SSpan span;
  private final PositionCoordinate[] cellPositions;

  /**
   * Creates a new {@link SplitSpanCommand}.
   * 
   * @param span The {@link SSpan} to split
   * @param cellPositions The selected coordinates of the {@link SSpan} to split
   */
  public SplitSpanCommand(SSpan span, PositionCoordinate[] cellPositions) {
    this.span = span;
    this.cellPositions = cellPositions;
  }

  /**
   * Returns the selected coordinates to the {@link SSpan} to split.
   * 
   * @return the selectedSpans
   */
  public final PositionCoordinate[] getSelectedCoordinates() {
    return cellPositions;
  }

  /**
   * Returns the {@link SSpan} to split.
   * 
   * @return the span
   */
  public final SSpan getSpan() {
    return span;
  }

}
