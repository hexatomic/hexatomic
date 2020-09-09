package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link RowHeaderDataProvider}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestRowHeaderDataProvider {

  private static RowHeaderDataProvider fixture = null;
  private static GraphDataProvider fixtureProvider = null;
  private static List<Column> columns = null;

  /**
   * Sets up the fixture.
   * 
   * @throws java.lang.Exception Any exception thrown during execution.
   */
  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() throws Exception {
    fixtureProvider = mock(GraphDataProvider.class);
    when(fixtureProvider.getRowCount()).thenReturn(1);
    columns = mock(List.class);
    when(columns.size()).thenReturn(1);
    when(fixtureProvider.getColumns()).thenReturn(columns);
    fixture = new RowHeaderDataProvider(fixtureProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider#RowHeaderDataProvider(org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testRowHeaderDataProvider() {
    assertDoesNotThrow(() -> new RowHeaderDataProvider(fixtureProvider));
    assertThrows(RuntimeException.class, () -> new RowHeaderDataProvider(null));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider#getDataValue(int, int)}.
   */
  @Test
  void testGetDataValue() {
    assertEquals(0, fixture.getDataValue(-1, -1));
    assertEquals(1, fixture.getDataValue(-1, 0));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider#setDataValue(int, int, java.lang.Object)}.
   */
  @Test
  void testSetDataValue() {
    assertDoesNotThrow(() -> fixture.setDataValue(-1, -1, null));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider#getColumnCount()}.
   */
  @Test
  void testGetColumnCount() {
    assertEquals(1, fixture.getColumnCount());
    when(columns.isEmpty()).thenReturn(true);
    assertEquals(0, fixture.getColumnCount());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.RowHeaderDataProvider#getRowCount()}.
   */
  @Test
  void testGetRowCount() {
    assertEquals(1, fixture.getRowCount());
  }

}
