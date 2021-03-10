package org.corpus_tools.hexatomic.it.tests;

import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHelpMenu {

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
    helpMenu.menu("Update").click();
    SWTBotShell aboutShell = bot.shell("About Hexatomic");
  }
}
