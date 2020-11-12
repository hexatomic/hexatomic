package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.TestHelper;
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
    fixture =
        new ColumnHeaderDataProvider(TestHelper.createDataProvider(), mock(ProjectManager.class));
  }

  @Test
  void test() {

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
    assertEquals("Token", fixture.getDataValue(0, 0));
    assertEquals("salt::lemma", fixture.getDataValue(1, 0));
    assertEquals("salt::pos", fixture.getDataValue(2, 0));
    assertEquals("Inf-Struct", fixture.getDataValue(3, 0));
    assertNull(fixture.getDataValue(-1, 0));
    assertEquals("Token", fixture.getDataValue(0, 2));
    assertNull(fixture.getDataValue(-1, 10));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.ColumnHeaderDataProvider#setDataValue(int, int, java.lang.Object)}.
   */
  @Test
  void testSetDataValue() {
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
    assertEquals(4, fixture.getColumnCount());
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
    assertFalse(fixture.renameColumnPosition(1, "salt::lemma"));
    assertTrue(fixture.renameColumnPosition(1, "TEST"));
  }

}
