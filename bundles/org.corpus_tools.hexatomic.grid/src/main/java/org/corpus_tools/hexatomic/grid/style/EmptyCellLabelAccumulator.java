package org.corpus_tools.hexatomic.grid.style;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;

/**
 * A cell label accumulator which assigns empty cells a custom configuration label.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class EmptyCellLabelAccumulator implements IConfigLabelAccumulator {

  private final DefaultBodyLayerStack bodyLayer;

  /**
   * Constructor setting the {@link #bodyLayer} field.
   * 
   * A {@link DefaultBodyLayerStack} is needed to determine properties of cells that should be
   * assigned custom configuration labels.
   * 
   * @param bodyLayer
   */
  public EmptyCellLabelAccumulator(DefaultBodyLayerStack bodyLayer) {
    this.bodyLayer = bodyLayer;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    ILayerCell cell = bodyLayer.getCellByPosition(columnPosition, rowPosition);
    if (cell.getDataValue() == null) {
      configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
    }
  }

}
