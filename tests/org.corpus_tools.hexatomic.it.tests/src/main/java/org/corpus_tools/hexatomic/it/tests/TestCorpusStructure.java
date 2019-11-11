package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.jupiter.api.Test;

class TestCorpusStructure {
  
  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  @Test
  void testRenameDocument() {
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();
    
    // corpus graph 1
    bot.toolbarDropDownButton(0).click();
    // corpus 1
    bot.toolbarDropDownButton(0).click();
    // document_1
    bot.toolbarDropDownButton(0).click();
    // document_2
    bot.toolbarDropDownButton(0).click();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1").select();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1").doubleClick();
    bot.text("document_1").setText("abc").pressShortcut(Keystrokes.LF);
   
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("document_2");
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_2").select();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_2").doubleClick();
    bot.text("document_2").setText("def").pressShortcut(Keystrokes.LF);
   
    // make sure that the salt project has been renamed
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("abc");
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("def");
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("abc"));
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("def"));
  }

}
