package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides static helper methods.
 * 
 * @author Thomas Krause
 * @author Stephan Druskat
 *
 */
public class TestHelper {

  /**
   * Get the Eclipse context for testing to create {@link SWTWorkbenchBot} instances.
   * 
   * @return The context.
   */
  public static IEclipseContext getEclipseContext() {
    Bundle activatorBundle = FrameworkUtil.getBundle(Activator.class);
    final IEclipseContext serviceContext =
        EclipseContextFactory.getServiceContext(activatorBundle.getBundleContext());

    return serviceContext.get(IWorkbench.class).getApplication().getContext();
  }

  /**
   * Sets the SWTBot keyboard layout to <code>EN_US</code>.
   */
  public static void setKeyboardLayout() {
    org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
  }

}
