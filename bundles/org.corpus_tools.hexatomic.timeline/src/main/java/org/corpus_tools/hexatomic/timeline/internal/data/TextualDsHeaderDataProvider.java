package org.corpus_tools.hexatomic.timeline.internal.data;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

@Creatable
public class TextualDsHeaderDataProvider implements IDataProvider {

  private SDocumentGraph graph;

  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return graph.getTextualDSs().get(columnIndex).getName();
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getColumnCount() {
    // Every textual data source is a column
    return graph.getTextualDSs().size();
  }

  @Override
  public int getRowCount() {
    return 1;
  }

}
