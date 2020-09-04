/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LabelAccumulator}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestLabelAccumulator {

  private static LabelAccumulator fixture = null;
  private static GraphDataProvider fixtureProvider = null;
  private static LabelStack fixtureLabelStack = null;
  private static SpanningDataLayer fixtureLayer = null;

  /**
   * Sets up the fixture for unit tests.
   * 
   * @throws java.lang.Exception Any exception during setup
   */
  @BeforeEach
  void setUp() throws Exception {
    fixtureProvider = mock(GraphDataProvider.class);
    fixtureLabelStack = mock(LabelStack.class);
    fixtureLayer = mock(SpanningDataLayer.class);
    SToken token = mock(SToken.class);
    when(fixtureProvider.getDataValue(0, 0)).thenReturn(token);
    fixture = new LabelAccumulator(fixtureLayer, fixtureProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#LabelAccumulator(org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer, org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testLabelAccumulator() {
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, fixtureProvider));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(fixtureLayer, null));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, null));
    assertDoesNotThrow(() -> new LabelAccumulator(fixtureLayer, fixtureProvider));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#accumulateConfigLabels(org.eclipse.nebula.widgets.nattable.layer.LabelStack, int, int)}.
   */
  @Test
  void testAccumulateConfigLabelsLabelStackIntInt() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#getQNameForColumn(int)}.
   */
  @Test
  void testGetQNameForColumn() {
    assertEquals("Token", fixture.getQNameForColumn(0));
  }

}
