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

  private static final String NAMESPACE = "namespace";
  private static final String NAMESPACE_NAME = "namespace::name";
  private static final String NAME = "name";
  private static final String NAMESPACE_SEPARATOR = "namespace::";

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#splitNamespaceFromQNameString(java.lang.String)}.
   */
  @Test
  void testSplitNamespaceFromQNameString() {
    assertEquals(NAMESPACE, DataUtil.splitNamespaceFromQNameString(NAMESPACE_NAME));
    assertNull(DataUtil.splitNamespaceFromQNameString(null));
    assertNull(DataUtil.splitNamespaceFromQNameString("namespace::name::something_else"));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#splitNameFromQNameString(java.lang.String)}.
   */
  @Test
  void testSplitNameFromQNameString() {
    assertEquals(NAME, DataUtil.splitNameFromQNameString(NAMESPACE_NAME));
    assertEquals(NAME, DataUtil.splitNameFromQNameString(NAME));
    assertNull(DataUtil.splitNameFromQNameString(NAMESPACE_SEPARATOR));
    assertNull(DataUtil.splitNameFromQNameString(null));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#isValidQName(java.lang.String)}.
   */
  @Test
  void testIsValidQName() {
    assertTrue(DataUtil.isValidQName(NAMESPACE_NAME));
    assertFalse(DataUtil.isValidQName("namespace :: name"));
    assertFalse(DataUtil.isValidQName("namespace::name::something_else"));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.DataUtil#buildQName(java.lang.String, java.lang.String)}.
   */
  @Test
  void testBuildQName() {
    assertEquals(NAMESPACE_NAME, DataUtil.buildQName(NAMESPACE, NAME));
    assertEquals(NAMESPACE_SEPARATOR, DataUtil.buildQName(NAMESPACE, null));
    assertEquals(NAMESPACE_SEPARATOR, DataUtil.buildQName(NAMESPACE, ""));
    assertEquals(NAME, DataUtil.buildQName(null, NAME));
    assertEquals(NAME, DataUtil.buildQName("", NAME));
    assertNull(DataUtil.buildQName(null, null));
  }

}
