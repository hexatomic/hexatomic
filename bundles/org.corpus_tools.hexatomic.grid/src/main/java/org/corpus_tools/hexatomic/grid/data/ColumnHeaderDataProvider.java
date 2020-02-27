package org.corpus_tools.hexatomic.grid.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * A data provider for column headers.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class ColumnHeaderDataProvider implements IDataProvider {

  private GraphDataProvider provider;

  public ColumnHeaderDataProvider(GraphDataProvider provider) {
    this.provider = provider;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return provider.getColumns().get(columnIndex).getHeader();
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // Not implemented, as header values should not be settable.
  }

  @Override
  public int getColumnCount() {
    return provider.getColumnCount();
  }

  @Override
  public int getRowCount() {
    return 1;
  }

}
