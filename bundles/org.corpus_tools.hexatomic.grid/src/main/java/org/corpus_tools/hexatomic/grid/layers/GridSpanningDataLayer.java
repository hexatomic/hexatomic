/**
 * 
 */
package org.corpus_tools.hexatomic.grid.layers;

import org.corpus_tools.hexatomic.grid.data.NodeSpanningDataProvider;
import org.corpus_tools.hexatomic.grid.handlers.DisplayAnnotationRenameDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;

/**
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridSpanningDataLayer extends SpanningDataLayer {

  public GridSpanningDataLayer(NodeSpanningDataProvider spanningDataProvider) {
    super(spanningDataProvider);
  }

  @Override
  protected void registerCommandHandlers() {
    super.registerCommandHandlers();
    registerCommandHandler(new DisplayAnnotationRenameDialogCommandHandler(this));
  }

}
