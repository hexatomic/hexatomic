/*-
 * #%L
 * org.corpus_tools.hexatomic.formats
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.formats;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.core.PepperOSGiRunner;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Activator.class);

  private static final String[] BUNDLE_LOCATIONS = {"exmaralda-emf-api-1.2.2.jar", "gson-2.8.7.jar",
      "pepperModules-EXMARaLDAModules-1.3.1.jar",
      "pepperModules-GraphAnnoModules-2.0.1.jar",
      "pepperModules-PAULAModules-1.3.0.jar"};

  private static Optional<Pepper> pepper = Optional.empty();

  @Override
  public void start(BundleContext context) throws Exception {

    Bundle thisBundle = context.getBundle();

    File pluginsFolder =
        new File(FileLocator.toFileURL(thisBundle.getResource("pepper-plugins")).getFile());

    System.setProperty(PepperConfiguration.PROP_PEPPER_MODULE_RESOURCES,
        pluginsFolder.getAbsolutePath());
    System.setProperty(PepperOSGiRunner.PROP_TEST_DISABLED, Boolean.TRUE.toString());

    log.info("Using {} as Pepper module resource folder", pluginsFolder.getAbsolutePath());

    Bundle[] bundles = context.getBundles();
    for (Bundle b : bundles) {
      if (b.getSymbolicName().startsWith("org.corpus-tools.pepper-framework")) {
        // Start the pepper-framework bundle and get a reference to it
        if (b.getState() != Bundle.ACTIVE) {
          b.start();
        }
        int state = b.getState();
        if (state == Bundle.ACTIVE) {
          ServiceReference<Pepper> pepperRef = context.getServiceReference(Pepper.class);
          if (pepperRef != null) {
            setPepper(Optional.ofNullable(context.getService(pepperRef)));
          }
        } else {
          log.error("Could not start pepper bundle, status is {}", state);
        }
      }
    }

    // Add and start all required pepper module bundles in the correct order
    for (String location : BUNDLE_LOCATIONS) {
      URL resourceUrl = thisBundle.getResource("pepper-plugins/" + location);
      URL fileUrl = FileLocator.toFileURL(resourceUrl);
      log.info("Installing Pepper module from {}", fileUrl);
      Bundle b = context.installBundle(fileUrl.toExternalForm());
      b.start();
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    setPepper(Optional.empty());
  }

  public static synchronized Optional<Pepper> getPepper() {
    return pepper;
  }

  private static synchronized void setPepper(Optional<Pepper> newPepper) {
    pepper = newPepper;
  }

}
