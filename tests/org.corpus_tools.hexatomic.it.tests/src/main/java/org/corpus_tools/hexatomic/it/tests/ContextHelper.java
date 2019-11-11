package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * A helper class to get an Eclipse context in test to create {@link SWTWorkbenchBot} instances.
 * @author Thomas Krause
 *
 */
public class ContextHelper {
  
  /**
   * Get the Eclipse context for testing.
   * @return The context.
   */
  public static IEclipseContext getEclipseContext() {
    Bundle activatorBundle = FrameworkUtil.getBundle(Activator.class);
    final IEclipseContext serviceContext = EclipseContextFactory
        .getServiceContext(activatorBundle.getBundleContext());

    return serviceContext.get(IWorkbench.class).getApplication().getContext();
  }

}
