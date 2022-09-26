package org.corpus_tools.hexatomic.timeline.internal.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;

public class TliCornerDataProvider extends DefaultCornerDataProvider {

  public TliCornerDataProvider(IDataProvider columnHeaderDataProvider,
      IDataProvider rowHeaderDataProvider) {
    super(columnHeaderDataProvider, rowHeaderDataProvider);
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return "TLI";
  }

}
