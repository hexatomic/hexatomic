package org.corpus_tools.hexatomic.timeline.internal.data;

import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

@Creatable
public class GraphDataProvider implements IDataProvider {

  @Inject
  private ProjectManager projectManager;

  private SDocumentGraph graph;

  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getColumnCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getRowCount() {
    // TODO Auto-generated method stub
    return 0;
  }


}
