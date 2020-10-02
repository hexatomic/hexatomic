package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ColumnHeaderDataProvider}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestColumnHeaderDataProvider {

  private ColumnHeaderDataProvider fixture = null;

  /**
   * Sets up the fixture.
   */
  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    GraphDataProvider fixtureProvider = mock(GraphDataProvider.class);
    List<Column> columns = mock(List.class);
    Column tokenColumn = mock(Column.class);
    Column spanColumn = mock(Column.class);
    when(tokenColumn.getHeader()).thenReturn("ZERO");
    when(columns.get(0)).thenReturn(tokenColumn);
    when(spanColumn.getHeader()).thenReturn("ONE");
    when(spanColumn.getColumnValue()).thenReturn("OLD");
    when(columns.get(1)).thenReturn(spanColumn);
    when(columns.size()).thenReturn(2);
    when(fixtureProvider.getColumns()).thenReturn(columns);
    when(fixtureProvider.getColumnCount()).thenReturn(2);
    when(fixtureProvider.getRowCount()).thenReturn(3);
    fixture = new ColumnHeaderDataProvider(fixtureProvider, mock(ProjectManager.class));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#ColumnHeaderDataProvider(org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testColumnHeaderDataProvider() {
    assertDoesNotThrow(() -> new ColumnHeaderDataProvider(mock(GraphDataProvider.class),
        mock(ProjectManager.class)));
    assertThrows(HexatomicRuntimeException.class,
        () -> new ColumnHeaderDataProvider(null, mock(ProjectManager.class)));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#getDataValue(int, int)}.
   */
  @Test
  void testGetDataValue() {
    assertEquals("ZERO", fixture.getDataValue(0, 0));
    assertEquals("ONE", fixture.getDataValue(1, 0));
    assertNull(fixture.getDataValue(-1, 0));
    assertEquals("ZERO", fixture.getDataValue(0, 2));
    assertNull(fixture.getDataValue(-1, 2));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#setDataValue(int, int, java.lang.Object)}.
   */
  @Test
  void testSetDataValue() {
    assertThrows(HexatomicRuntimeException.class, () -> fixture.setDataValue(-1, -1, null));
    assertThrows(HexatomicRuntimeException.class, () -> fixture.setDataValue(1, 0, null));
    assertThrows(HexatomicRuntimeException.class, () -> fixture.setDataValue(1, 0, ""));
    assertDoesNotThrow(() -> fixture.setDataValue(1, 0, "test"));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#getColumnCount()}.
   */
  @Test
  void testGetColumnCount() {
    assertEquals(2, fixture.getColumnCount());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#getRowCount()}.
   */
  @Test
  void testGetRowCount() {
    assertEquals(1, fixture.getRowCount());
  }

  /**
   * Test method for {@link ColumnHeaderDataProvider#renameColumnPosition(int, String)}.
   */
  @Test
  void testRenameColumnPosition() {
    assertThrows(NullPointerException.class, () -> fixture.renameColumnPosition(-1, null));
    assertThrows(NullPointerException.class, () -> fixture.renameColumnPosition(-1, ""));
    assertFalse(fixture.renameColumnPosition(1, "OLD"));
  }

}
