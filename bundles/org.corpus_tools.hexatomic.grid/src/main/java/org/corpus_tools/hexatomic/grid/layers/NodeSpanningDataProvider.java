package org.corpus_tools.hexatomic.grid.layers;

import java.util.ArrayList;
import java.util.List;
import org.corpus_tools.hexatomic.grid.data.GraphDataProvider;
import org.corpus_tools.salt.common.SStructuredNode;
import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * A spanning data provider which spans automatically based on the condition that the underlying
 * data object is the same for the adjacent cells to be spanned. This differs from the default
 * {@link AutomaticSpanningDataProvider}, which spans based on cell value, which is a string. This
 * implementation is necessary, as two adjacent cells may share the same value string, which however
 * may be from two different annotations across two different model objects.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class NodeSpanningDataProvider extends AutomaticSpanningDataProvider {

  /**
   * The {@link GraphDataProvider} that is wrapped by this {@link NodeSpanningDataProvider}.
   */
  private final GraphDataProvider graphDataProvider;

  /**
   * List of column positions for which automatic spanning is enabled. If this list is empty, all
   * columns will do auto row spanning.
   */
  private List<Integer> autoSpanColumns = new ArrayList<Integer>();

  /**
   * List of row positions for which automatic spanning is enabled. If this list is empty, all rows
   * will do auto column spanning.
   */
  private List<Integer> autoSpanRows = new ArrayList<Integer>();

  /**
   * Constructor limiting the parameter <code>underlyingDataProvider</code> to instance of
   * {@link GraphDataProvider}, that this class needs wo work on nodes rather than strings for
   * equality checks. If another type is passed as data provider, an unchecked
   * IllegalArgumentException is thrown.
   * 
   * @param underlyingDataProvider The underlying data provider of type {@link GraphDataProvider}
   * @param autoColumnSpan Flag to configure automatic column spanning
   * @param autoRowSpan Flag to configure automatic row spanning
   */
  public NodeSpanningDataProvider(IDataProvider underlyingDataProvider, boolean autoColumnSpan,
      boolean autoRowSpan) {
    super(underlyingDataProvider, autoColumnSpan, autoRowSpan);
    if (underlyingDataProvider instanceof GraphDataProvider) {
      this.graphDataProvider = (GraphDataProvider) underlyingDataProvider;
    } else {
      throw new IllegalArgumentException("Error setting underlying data provider to an instance of "
          + underlyingDataProvider.getClass().getCanonicalName() + ". Underlying data provider in "
          + this.getClass().getCanonicalName() + " must be of type "
          + GraphDataProvider.class.getCanonicalName() + ".");
    }
    super.setAutoColumnSpan(autoColumnSpan);
    super.setAutoRowSpan(autoRowSpan);
  }

  /**
   * Checks if the given column position is configured as an auto span column.
   *
   * @param columnPosition The column position to check
   * @return <code>true</code> if the given column position is configured as an auto span column.
   */
  private boolean isAutoSpanColumn(int columnPosition) {
    return (this.autoSpanColumns.isEmpty() || this.autoSpanColumns.contains(columnPosition));
  }

  /**
   * Checks if the given row position is configured as an auto span row.
   *
   * @param rowPosition The row position to check
   * @return <code>true</code> if the given row position is configured as an auto span row.
   */
  private boolean isAutoSpanRow(int rowPosition) {
    return (this.autoSpanRows.isEmpty() || this.autoSpanRows.contains(rowPosition));
  }

  /**
   * Checks if the value of the column to the left of the given column position is contained in the
   * same node. In this case the given column is spanned with the one to the left and therefore that
   * column position will be returned here. In contrast to the overriden method, this method works
   * on the underlying {@link SStructuredNode} model objects of the cell, not the cell values.
   *
   * @param columnPosition The column position whose spanning starting column is searched
   * @param rowPosition The row position where the column spanning should be performed.
   * @return The column position where the spanning starts or the given column position if it is not
   *         spanned with the columns to the left.
   */
  @Override
  protected int getStartColumnPosition(int columnPosition, int rowPosition) {
    int columnPos;
    for (columnPos = columnPosition; columnPos >= 0; columnPos--) {
      if (columnPos <= 0 || !isAutoSpanColumn(columnPos) || !isAutoSpanColumn(columnPos - 1)) {
        break;
      }

      // Get underlying node for the given column
      SStructuredNode current = graphDataProvider.getNode(columnPos, rowPosition);
      // Get underlying node for the column left of the given column
      SStructuredNode before = graphDataProvider.getNode(columnPos - 1, rowPosition);

      if (valuesNotEqual(current, before)) {
        // Values are not equal, so stop here and return the given column position
        break;
      }
    }
    return columnPos;
  }

  /**
   * Checks if the value of the row above the given row position is contained in the same node. In
   * this case the given row is spanned with the above and therefore the above row position will be
   * returned here. In contrast to the overriden method, this method works on the underlying
   * {@link SStructuredNode} model objects of the cell, not the cell values.
   *
   * @param columnPosition The column position for which the row spanning should be checked
   * @param rowPosition The row position whose spanning state should be checked.
   * @return The row position where the spanning starts or the given row position if it is not
   *         spanned with rows above.
   */
  protected int getStartRowPosition(int columnPosition, int rowPosition) {
    int rowPos;
    for (rowPos = rowPosition; rowPos >= 0; rowPos--) {
      if (rowPos <= 0 || !isAutoSpanRow(rowPos) || !isAutoSpanRow(rowPos - 1)) {
        break;
      }

      // Get the underlying node of the given row
      SStructuredNode current = graphDataProvider.getNode(columnPosition, rowPos);
      // Get the underlying node of the row above
      SStructuredNode before = graphDataProvider.getNode(columnPosition, rowPos - 1);

      if (valuesNotEqual(current, before)) {
        // Values are not equal, so stop here and return the given row position
        break;
      }
    }
    return rowPos;
  }

  /**
   * Calculates the number of columns to span regarding the underlying node data of the cells. In
   * contrast to the overriden method, this method works on the underlying {@link SStructuredNode}
   * model objects of the cell, not the cell values.
   *
   * @param columnPosition The column position to start the check for spanning
   * @param rowPosition The row position for which the column spanning should be checked
   * @return The number of columns to span
   */
  protected int getColumnSpan(int columnPosition, int rowPosition) {
    int span = 1;

    while (columnPosition < getColumnCount() - 1 && isAutoSpanColumn(columnPosition)
        && isAutoSpanColumn(columnPosition + 1)
        && !valuesNotEqual(graphDataProvider.getNode(columnPosition, rowPosition),
            graphDataProvider.getNode(columnPosition + 1, rowPosition))) {
      span++;
      columnPosition++;
    }
    return span;
  }

  /**
   * Calculates the number of rows to span regarding the node data of the cells. In contrast to the
   * overriden method, this method works on the underlying {@link SStructuredNode} model objects of
   * the cell, not the cell values.
   *
   * @param columnPosition The column position for which the row spanning should be checked
   * @param rowPosition The row position to start the check for spanning
   * @return The number of rows to span
   */
  protected int getRowSpan(int columnPosition, int rowPosition) {
    int span = 1;

    while (rowPosition < getRowCount() - 1 && isAutoSpanRow(rowPosition)
        && isAutoSpanRow(rowPosition + 1)
        && !valuesNotEqual(graphDataProvider.getNode(columnPosition, rowPosition),
            graphDataProvider.getNode(columnPosition, rowPosition + 1))) {
      span++;
      rowPosition++;
    }
    return span;
  }

}
