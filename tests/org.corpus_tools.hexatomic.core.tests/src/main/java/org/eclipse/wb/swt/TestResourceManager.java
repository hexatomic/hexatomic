package org.eclipse.wb.swt;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.swt.graphics.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestResourceManager {

  @BeforeEach
  void setUp() {}

  /**
   * Tests correct caching and disposal of images.
   */
  @Test
  public void testCacheAndDisposeImages() {
    assertEquals(0, ResourceManager.urlImageMap.size());

    // Add first image
    Image img1 = ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
        "icons/fontawesome/plus-solid.png");
    assertNotNull(img1);
    assertEquals(1, ResourceManager.urlImageMap.size());

    // Add second image
    Image img2 = ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
        "icons/fontawesome/folder-regular.png");
    assertNotNull(img2);
    assertEquals(2, ResourceManager.urlImageMap.size());

    // Add image again, this should not change the cache size
    Image cachedImg = ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
        "icons/fontawesome/plus-solid.png");
    assertEquals(img1, cachedImg);
    assertEquals(2, ResourceManager.urlImageMap.size());

    // Dispose image resources and check that the map is empty
    ResourceManager.dispose();
    assertEquals(0, ResourceManager.urlImageMap.size());
  }

}
