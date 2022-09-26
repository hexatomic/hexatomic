package org.corpus_tools.hexatomic.timeline.internal.data;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STimeline;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

@Creatable
public class TliRowHeaderDataProvider implements IDataProvider {

  private SDocumentGraph graph;

  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return rowIndex;
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getColumnCount() {
    return 1;
  }

  @Override
  public int getRowCount() {
    STimeline timeline = graph.getTimeline();
    if (timeline == null) {
      return 0;
    } else {
      return timeline.getEnd() - timeline.getStart();
    }
  }

}
