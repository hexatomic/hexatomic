package org.corpus_tools.hexatomic.timeline.internal.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.STimelineRelation;
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
    if (this.graph.getTimeline().getData() == null) {
      this.graph.getTimeline().increasePointOfTime();
    }
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {

    Optional<SToken> tokenForDs = getTokenForTli(columnIndex, rowIndex).stream().findFirst();
    if (tokenForDs.isPresent()) {
      return tokenForDs.get();
    } else {
      return null;
    }
  }

  /** Find the tokens that are connected to TLI belonging to a data source */
  private List<SToken> getTokenForTli(int columnIndex, int rowIndex) {
    // Find the tokens that is connected to this TLI
    List<SToken> token = graph.getTimelineRelations().stream()
        .filter(rel -> rel.getStart() <= rowIndex && rel.getEnd() > rowIndex)
        .map(rel -> rel.getSource()).collect(Collectors.toList());
    // Reduce tokens to the ones belonging to the selected data source
    STextualDS ds = graph.getTextualDSs().get(columnIndex);
    return token.stream().flatMap(t -> t.getOutRelations().stream()).map(rel -> {
          if (rel instanceof STextualRelation) {
            STextualRelation textRel = (STextualRelation) rel;
            if (textRel.getTarget() == ds) {
              return textRel.getSource();
            }
          }
          return null;
    }).filter(t -> t != null).collect(Collectors.toList());
  }

  @Override
  public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    if (newValue instanceof String) {
      String newText = (String) newValue;
      // Get the DS for this column
      STextualDS ds = graph.getTextualDSs().get(columnIndex);

      // Get the token associated with this cell
      List<SToken> tokenForTli = getTokenForTli(columnIndex, rowIndex);
      if (tokenForTli.isEmpty()) {
        // No token yet, create a new one
        StringBuilder sb;
        if (ds.getText() == null) {
          sb = new StringBuilder();
        } else {
          sb = new StringBuilder(ds.getText());
        }
        if (sb.length() > 0) {
          // Add a space to the previous token
          sb.append(' ');
        }
        int startPosition = sb.length();
        sb.append(newText);
        int endPosition = sb.length();
        ds.setText(sb.toString());
        SToken newToken = graph.createToken(ds, startPosition, endPosition);
        // Align new token with the selected TLI
        STimelineRelation timeLineRel = SaltFactory.createSTimelineRelation();
        timeLineRel.setSource(newToken);
        timeLineRel.setTarget(graph.getTimeline());
        timeLineRel.setStart(rowIndex);
        timeLineRel.setEnd(rowIndex + 1);
        graph.addRelation(timeLineRel);
        if ((rowIndex + 1) == graph.getTimeline().getEnd()) {
          // Add an additional TLI at the end
          graph.getTimeline().increasePointOfTime();
        }
      } else if (tokenForTli.size() == 1) {
        // Change the text of the token
        SaltHelper.changeTokenText(tokenForTli.get(0), newText);
      }

    }
  }

  @Override
  public int getColumnCount() {
    // Every textual data source is a column
    return graph.getTextualDSs().size();
  }

  @Override
  public int getRowCount() {
    if (graph != null && graph.getTimeline() != null && graph.getTimeline().getStart() != null
        && graph.getTimeline().getEnd() != null) {
      // The number of columns is the number of timeline items
      return graph.getTimeline().getEnd() - graph.getTimeline().getStart();
    } else {
      return 0;
    }
  }


}
