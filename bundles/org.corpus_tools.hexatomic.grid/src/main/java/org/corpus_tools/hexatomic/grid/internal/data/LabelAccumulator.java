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

package org.corpus_tools.hexatomic.grid.internal.data;

import java.util.List;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * A label accumulator which assigns cells custom configuration labels whereby they can be
 * identified for configuration.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class LabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final SpanningDataLayer bodyDataLayer;
  private final GraphDataProvider bodyDataProvider;

  /**
   * Constructor setting the {@link #bodyDataLayer} field, and the {@link #bodyDataProvider} field.
   * 
   * <p>
   * The lowermost layer in the layer stack is needed to determine properties of cells that should
   * be assigned custom configuration labels. Similarly, the dataProvider is needed to access
   * properties of model elements to determine conditional styling of cells.
   * </p>
   * 
   * @param bodyDataLayer The {@link SpanningDataLayer} to set to the {@link #bodyDataLayer} field
   * @param bodyDataProvider The {@link GraphDataProvider} to set to the {@link #bodyDataProvider}
   *        field
   */
  public LabelAccumulator(SpanningDataLayer bodyDataLayer, GraphDataProvider bodyDataProvider) {
    super(bodyDataLayer);
    if (bodyDataLayer == null || bodyDataProvider == null) {
      throw new IllegalArgumentException(
          "Arguments may not be null in " + this.getClass().getSimpleName() + ".");
    }
    this.bodyDataLayer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    // Inherit defaults
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

    ILayerCell bodyCell = bodyDataLayer.getCellByPosition(columnPosition, rowPosition);

    // Assign the empty cell style iff the cell in the data body layer is null
    if (bodyCell != null) {
      if (bodyCell.getDataValue() == null) {
        configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
      }

      int columnIndex = bodyDataLayer.getColumnIndexByPosition(columnPosition);
      List<Column> columns = bodyDataProvider.getColumns();
      if (!columns.isEmpty()) {
        ColumnType columnType = columns.get(columnIndex).getColumnType();
        if (columnType == ColumnType.SPAN_ANNOTATION) {
          // Assign the span annotation style label to cells in columns with the respective flag.
          configLabels.addLabel(StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE);
        } else if (columnType == ColumnType.TOKEN_TEXT) {
          // Assign the token text style label to cells in columns with the respective flag.
          configLabels.addLabel(StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
        }
      }
    } else {
      throw new HexatomicRuntimeException("There is no cell at column position " + columnPosition
          + ", row position " + rowPosition + ".");
    }
  }

}
