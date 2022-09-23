package org.corpus_tools.hexatomic.it.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class TestReassignNodeInNames {


  private static final String REFACTOR = "Refactor";

  private static final String EDIT = "Edit";

  private static final String ALL_DOCUMENTS_LABEL = "Re-assign node names for all documents";

  private final SWTWorkbenchBot bot = new SWTWorkbenchBot(TestHelper.getEclipseContext());

  private URI exampleProjectUri;
  private ECommandService commandService;
  private EHandlerService handlerService;

  private IEventBroker events;
  private ProjectManager projectManager;

  @BeforeEach
  void setup() {
    TestHelper.setKeyboardLayout();
    IEclipseContext ctx = TestHelper.getEclipseContext();

    projectManager = ContextInjectionFactory.make(ProjectManager.class, ctx);

    events = ctx.get(IEventBroker.class);
    assertNotNull(events);

    commandService = ctx.get(ECommandService.class);
    assertNotNull(commandService);

    handlerService = ctx.get(EHandlerService.class);
    assertNotNull(handlerService);

    File exampleProjectDirectory = new File("../org.corpus_tools.hexatomic.core.tests/"
        + "src/main/resources/org/corpus_tools/hexatomic/core/example-corpus/");
    assertTrue(exampleProjectDirectory.isDirectory());
    exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
  }

  void openDefaultExample() {
    // Programmatically open the example corpus
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, exampleProjectUri.toFileString());
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.open_salt_project", params);
    handlerService.executeHandler(cmd);
  }


  @Test
  void testReassignNamesForProject() throws InterruptedException {


    // The menu item should be disabled when no project is loaded

    bot.menu(EDIT).click();
    bot.menu(EDIT).menu(REFACTOR).click();
    assertFalse(bot.menu(EDIT).menu(REFACTOR).menu(ALL_DOCUMENTS_LABEL).isEnabled());

    openDefaultExample();

    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return bot.menu(EDIT).menu(REFACTOR).menu(ALL_DOCUMENTS_LABEL).isEnabled();
      }

      @Override
      public String getFailureMessage() {
        return "Re-assign node name menu item was not enabled";
      }

    });

    // Apply action and check that the Salt project has changed
    bot.menu(EDIT).menu(REFACTOR).menu(ALL_DOCUMENTS_LABEL).click();
    SWTBotShell dialog = bot.shell(ALL_DOCUMENTS_LABEL);
    dialog.bot().button("Yes").click();

    // Wait for dialog to finish
    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        boolean hasMatchingShell = Arrays.asList(bot.shells()).stream()
            .anyMatch(s -> s.isOpen() && ALL_DOCUMENTS_LABEL.equals(s.getText()));
        return !hasMatchingShell && projectManager.canUndo();
      }

      @Override
      public String getFailureMessage() {
        return "Renaming operation did not finish.";
      }
    }, 5000, 100);

    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        Optional<SDocument> loadedDoc =
            projectManager.getDocument(documentReference.getId(), true);
        assertTrue(loadedDoc.isPresent());
        if (loadedDoc.isPresent()) {
          SDocumentGraph g = loadedDoc.get().getDocumentGraph();

          for (SNode n : g.getNodes()) {
            if (n instanceof SToken) {
              assertTrue(n.getName().matches("t[0-9]+"), "Token " + n.getName() + " in document "
                  + documentReference.getId() + " did not match pattern t<number>");
            } else if (n instanceof SStructuredNode) {
              assertTrue(n.getName().matches("n[0-9]+"), "Node " + n.getName() + "in document "
                  + documentReference.getId() + " did not match pattern n<number>");
            }
          }
        }
      }
    }
  }
}
