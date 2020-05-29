package org.corpus_tools.hexatomic.grid.configuration;

import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;

/**
 * A menu item state which is active on column indices > 1 (i.e., excluding the index and token text
 * columns).
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class AnnotationHeaderState implements IMenuItemState {

  @Override
  public boolean isActive(NatEventData natEventData) {
    return natEventData.getColumnPosition() > 1;
  }

}