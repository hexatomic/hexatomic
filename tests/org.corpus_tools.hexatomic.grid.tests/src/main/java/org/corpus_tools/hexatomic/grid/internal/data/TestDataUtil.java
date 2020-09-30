/**
 * 
 */
package org.corpus_tools.hexatomic.grid.internal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DataUtil}.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
class TestDataUtil {

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#splitNamespaceFromQNameString(java.lang.String)}.
   */
  @Test
  void testSplitNamespaceFromQNameString() {
    assertEquals("namespace", DataUtil.splitNamespaceFromQNameString("namespace::name"));
    assertNull(DataUtil.splitNamespaceFromQNameString(null));
    assertNull(DataUtil.splitNamespaceFromQNameString("namespace::name::something_else"));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#splitNameFromQNameString(java.lang.String)}.
   */
  @Test
  void testSplitNameFromQNameString() {
    assertEquals("name", DataUtil.splitNameFromQNameString("namespace::name"));
    assertEquals("name", DataUtil.splitNameFromQNameString("name"));
    assertNull(DataUtil.splitNameFromQNameString("namespace::"));
    assertNull(DataUtil.splitNameFromQNameString(null));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#isValidQName(java.lang.String)}.
   */
  @Test
  void testIsValidQName() {
    assertTrue(DataUtil.isValidQName("namespace::name"));
    assertFalse(DataUtil.isValidQName("namespace :: name"));
    assertFalse(DataUtil.isValidQName("namespace::name::something_else"));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#buildQName(java.lang.String, java.lang.String)}.
   */
  @Test
  void testBuildQName() {
    assertEquals("namespace::name", DataUtil.buildQName("namespace", "name"));
    assertEquals("namespace::", DataUtil.buildQName("namespace", null));
    assertEquals("namespace::", DataUtil.buildQName("namespace", ""));
    assertEquals("name", DataUtil.buildQName(null, "name"));
    assertEquals("name", DataUtil.buildQName("", "name"));
    assertNull(DataUtil.buildQName(null, null));
  }

}
