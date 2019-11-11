package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCorpusStructureEditor {
  
  private static SWTWorkbenchBot wbot;
  
  @BeforeAll
  public static void beforeClass() {
    
    IEclipseContext ctx = ContextHelper.getEclipseContext();
    wbot = new SWTWorkbenchBot(ctx);
  }

  @BeforeEach
  void setUp() throws Exception {
    IEclipseContext ctx = ContextHelper.getEclipseContext();
    wbot = new SWTWorkbenchBot(ctx);
  }

  @Test
  void test() {
    assertNotNull(wbot);
    fail("Not yet implemented");
  }

}
