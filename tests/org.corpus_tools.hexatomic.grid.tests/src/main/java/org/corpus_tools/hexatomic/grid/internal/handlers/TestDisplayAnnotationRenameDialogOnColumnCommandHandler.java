package org.corpus_tools.hexatomic.grid.internal.handlers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.hexatomic.grid.LayerSetupException;
import org.eclipse.nebula.widgets.nattable.columnRename.DisplayColumnRenameDialogCommand;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DisplayAnnotationRenameDialogOnColumnCommandHandler}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestDisplayAnnotationRenameDialogOnColumnCommandHandler {

  /**
   * <p>
   * Tests that a {@link LayerSetupException} is thrown when the column header layer is not of the
   * expected type.
   * </p>
   */
  @Test
  void testDoCommandDisplayColumnRenameDialogCommand() {
    ColumnHeaderLayer layer = mock(ColumnHeaderLayer.class);
    DisplayColumnRenameDialogCommand command = mock(DisplayColumnRenameDialogCommand.class);
    when(command.getColumnPosition()).thenReturn(1);
    DisplayAnnotationRenameDialogOnColumnCommandHandler fixture =
        new DisplayAnnotationRenameDialogOnColumnCommandHandler(layer);
    assertThrows(LayerSetupException.class, () -> fixture.doCommand(command));
  }

}
