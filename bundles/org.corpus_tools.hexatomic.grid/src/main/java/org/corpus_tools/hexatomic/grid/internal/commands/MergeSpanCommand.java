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

import java.util.List;
import org.corpus_tools.salt.common.SSpan;
import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;

/**
 * An {@link AbstractContextFreeCommand} that is used to merge annotated spans into a single span in
 * an existing span column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class MergeSpanCommand extends AbstractContextFreeCommand {

  private final List<SSpan> spans;
  private final PositionCoordinate[] cellPositions;

  /**
   * Creates a new {@link MergeSpanCommand}.
   * 
   * @param spans The {@link SSpan}s to merge
   * @param cellPositions The selected coordinates of the {@link SSpan} to merge
   */
  public MergeSpanCommand(List<SSpan> spans, PositionCoordinate[] cellPositions) {
    this.spans = spans;
    this.cellPositions = cellPositions;
  }

  /**
   * Returns the selected coordinates to the {@link SSpan} to merge.
   * 
   * @return the selectedSpans
   */
  public final PositionCoordinate[] getSelectedCoordinates() {
    return cellPositions;
  }

  /**
   * Returns the {@link SSpan}s to merge.
   * 
   * @return the span
   */
  public final List<SSpan> getSpans() {
    return spans;
  }

}
