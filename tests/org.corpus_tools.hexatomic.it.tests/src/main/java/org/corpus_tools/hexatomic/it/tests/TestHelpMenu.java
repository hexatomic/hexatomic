package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

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
  @EnabledOnOs({OS.WINDOWS, OS.LINUX})
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
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu("Preferences").click();
    SWTBotShell preferencesShell = bot.shell("Enable startup checks");
    assertTrue(preferencesShell.bot()
        .label("When checked, Hexatomic will automatically check for updates at each start.")
        .isVisible());
    boolean autoUpdatePreSelect = prefs.getBoolean("autoUpdate", true);
    if (preferencesShell.bot().checkBox().isChecked()) {
      preferencesShell.bot().checkBox().deselect();
    } else {
      preferencesShell.bot().checkBox().select();
    }
    preferencesShell.bot().button("OK").click();
    boolean autoUpdatePostSelect = prefs.getBoolean("autoUpdate", false);
    assertNotEquals(autoUpdatePreSelect, autoUpdatePostSelect);

  }

}
