/*******************************************************************************
 * Copyright (c) 2011 Google, Inc. and others All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * <p>
 * Contributors: Google, Inc. - initial API and implementation <br />
 * Wim Jongman - 1.8 and higher compliance
 * </p>
 *******************************************************************************/

package org.eclipse.wb.swt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * Utility class for managing OS resources associated with SWT/JFace controls such as images.
 * 
 * <p>
 * This class is created automatically when you fiddle around with images and colors in WB. You
 * might want to prevent your application from using this class and provide your own more effective
 * means of resource caching.
 * </p>
 * 
 * <p>
 * Even though this class can be used to manage these resources, if they are here for the duration
 * of the application and not used then you still have an effective resource leak.
 * </p>
 * 
 * <p>
 * Application code must explicitly invoke the <code>dispose()</code> method to release the
 * operating system resources managed by cached objects when those objects and OS resources are no
 * longer needed.
 * </p>
 * 
 * <p>
 * This class may be freely distributed as part of any application or plugin.
 * </p>
 * 
 * @author scheglov_ke
 * @author Dan Rubel
 * @author Wim Jongman
 */
public class ResourceManager {

  private ResourceManager() {
    throw new IllegalStateException("Utility class that should not be instantiated");
  }



  private static void disposePluginImages() {
    for (Iterator<Image> imageIterator = urlImageMap.values().iterator(); imageIterator
        .hasNext();) {
      imageIterator.next().dispose();
    }
    urlImageMap.clear();
  }

  /**
   * Dispose all of the cached images.
   */
  public static void disposeImages() {
    disposePluginImages();
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Plugin images support
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Maps URL to images.
   */
  private static Map<String, Image> urlImageMap = new HashMap<>();

  /**
   * Provider for plugin resources, used by WindowBuilder at design time.
   */
  public interface PluginResourceProvider {
    URL getEntry(String symbolicName, String path);
  }

  /**
   * Instance of {@link PluginResourceProvider}, used by WindowBuilder at design time.
   */
  private static PluginResourceProvider designTimePluginResourceProvider = null;


  /**
   * Returns an {@link Image} based on a {@link Bundle} and resource entry path.
   * 
   * @param symbolicName the symbolic name of the {@link Bundle}.
   * @param path the path of the resource entry.
   * @return the {@link Image} stored in the file at the specified path.
   */
  public static Image getPluginImage(String symbolicName, String path) {
    try {
      URL url = getPluginImageUrl(symbolicName, path);
      if (url != null) {
        return getPluginImageFromUrl(url);
      }
    } catch (Exception e) {
      // Ignore any exceptions
    }
    return null;
  }

  /**
   * Returns an {@link Image} encoded by the specified {@link InputStream}.
   * 
   * @param stream the {@link InputStream} encoding the image data
   * @return the {@link Image} encoded by the specified input stream
   */
  protected static Image getImage(InputStream stream) throws IOException {
    try {
      Display display = Display.getCurrent();
      ImageData data = new ImageData(stream);
      if (data.transparentPixel > 0) {
        return new Image(display, data, data.getTransparencyMask());
      }
      return new Image(display, data);
    } finally {
      stream.close();
    }
  }


  /**
   * Returns an {@link Image} based on given {@link URL}.
   */
  private static Image getPluginImageFromUrl(URL url) {
    return urlImageMap.computeIfAbsent(url.toExternalForm(), key -> {
      try (InputStream stream = url.openStream()) {
        return getImage(stream);
      } catch (Exception ex) {
        // Ignore any exceptions
        return null;
      }
    });

  }


  /**
   * Returns an {@link URL} based on a {@link Bundle} and resource entry path.
   */
  private static URL getPluginImageUrl(String symbolicName, String path) {
    // try runtime plugins
    Bundle bundle = Platform.getBundle(symbolicName);
    if (bundle != null) {
      return bundle.getEntry(path);
    }

    // try design time provider
    if (designTimePluginResourceProvider != null) {
      return designTimePluginResourceProvider.getEntry(symbolicName, path);
    }
    // no such resource
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // General
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Dispose of cached objects and their underlying OS resources. This should only be called when
   * the cached objects are no longer needed (e.g. on application shutdown).
   */
  public static void dispose() {
    SwtResourceManager.dispose();
    disposeImages();
  }
}
