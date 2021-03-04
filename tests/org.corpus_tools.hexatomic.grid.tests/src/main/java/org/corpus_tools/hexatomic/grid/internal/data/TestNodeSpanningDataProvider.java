package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NodeSpanningDataProvider}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
class TestNodeSpanningDataProvider {

  private NodeSpanningDataProvider fixture = null;

  /**
   * Sets up the fixture.
   * 
   * @throws java.lang.Exception any exception thrown during set up.
   */
  @BeforeEach
  void setUp() throws IllegalArgumentException {
    fixture = new NodeSpanningDataProvider(mock(GraphDataProvider.class));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.NodeSpanningDataProvider#NodeSpanningDataProvider(org.eclipse.nebula.widgets.nattable.data.IDataProvider, boolean, boolean)}.
   */
  @Test
  void testNodeSpanningDataProvider() {
    assertDoesNotThrow(() -> new NodeSpanningDataProvider(mock(GraphDataProvider.class)));
    assertThrows(IllegalArgumentException.class, () -> new NodeSpanningDataProvider(null));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.NodeSpanningDataProvider#valuesNotEqual(java.lang.Object, java.lang.Object)}.
   */
  @Test
  void testValuesNotEqualObjectObject() {
    SSpan span = mock(SSpan.class);
    SToken token = mock(SToken.class);
    assertFalse(fixture.valuesNotEqual(span, span));
    assertFalse(fixture.valuesNotEqual(token, token));
    assertTrue(fixture.valuesNotEqual(span, token));
    assertTrue(fixture.valuesNotEqual(token, span));
    assertTrue(fixture.valuesNotEqual(span, token));
    assertTrue(fixture.valuesNotEqual(null, null));
  }

}
