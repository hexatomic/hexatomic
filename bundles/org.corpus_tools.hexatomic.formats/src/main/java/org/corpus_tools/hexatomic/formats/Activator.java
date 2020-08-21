package org.corpus_tools.hexatomic.formats;

import java.util.Optional;
import org.corpus_tools.pepper.common.Pepper;
import org.corpus_tools.pepper.common.PepperConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Activator.class);

  private static Optional<Pepper> pepper = Optional.empty();

  @Override
  public void start(BundleContext context) throws Exception {

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
            pepper = Optional.ofNullable(context.getService(pepperRef));
          }
        } else {
          log.error("Could not start pepper bundle, status is {}", state);
        }
      }
    }

    // Add and start all required pepper module bundles
    // TODO: bundle the JAR files with Hexatomic
    System.getProperties().setProperty(PepperConfiguration.PROP_PEPPER_MODULE_RESOURCES,
        "/home/thomas/src/pepper/pepper-lib/target/distribution/pepper-lib-3.4.2-SNAPSHOT/org.corpus-tools.pepper-lib_3.4.2-SNAPSHOT/plugins/");
    String[] bundleLocationsToLoad = {
        "file:/home/thomas/src/pepper/pepper-lib/target/distribution/pepper-lib-3.4.2-SNAPSHOT/org.corpus-tools.pepper-lib_3.4.2-SNAPSHOT/plugins/exmaralda-emf-api-1.2.1.jar   ",
        "file:/home/thomas/src/pepper/pepper-lib/target/distribution/pepper-lib-3.4.2-SNAPSHOT/org.corpus-tools.pepper-lib_3.4.2-SNAPSHOT/plugins/pepperModules-EXMARaLDAModules-1.3.0.jar"};
    for (String bundleLocation : bundleLocationsToLoad) {
      Bundle b = context.installBundle(bundleLocation);
      b.start();
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    pepper = Optional.empty();
  }

  public static Optional<Pepper> getPepper() {
    return pepper;
  }

}
