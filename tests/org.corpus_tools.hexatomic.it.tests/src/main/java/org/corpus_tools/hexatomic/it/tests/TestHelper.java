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

  private static final String SWTBOT_KEYBOARD_LAYOUT = "SWTBOT_KEYBOARD_LAYOUT";
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestHelper.class);


  private TestHelper() {
    // Hide default constructor
  }

  /**
   * Get the Eclipse context for testing to create {@link SWTWorkbenchBot} instances.
   * 
   * @return The context.
   */
  public static IEclipseContext getEclipseContext() {
    Bundle activatorBundle = FrameworkUtil.getBundle(Activator.class);
    final IEclipseContext serviceContext =
        EclipseContextFactory.getServiceContext(activatorBundle.getBundleContext());
    IEclipseContext applicationContext =
        serviceContext.get(IWorkbench.class).getApplication().getContext();
    // Make sure the context is active which amongst other things also means the window is
    // activated. For some environments like a virtual XVfb server, this is not always given
    // automatically.
    applicationContext.activate();
    return applicationContext;
  }

  /**
   * Checks if the {{@link #SWTBOT_KEYBOARD_LAYOUT} environment variable is set and updates the
   * keyboard layout accordingly.
   */
  public static void setKeyboardLayout() {
    try {
      String forcedKeyboardLayout = System.getenv(SWTBOT_KEYBOARD_LAYOUT);
      if (forcedKeyboardLayout != null) {
        org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences.KEYBOARD_LAYOUT =
            forcedKeyboardLayout;
      }
    } catch (SecurityException ex) {
      log.error(
          "Could not get environment variable " + SWTBOT_KEYBOARD_LAYOUT
              + " because the security mananger is running and disallowed access",
          ex);
    }
  }

}
