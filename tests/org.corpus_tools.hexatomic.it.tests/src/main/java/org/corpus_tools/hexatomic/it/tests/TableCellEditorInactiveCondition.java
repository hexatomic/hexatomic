package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public final class TableCellEditorInactiveCondition extends DefaultCondition {
  private final SWTBotNatTable table;

  public TableCellEditorInactiveCondition(SWTBotNatTable table) {
    this.table = table;
  }

  @Override
  public boolean test() throws Exception {
    return table.widget.getActiveCellEditor() == null;
  }

  @Override
  public String getFailureMessage() {
    return "Setting new value for cell took too long.";
  }
}
