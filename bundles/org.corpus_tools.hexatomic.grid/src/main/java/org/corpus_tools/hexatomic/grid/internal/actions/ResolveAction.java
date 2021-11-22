package org.corpus_tools.hexatomic.grid.internal.actions;

import org.corpus_tools.hexatomic.grid.GridHelper;
import org.corpus_tools.hexatomic.grid.internal.events.TriggerResolutionEvent;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.swt.events.KeyEvent;

/**
 * Resolves the model for a given {@link NatTable} from scratch.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class ResolveAction implements IContextFreeAction, IKeyAction {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ResolveAction.class);

  @Override
  public void run(NatTable natTable, KeyEvent event) {
    run(natTable);
  }

  @Override
  public void run(NatTable natTable) {
    log.trace("Firing event to trigger model resolution on body data layer");
    GridHelper.getBodyLayer(natTable).fireLayerEvent(new TriggerResolutionEvent());
  }

}
