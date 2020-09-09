/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestColumnHeaderDataProvider {

  private static ColumnHeaderDataProvider fixture = null;
  private static GraphDataProvider fixtureProvider;
  private static List<Column> columns = null;

  /**
   * @throws java.lang.Exception
   */
  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() throws Exception {
    fixtureProvider = mock(GraphDataProvider.class);
    columns = mock(List.class);
    Column tokenColumn = mock(Column.class);
    Column spanColumn = mock(Column.class);
    when(tokenColumn.getHeader()).thenReturn("ZERO");
    when(columns.get(0)).thenReturn(tokenColumn);
    when(spanColumn.getHeader()).thenReturn("ONE");
    when(columns.get(1)).thenReturn(spanColumn);
    when(columns.size()).thenReturn(2);
    when(fixtureProvider.getColumns()).thenReturn(columns);
    when(fixtureProvider.getColumnCount()).thenReturn(2);
    when(fixtureProvider.getRowCount()).thenReturn(3);
    fixture = new ColumnHeaderDataProvider(fixtureProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#ColumnHeaderDataProvider(org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testColumnHeaderDataProvider() {
    assertDoesNotThrow(() -> new ColumnHeaderDataProvider(fixtureProvider));
    assertThrows(RuntimeException.class, () -> new ColumnHeaderDataProvider(null));
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
    assertDoesNotThrow(() -> fixture.setDataValue(-1, -1, null));
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
    when(columns.isEmpty()).thenReturn(true);
    assertEquals(0, fixture.getRowCount());
  }

}
