package org.corpus_tools.hexatomic.it.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  static BundleContext ctx;

  static BundleContext getContext() {
    return ctx;
  }


  @Override
  public void start(BundleContext context) throws Exception {
    Activator.ctx = context;

  }

  @Override
  public void stop(BundleContext context) throws Exception {
    Activator.ctx = null;

  }

}
