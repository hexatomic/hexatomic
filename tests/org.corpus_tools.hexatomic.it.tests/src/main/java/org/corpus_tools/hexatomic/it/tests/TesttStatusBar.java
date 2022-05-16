package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.corpus_tools.hexatomic.core.Topics;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class TesttStatusBar {


  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());


  private IEventBroker events;

  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();
    IEclipseContext ctx = TestHelper.getEclipseContext();
    events = ctx.get(IEventBroker.class);;
    assertNotNull(events);
  }

  /**
   * Test that sending status events to the broker will update the label.
   */
  @Test
  void testCustomStatusMessage() throws IOException {
    events.send(Topics.TOOLBAR_STATUS_MESSAGE, "This is an example");
    bot.waitUntil(new DefaultCondition() {
      
      @Override
      public boolean test() throws Exception {
        SWTBotLabel lbl = bot.labelWithId("permanent-status-message");
        return "This is an example".equals(lbl.getText());
      }
      
      @Override
      public String getFailureMessage() {
        return "Permanent status message should have value \"This is an example\"";
      }
    });
  }
}
