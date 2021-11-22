package org.corpus_tools.hexatomic.it.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHelpMenu {
  private IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();
  }



  @Test
  void testOnlineDocumentationExists() {
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu("Online Documentation").isActive();
  }

  @Test
  void testOpenAboutDialog() {
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu("About").click();

    SWTBotShell aboutShell = bot.shell("About Hexatomic");
    aboutShell.bot().label("Hexatomic").isVisible();
    aboutShell.bot().button("OK").click();
  }
  
  @Test
  void testOpenUpdateConfiguration() {
    SWTBotMenu helpMenu = bot.menu("Help");
    assertNotNull(helpMenu.menu("Update"));
  }
  
  @Test
  void testOpenPreferenceDialog() {
    boolean autoupdate = prefs.getBoolean("autoUpdate", false);
    assertFalse("Preference allready set", autoupdate);
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu("Preferences").click();
    SWTBotShell preferencesShell = bot.shell("Enable Startup-Checks");
    preferencesShell.bot().label(
      "When checked Hexatomic will search for p2-Updates at each startup")
      .isVisible();
    preferencesShell.bot().checkBox().select();
    preferencesShell.bot().button("OK").click();
    autoupdate = prefs.getBoolean("autoUpdate", false);
    assertTrue("Preference not set", autoupdate);
  }  
  
}
