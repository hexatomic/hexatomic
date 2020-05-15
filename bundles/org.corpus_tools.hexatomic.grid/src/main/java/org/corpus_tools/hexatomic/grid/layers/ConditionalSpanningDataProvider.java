package org.corpus_tools.hexatomic.grid.layers;

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
public class ConditionalSpanningDataProvider extends AutomaticSpanningDataProvider {

  /**
   * @param underlyingDataProvider
   * @param autoColumnSpan
   * @param autoRowSpan
   */
  public ConditionalSpanningDataProvider(IDataProvider underlyingDataProvider,
      boolean autoColumnSpan, boolean autoRowSpan) {
    super(underlyingDataProvider, autoColumnSpan, autoRowSpan);
  }


}
