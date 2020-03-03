package org.corpus_tools.hexatomic.grid.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class RowHeaderDataProvider implements IDataProvider {

  private final GraphDataProvider provider;

  public RowHeaderDataProvider(GraphDataProvider bodyDataProvider) {
    this.provider = bodyDataProvider;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return rowIndex + 1;
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // Left unimplemented, as header data shouldn't be settable

  }

  @Override
  public int getColumnCount() {
    return 1;
  }

  @Override
  public int getRowCount() {
    return provider.getRowCount();
  }

}
