package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.corpus_tools.hexatomic.core.LinkOpener;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

class TestHelpMenu {
  private static final String USER_GUIDE_REGEX =
      "https://hexatomic.github.io/hexatomic/user/v[.0-9]+";
  private static final String ONLINE_DOCUMENTATION = "Online Documentation";
  private IEclipsePreferences prefs =
      ConfigurationScope.INSTANCE.getNode("org.corpus_tools.hexatomic.core");
  private SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private LinkOpener linkOpener;

  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();

    IEclipseContext ctx = TestHelper.getEclipseContext();

    // Replace the default link opener implementation with a mock
    linkOpener = mock(LinkOpener.class);
    ctx.set(LinkOpener.class, linkOpener);
  }


  @Test
  void testOnlineDocumentationLink() {
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu(ONLINE_DOCUMENTATION).isActive();
    helpMenu.menu(ONLINE_DOCUMENTATION).click();

    verify(linkOpener).open(matches(USER_GUIDE_REGEX));
    verifyNoMoreInteractions(linkOpener);
  }

  @Test
  @EnabledOnOs({OS.WINDOWS, OS.LINUX})
  void testOpenAboutDialog() {
    SWTBotMenu helpMenu = bot.menu("Help");
    helpMenu.menu("About").click();

    SWTBotShell aboutShell = bot.shell("About Hexatomic");
    aboutShell.bot().label("Hexatomic").isVisible();

    // Click on the links and check the link opener has been involved
    aboutShell.bot().link(0).click("https://hexatomic.github.io/");
    verify(linkOpener).open(eq("https://hexatomic.github.io/"));

    aboutShell.bot().link(1).click("Online User Documentation");
    verify(linkOpener).open(matches(USER_GUIDE_REGEX));

    aboutShell.bot().link(2).click("Hexatomic project team");
    verify(linkOpener).open(eq("https://github.com/orgs/hexatomic/teams/project/members"));

    aboutShell.bot().link(3).click("Create a bug report");
    verify(linkOpener).open(matches(
        "https://github.com/hexatomic/hexatomic/issues/new\\?assignees=\\&labels=bug\\&template=bug_report.md\\&title=Bug in version [.0-9]+"));

    aboutShell.bot().link(4).click("Apache License 2.0");
    verify(linkOpener).open(eq("https://spdx.org/licenses/Apache-2.0.html"));

    aboutShell.bot().link(5).click("CITATION.cff");
    verify(linkOpener)
        .open(matches("https://github.com/hexatomic/hexatomic/blob/v[.0-9]+/CITATION\\.cff"));
    verifyNoMoreInteractions(linkOpener);

    aboutShell.bot().link(6).click("THIRD-PARTY");
    verify(linkOpener)
        .open(matches("https://github.com/hexatomic/hexatomic/blob/v[.0-9]+/THIRD-PARTY"));
    verifyNoMoreInteractions(linkOpener);

    // Close the dialog and wait for it to actually close
    aboutShell.bot().button("Close").click();
    bot.waitUntil(Conditions.shellCloses(aboutShell));
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

    bot.waitUntil(Conditions.shellCloses(preferencesShell));

  }

}
