/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.events;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;

/**
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class ColumnsChangedEvent implements ILayerEvent {

  @Override
  public boolean convertToLocal(ILayer localLayer) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ILayerEvent cloneEvent() {
    return this;
  }

}
