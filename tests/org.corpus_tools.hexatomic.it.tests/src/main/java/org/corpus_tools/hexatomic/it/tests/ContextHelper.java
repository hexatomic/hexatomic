package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ContextHelper {
  public static IEclipseContext getEclipseContext() {
    Bundle activatorBundle = FrameworkUtil.getBundle(Activator.class);
    final IEclipseContext serviceContext = EclipseContextFactory
        .getServiceContext(activatorBundle.getBundleContext());

    return serviceContext.get(IWorkbench.class).getApplication().getContext();
  }

}
