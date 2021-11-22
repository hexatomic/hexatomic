package org.corpus_tools.hexatomic.grid.internal.events;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;

/**
 * Signals that a full model resolution should be triggered.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class TriggerResolutionEvent implements ILayerEvent {

  @Override
  public boolean convertToLocal(ILayer localLayer) {
    // Unimplemented
    return false;
  }

  @Override
  public ILayerEvent cloneEvent() {
    return this;
  }

}
