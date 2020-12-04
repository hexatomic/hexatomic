package org.corpus_tools.hexatomic.grid.internal.actions;

import org.eclipse.nebula.widgets.nattable.NatTable;

/**
 * An action that runs independently of context beyond the {@link NatTable} is belongs to.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
interface IContextFreeAction {

  /**
   * Runs the defined action, e.g., a command that is registered with a layer in the NatTable.
   * 
   * @param natTable The {@link NatTable} this action belongs to
   */
  public void run(NatTable natTable);

}
