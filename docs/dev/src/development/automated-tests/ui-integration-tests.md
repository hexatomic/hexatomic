# Use interface integration tests

To add user interface integration tests add new test cases to the special bundle `org.corpus_tools.hexatomic.it.tests`.
The tests will be executed with SWTBot, for which you can find a general tutorial 
[here](https://www.vogella.com/tutorials/SWTBot/article.html).

SWTBot needs an instance of the class `org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot` to execute user interactions
automatically from the test case.
Make sure you use the version from the `org.eclipse.swtbot.e4.finder.widgets` package, which is the only one working
with Eclipse RCP 4.
Creating an instance of a `SWTWorkbenchBot` needs an Eclipse context, which can be acquired with the helper class 
`org.corpus_tools.hexatomic.it.tests.ContextHelper`.

```java
SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());
```

Otherwise, these tests are normal JUnit5 test cases and have the same annotations and structure.
E.g. a complete example, that tests if renaming a document in the corpus structure editor works, would look like this:

```java
package org.corpus_tools.hexatomic.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.jupiter.api.Test;

class TestCorpusStructure {
  
  private SWTWorkbenchBot bot = new SWTWorkbenchBot(ContextHelper.getEclipseContext());

  @Test
  void testRenameDocument() {
    // Make sure to activate the part to test before selecting SWT components
    bot.partById("org.corpus_tools.hexatomic.corpusedit.part.corpusstructure").show();
    
    // Add corpus graph 1 by clicking on the first toolbar button ("Add") in the corpus structure editor part
    bot.toolbarDropDownButton(0).click();
    // Add corpus 1
    bot.toolbarDropDownButton(0).click();
    // Add document_1
    bot.toolbarDropDownButton(0).click();
    // Select and edit the first document
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1").select();
    bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("document_1").doubleClick();
    bot.text("document_1").setText("abc").pressShortcut(Keystrokes.LF);
   
    // Make sure that the document has been renamed
    bot.tree().expandNode("corpus_graph_1").expandNode("corpus_1").expandNode("abc");
    assertNotNull(bot.tree().getTreeItem("corpus_graph_1").getNode("corpus_1").getNode("abc"));
  }
}

```
To test whether an action was successful, integration tests do not access the internal data model of the application.
Instead, they check the effects on the user interface (for example, the result of a renaming action is a new tree item with the 
new name).
To test internal states, use unit tests for the controllers of the bundle instead.

When your test case has more than one test, all tests are executed in the same environment.
Thus, previous executed tests will influence the next ones.
You can set the execution order a test explicitly: <https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order>.

If you add an integration to a new bundle, that was not tested before, you have to add the bundle manually to the 
dependencies of `org.corpus_tools.hexatomic.it.tests`.
Adding it to the feature or product is not enough.
