package org.corpus_tools.hexatomic.transcription.internal.data;

import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

public class LabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final ILayer layer;

  public LabelAccumulator(ILayer layer) {
    super(layer);
    this.layer = layer;
  }

  @Override
  public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
    // Inherit defaults
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

    Object value = layer.getDataValueByPosition(columnPosition, rowPosition);
    if (value == null) {
      configLabels.addLabel(StyleConfiguration.EMPTY_CELL_STYLE);
    } else {
      configLabels.addLabel(StyleConfiguration.TOKEN_TEXT_CELL_STYLE);
    }
  }

}
