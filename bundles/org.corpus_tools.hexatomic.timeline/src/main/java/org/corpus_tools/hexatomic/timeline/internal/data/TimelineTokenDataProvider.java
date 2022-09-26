package org.corpus_tools.hexatomic.timeline.internal.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

@Creatable
public class TimelineTokenDataProvider implements IDataProvider {

  private SDocumentGraph graph;

  public void setGraph(SDocumentGraph graph) {
    this.graph = graph;
    // TODO: make this a question to the user
    if (this.graph.getTimeline() == null) {
      this.graph.createTimeline();
    }
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    if (columnIndex == 0) {
      // TODO: get optional time when connected to a medial data source
      return "";
    } else {
      // Find the tokens that is connected to this TLI
      List<SToken> token = graph.getTimelineRelations().stream()
          .filter(rel -> rel.getStart() <= rowIndex && rel.getEnd() > rowIndex)
          .map(rel -> rel.getSource()).collect(Collectors.toList());
      // Reduce tokens to the ones belonging to the selected data source
      STextualDS ds = graph.getTextualDSs().get(columnIndex - 1);
      Optional<SToken> tokenForDs =
          token.stream().flatMap(t -> t.getOutRelations().stream()).map(rel -> {
            if (rel instanceof STextualRelation) {
              STextualRelation textRel = (STextualRelation) rel;
              if (textRel.getTarget() == ds) {
                return textRel.getSource();
              }
            }
            return null;
          }).filter(t -> t != null).findFirst();
      if (tokenForDs.isPresent()) {
        return graph.getText(tokenForDs.get());
      } else {
        return null;
      }
    }
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getColumnCount() {
    // The timeline items are a column + every textual data source is a column
    return 1 + graph.getTextualDSs().size();
  }

  @Override
  public int getRowCount() {
    // The number of columns is the number of timeline items
    return graph.getTimeline().getEnd() - graph.getTimeline().getStart();
  }


}
