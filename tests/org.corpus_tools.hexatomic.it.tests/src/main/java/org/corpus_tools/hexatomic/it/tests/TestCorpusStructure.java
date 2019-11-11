package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.fail;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.junit.jupiter.api.Test;

class TestCorpusStructure {
  
  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  @Test
  void testRenameDocument() {
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();

    fail("Not yet implemented");
  }

}
