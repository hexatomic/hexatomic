package org.corpus_tools.hexatomic.it.tests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
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
@SuppressWarnings("restriction")
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
    Bundle testBundle = FrameworkUtil.getBundle(TestHelper.class);
    final IEclipseContext serviceContext =
        EclipseContextFactory.getServiceContext(testBundle.getBundleContext());

    return serviceContext.get(IWorkbench.class).getApplication().getContext();
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
      log.error("Could not get environment variable " + SWTBOT_KEYBOARD_LAYOUT
          + " because the security mananger is running and disallowed access", ex);
    }
  }

  /**
   * Programmatically start a new salt project to get a clean state. This uses the Eclipse commands
   * instead of the project manager directly.
   * 
   * @param commandService The service used to create Eclipse RCP commands.
   * @param handlerService The service used to execute command handlers.
   */
  public static void executeNewProjectCommand(ECommandService commandService,
      EHandlerService handlerService) {
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.new_salt_project", params);
    handlerService.executeHandler(cmd);
  }

  /**
   * Recursively delete a directory.
   * 
   * @param directory The directory {@link Path} to delete recursively
   * @return If the directory exists
   * @throws IOException An error that occurred when reading or deleting from the file system
   */
  public static boolean deleteDirectory(Path directory) throws IOException {
    Path startPath = Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
    return Files.notExists(startPath);
  }

}
