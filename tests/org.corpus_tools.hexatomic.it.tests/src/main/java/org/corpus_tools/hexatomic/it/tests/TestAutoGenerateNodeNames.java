package org.corpus_tools.hexatomic.it.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestAutoGenerateNodeNames {

  private final class RefactoringFinishedCondition extends DefaultCondition {
    @Override
    public boolean test() throws Exception {
      boolean hasMatchingShell = Arrays.asList(bot.shells()).stream()
          .anyMatch(s -> s.isOpen() && (SELECTED_DIALOG_CAPTION.equals(s.getText())
              || ALL_DOCUMENTS_LABEL.equals(s.getText())));
      return !hasMatchingShell && projectManager.canUndo();
    }

    @Override
    public String getFailureMessage() {
      return "Renaming operation did not finish.";
    }
  }

  private static final String SELECTED_MENU_LABEL =
      "Automatically generate node names for selection";
  private static final String DOC1_ID = "salt:/rootCorpus/subCorpus1/doc1";
  private static final String DOC2_ID = "salt:/rootCorpus/subCorpus1/doc2";
  private static final String SELECTED_DIALOG_CAPTION =
      "Automatically generate node names for selected documents";
  private static final String REFACTOR = "Refactor";
  private static final String EDIT = "Edit";
  private static final String ALL_DOCUMENTS_LABEL =
      "Automatically generate node names for all documents";
  private static final String CORPUS_EDITOR_PART_ID =
      "org.corpus_tools.hexatomic.corpusedit.part.corpusstructure";

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

  @AfterEach
  void close() {
    // Programmatically close the example corpus by opening a new fresh one
    TestHelper.executeNewProjectCommand(commandService, handlerService);
  }

  SWTBotView openDefaultExample() {
    // Programmatically open the example corpus
    Map<String, String> params = new HashMap<>();
    params.put(CommandParams.LOCATION, exampleProjectUri.toFileString());
    params.put(CommandParams.FORCE_CLOSE, "true");
    ParameterizedCommand cmd = commandService
        .createCommand("org.corpus_tools.hexatomic.core.command.open_salt_project", params);
    handlerService.executeHandler(cmd);

    // Activate corpus structure editor
    SWTBotView corpusStructurePart = bot.partById(CORPUS_EDITOR_PART_ID);
    corpusStructurePart.restore();
    corpusStructurePart.show();
    return corpusStructurePart;
  }

  private void assertNewNodeName(SDocumentGraph g, String documentId) {
    for (SNode n : g.getNodes()) {
      if (n instanceof SToken) {
        assertTrue(n.getName().matches("t[0-9]+"), "Token " + n.getName() + " in document "
            + documentId + " did not match pattern t<number>");
      } else if (n instanceof SStructuredNode) {
        assertTrue(n.getName().matches("n[0-9]+"), "Node " + n.getName() + "in document "
            + documentId + " did not match pattern n<number>");
      }
    }
  }

  private void assertOldNodeName(SDocumentGraph g, String documentId) {
    for (SNode n : g.getNodes()) {
      if (n instanceof SToken) {
        assertTrue(n.getName().matches("sTok[0-9]+"), "Token " + n.getName() + " in document "
            + documentId + " did not match pattern t<number>");
      } else if (n instanceof SStructuredNode) {
        assertEquals(false, n.getName().matches("n[0-9]+"),
            "Node " + n.getName() + "in document " + documentId + " did match pattern n<number>");
      }
    }
  }

  @Test
  void testReassignNamesForProject() throws InterruptedException {
    openDefaultExample();

    bot.waitUntil(new DefaultCondition() {

      @Override
      public boolean test() throws Exception {
        return bot.menu(EDIT).menu(REFACTOR).menu(ALL_DOCUMENTS_LABEL).isEnabled();
      }

      @Override
      public String getFailureMessage() {
        return "Automatically generate node name menu item was not enabled";
      }

    });

    // Apply action and check that the Salt project has changed
    bot.menu(EDIT).menu(REFACTOR).menu(ALL_DOCUMENTS_LABEL).click();
    SWTBotShell dialog = bot.shell(ALL_DOCUMENTS_LABEL);
    dialog.bot().button("Yes").click();

    // Wait for dialog to finish
    bot.waitUntil(new RefactoringFinishedCondition(), 5000, 100);

    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        Optional<SDocument> loadedDoc = projectManager.getDocument(documentReference.getId(), true);
        assertTrue(loadedDoc.isPresent());
        if (loadedDoc.isPresent()) {
          assertNewNodeName(loadedDoc.get().getDocumentGraph(), documentReference.getId());
        }
      }
    }

    // Undo the changes and check that the nodes names are correct
    assertTrue(projectManager.canUndo());
    projectManager.undo();
    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        Optional<SDocument> loadedDoc = projectManager.getDocument(documentReference.getId(), true);
        assertTrue(loadedDoc.isPresent());
        if (loadedDoc.isPresent()) {
          assertOldNodeName(loadedDoc.get().getDocumentGraph(), documentReference.getId());
        }
      }
    }
    assertFalse(projectManager.canUndo());
    assertTrue(projectManager.canRedo());
  }

  @Test
  void testReassignNamesForDocument() throws InterruptedException {
    SWTBotView corpusStructurePart = openDefaultExample();

    // Select the first example document
    SWTBotTreeItem doc1Item = corpusStructurePart.bot().tree().expandNode("corpusGraph1")
        .expandNode("rootCorpus").expandNode("subCorpus1").expandNode("doc1");

    // Click on the refactoring and auto gen menu items
    doc1Item.click();
    doc1Item.contextMenu("Refactor").menu(SELECTED_MENU_LABEL).click();

    SWTBotShell dialog = bot.shell(SELECTED_DIALOG_CAPTION);
    dialog.bot().button("Yes").click();

    // Wait for dialog to finish
    bot.waitUntil(new RefactoringFinishedCondition(), 5000, 100);

    // Check that the one document is changed, but others still have the original node names
    Optional<SDocument> doc1 = projectManager.getDocument(DOC1_ID, true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      assertNewNodeName(doc1.get().getDocumentGraph(), DOC1_ID);
    }

    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        if (!documentReference.getId().equals(DOC1_ID)) {
          Optional<SDocument> loadedDoc =
              projectManager.getDocument(documentReference.getId(), true);
          assertTrue(loadedDoc.isPresent());
          if (loadedDoc.isPresent()) {
            assertOldNodeName(loadedDoc.get().getDocumentGraph(), documentReference.getId());
          }
        }
      }
    }
  }

  @Test
  void testReassignNamesForSubcorpus() throws InterruptedException {

    SWTBotView corpusStructurePart = openDefaultExample();

    // Select the first example subcorpus
    SWTBotTreeItem corpusItem = corpusStructurePart.bot().tree().expandNode("corpusGraph1")
        .expandNode("rootCorpus").expandNode("subCorpus1");

    // Click on the refactoring and auto gen menu items
    corpusItem.click();
    corpusItem.contextMenu("Refactor").menu(SELECTED_MENU_LABEL).click();

    SWTBotShell dialog = bot.shell(SELECTED_DIALOG_CAPTION);
    dialog.bot().button("Yes").click();

    // Wait for dialog to finish
    bot.waitUntil(new RefactoringFinishedCondition(), 5000, 100);

    // Check that the all documents of the subcorpus are, but others still have the original node
    // names
    Optional<SDocument> doc1 = projectManager.getDocument(DOC1_ID, true);
    assertTrue(doc1.isPresent());
    if (doc1.isPresent()) {
      assertNewNodeName(doc1.get().getDocumentGraph(), DOC1_ID);
    }
    Optional<SDocument> doc2 = projectManager.getDocument(DOC2_ID, true);
    assertTrue(doc2.isPresent());
    if (doc2.isPresent()) {
      assertNewNodeName(doc2.get().getDocumentGraph(), DOC2_ID);
    }

    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        if (!documentReference.getId().equals(DOC1_ID)
            && !documentReference.getId().equals(DOC2_ID)) {
          Optional<SDocument> loadedDoc =
              projectManager.getDocument(documentReference.getId(), true);
          assertTrue(loadedDoc.isPresent());
          if (loadedDoc.isPresent()) {
            assertOldNodeName(loadedDoc.get().getDocumentGraph(), documentReference.getId());
          }
        }
      }
    }
  }

  @Test
  void testReassignNamesForCorpusGraph() throws InterruptedException {

    SWTBotView corpusStructurePart = openDefaultExample();

    // Select the corpus graph
    SWTBotTreeItem cgItem = corpusStructurePart.bot().tree().expandNode("corpusGraph1");

    // Click on the refactoring and auto gen menu items
    cgItem.click();
    cgItem.contextMenu("Refactor").menu(SELECTED_MENU_LABEL).click();

    SWTBotShell dialog = bot.shell(SELECTED_DIALOG_CAPTION);
    dialog.bot().button("Yes").click();

    // Wait for dialog to finish
    bot.waitUntil(new RefactoringFinishedCondition(), 5000, 100);

    // Refactoring the only corpus graph effectively should change all documents
    for (SCorpusGraph cg : projectManager.getProject().getCorpusGraphs()) {
      for (SDocument documentReference : cg.getDocuments()) {
        Optional<SDocument> loadedDoc = projectManager.getDocument(documentReference.getId(), true);
        assertTrue(loadedDoc.isPresent());
        if (loadedDoc.isPresent()) {
          assertNewNodeName(loadedDoc.get().getDocumentGraph(), documentReference.getId());
        }
      }
    }
  }
}
