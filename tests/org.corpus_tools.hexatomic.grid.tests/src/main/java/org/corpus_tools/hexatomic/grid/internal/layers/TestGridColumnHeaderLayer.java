package org.corpus_tools.hexatomic.grid.internal.layers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GridColumnHeaderLayer}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestGridColumnHeaderLayer {

  private GridColumnHeaderLayer fixture = null;
  // Mockito.spy(new GridColumnHeaderLayer(mock(IUniqueIndexLayer.class), mock(ILayer.class),
  // mock(SelectionLayer.class)));

  /**
   * Sets up the fixture.
   *
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new GridColumnHeaderLayer(mock(IUniqueIndexLayer.class), mock(ILayer.class),
        mock(SelectionLayer.class));
  }

  /**
   * Test method for {@link org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer#getDataValueByPosition(int, int)}.
   */
  @Test
  void testThrowsOnGetDataValueByPosition() {
    when(fixture.getDataValueByPosition(1, 0)).thenReturn(1L);
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> fixture.getAnnotationQName(1));
    assertEquals(
        "Expected column header data value at column position 1 to be a string, but got Long",
        e.getMessage());
  }

  /**
   * Test method for {@link org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer#getDataValueByPosition(int, int)}.
   */
  @Test
  void testGetAnnotationQNameWithNumber() {
    when(fixture.getDataValueByPosition(1, 0)).thenReturn("TEST (2)");
    assertEquals("TEST", fixture.getAnnotationQName(1));
  }

  /**
   * Test method for
   * {@link org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer#getDataValueByPosition(int, int)}.
   */
  @Test
  void testGetAnnotationQName() {
    when(fixture.getDataValueByPosition(1, 0)).thenReturn("TEST");
    assertEquals("TEST", fixture.getAnnotationQName(1));
  }

}
