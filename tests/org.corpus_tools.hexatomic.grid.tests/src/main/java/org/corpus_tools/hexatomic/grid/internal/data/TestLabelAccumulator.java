package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.corpus_tools.hexatomic.grid.internal.test.TestHelper;
import org.corpus_tools.hexatomic.grid.style.StyleConfiguration;
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

  private LabelAccumulator fixture = null;
  private GraphDataProvider dataProvider = null;
  private SpanningDataLayer fixtureLayer = null;

  /**
   * Sets up the fixture for unit tests.
   */
  @BeforeEach
  void setUp() {
    dataProvider = TestHelper.createOverlappingDataProvider();
    NodeSpanningDataProvider fixtureProvider = new NodeSpanningDataProvider(dataProvider);
    fixtureLayer = new SpanningDataLayer(fixtureProvider);
    fixture = new LabelAccumulator(fixtureLayer, dataProvider);
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#LabelAccumulator(org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer, org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider)}.
   */
  @Test
  void testLabelAccumulator() {
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, dataProvider));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(fixtureLayer, null));
    assertThrows(IllegalArgumentException.class, () -> new LabelAccumulator(null, null));
    assertDoesNotThrow(() -> new LabelAccumulator(fixtureLayer, dataProvider));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.LabelAccumulator#accumulateConfigLabels(org.eclipse.nebula.widgets.nattable.layer.LabelStack, int, int)}.
   */
  @Test
  void testAccumulateConfigLabelsLabelStackIntInt() {
    // Test exception with non-existent cell
    RuntimeException thrownException =
        assertThrows(RuntimeException.class, () -> fixture.accumulateConfigLabels(null, -1, -1));
    assertEquals("There is no cell at column position -1, row position -1.",
        thrownException.getMessage());

    // Test empty cell
    final LabelStack emptyStack = new LabelStack("");
    final LabelStack tokenStack = new LabelStack("");
    final LabelStack spanStack = new LabelStack("");
    fixture.accumulateConfigLabels(emptyStack, 1, 1);
    assertTrue(emptyStack.getLabels().contains(StyleConfiguration.EMPTY_CELL_STYLE));
    // // Also has empty string
    assertEquals(2, emptyStack.getLabels().size());
    fixture.accumulateConfigLabels(tokenStack, 0, 0);
    assertTrue(tokenStack.getLabels().contains(StyleConfiguration.TOKEN_TEXT_CELL_STYLE));
    // Also has empty string
    assertEquals(2, tokenStack.getLabels().size());
    fixture.accumulateConfigLabels(spanStack, 3, 0);
    assertTrue(spanStack.getLabels().contains(StyleConfiguration.SPAN_ANNOTATION_CELL_STYLE));
    assertEquals(2, spanStack.getLabels().size());
  }

}
