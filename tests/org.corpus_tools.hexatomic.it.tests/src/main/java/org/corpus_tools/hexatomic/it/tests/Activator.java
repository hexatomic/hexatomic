package org.corpus_tools.hexatomic.it.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * An activator for the integration test bundle.
 * @author Thomas Krause
 *
 */
public class Activator implements BundleActivator {

  static BundleContext ctx;


  @Override
  public void start(BundleContext context) throws Exception {
    Activator.ctx = context;

  }

  @Override
  public void stop(BundleContext context) throws Exception {
    Activator.ctx = null;

  }

}
