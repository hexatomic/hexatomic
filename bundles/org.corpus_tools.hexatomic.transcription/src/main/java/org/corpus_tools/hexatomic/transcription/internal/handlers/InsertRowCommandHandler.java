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

package org.corpus_tools.hexatomic.transcription.internal.handlers;

import java.util.OptionalInt;
import java.util.Set;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.transcription.internal.commands.InsertRowCommand;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STimelineRelation;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link AbstractLayerCommandHandler} that handles {@link InsertRowCommand}s, i.e., the
 * insertion of new rows between existing ones. existing span column.
 * 
 * @author Thomas Krause {@literal <thomas.krause@hu-berlin.de>}
 */
public class InsertRowCommandHandler extends AbstractLayerCommandHandler<InsertRowCommand> {

  private final SDocumentGraph graph;
  private final ProjectManager projectManager;

  private Logger log = LoggerFactory.getLogger(InsertRowCommandHandler.class);

  /**
   * Creates a {@link InsertRowCommandHandler} and sets the {@link #layer} field.
   * 
   */
  public InsertRowCommandHandler(SDocumentGraph graph, ProjectManager projectManager) {
    this.graph = graph;
    this.projectManager = projectManager;
  }

  @Override
  public Class<InsertRowCommand> getCommandClass() {
    return InsertRowCommand.class;
  }

  @Override
  protected boolean doCommand(InsertRowCommand command) {
    log.debug("Executing command {}.", getCommandClass().getSimpleName());
    Set<PositionCoordinate> selectedCoordinates = command.getSelectedCells();

    OptionalInt optionalRow =
        selectedCoordinates.stream().mapToInt(PositionCoordinate::getRowPosition).min();

    if (optionalRow.isPresent()) {
      int selectedRow = optionalRow.getAsInt();

      // Append a new TLI at the end and move all existing TLIs after the selection by one
      graph.getTimeline().increasePointOfTime();
      for (STimelineRelation rel : graph.getTimelineRelations()) {
        if (rel.getStart() >= selectedRow) {
          rel.setStart(rel.getStart() + 1);
        }
        if (rel.getEnd() > selectedRow) {
          rel.setEnd(rel.getEnd() + 1);
        }
      }
      projectManager.addCheckpoint();

      return true;
    }

    return false;

  }

}
