/*-
 * #%L
 * [bundle] Hexatomic Core Plugin
 * %%
 * Copyright (C) 2018 - 2022 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.core.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusDocumentRelation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SCorpusRelation;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class ReassignNodeNamesHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;



  private final class DocumentForCorpusTraverser implements GraphTraverseHandler {

    private final Set<String> childDocumentIds = new TreeSet<String>();

    @Override
    public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
        SRelation<SNode, SNode> relation, SNode fromNode, long order) {
      // If the target node is a document, add it to our set
      if (currNode instanceof SDocument) {
        childDocumentIds.add(currNode.getId());
      }
    }

    @Override
    public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
        SRelation<SNode, SNode> relation, SNode fromNode, long order) {
      // We already add the documents when we reach the nodes.
    }

    @Override
    public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
        @SuppressWarnings("rawtypes") SRelation relation, SNode currNode, long order) {
      // Traverse to sub-corpora and documents
      return relation == null || relation instanceof SCorpusDocumentRelation
          || relation instanceof SCorpusRelation;
    }
  }


  protected class NameAssigner implements IRunnableWithProgress {

    private Set<String> documentIds;

    public NameAssigner(Set<String> documentIds) {
      this.documentIds = documentIds;
    }

    @Override
    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException {
      monitor.beginTask("Re-assign node names", this.documentIds.size());

      for (String docId : documentIds) {
        if (monitor.isCanceled()) {
          monitor.subTask("Cancelling");
          projectManager.revertToLastCheckpoint();
          return;
        }
        monitor.subTask(docId);

        java.util.Optional<SDocument> document = projectManager.getDocument(docId, true);
        if (document.isPresent()) {
          SDocumentGraph graph = document.get().getDocumentGraph();
          int tokenCounter = 1;
          for (SToken tok : graph.getSortedTokenByText()) {
            tok.setName("t" + tokenCounter++);
          }
          int nodeCounter = 1;
          for (SStructure struct : graph.getStructures()) {
            struct.setName("n" + nodeCounter++);
          }
          for (SSpan span : graph.getSpans()) {
            span.setName("n" + nodeCounter++);
          }
        }

        monitor.worked(1);
      }

      monitor.subTask("Adding undo checkpoint");
      projectManager.addCheckpoint();

      monitor.done();

    }


  }

  private Set<String> getSelectedDocumentIds(Object selection, boolean forceAll) {
    Set<String> selectedDocuments;
    if (selection == null || forceAll) {
      // Use all documents of the project
      selectedDocuments = projectManager.getProject().getCorpusGraphs().stream()
          .flatMap(cg -> cg.getDocuments().stream()).map(SDocument::getId)
          .collect(Collectors.toSet());
    } else {
      // The objects could be (sub-) corpora or documents. For the (sub-) corpora get all the
      // documents and merge all documents into one set
      selectedDocuments = new HashSet<>();
      if (selection instanceof SDocument) {
        selectedDocuments.add(((SDocument) selection).getId());
      } else if (selection instanceof SCorpus) {
        SCorpus selectedCorpus = (SCorpus) selection;
        DocumentForCorpusTraverser traverser = new DocumentForCorpusTraverser();
        selectedCorpus.getGraph().traverse(Arrays.asList(selectedCorpus),
            GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "GetDocumentsForGraph", traverser);
        selectedDocuments.addAll(traverser.childDocumentIds);
      } else if (selection instanceof SCorpusGraph) {
        SCorpusGraph cg = (SCorpusGraph) selection;
        selectedDocuments
            .addAll(cg.getDocuments().stream().map(SDocument::getId).collect(Collectors.toList()));
      }
    }
    return selectedDocuments;
  }

  @Execute
  protected void execute(Shell shell, ESelectionService selectionService,
      @Optional @Named(CommandParams.FORCE_ALL) String forceAllRaw) {

    final Object selection = selectionService.getSelection();
    final boolean forceAll = forceAllRaw != null && forceAllRaw.equalsIgnoreCase("true");

    // Get all documents to apply this handler to
    Set<String> selectedDocuments = getSelectedDocumentIds(selection, forceAll);

    String messageTitle;
    String appliedToMessage;
    if (forceAll || selection == null) {
      messageTitle = "Re-assign node names for all documents";
      appliedToMessage = "This will be applied to all documents in all opened corpora.";
    } else {
      messageTitle = "Re-assign node names for selected documents";
      if (selectedDocuments.size() == 1) {
        appliedToMessage =
            "This will be applied to the document \"" + selectedDocuments.iterator().next() + "\".";
      } else {
        appliedToMessage = "This will be applied to " + selectedDocuments.size() + " documents.";
      }
    }

    boolean performReassign = MessageDialog.openQuestion(shell, messageTitle,
        "Do you want to assign automatically generated names to all nodes and tokens? "
            + "Tokens will get the prefix \"t\" and a number (e.g. \"t1\") and "
            + "other nodes the prefix \"n\" and a number (e.g. \"n2\").\n\n" + appliedToMessage);

    if (performReassign) {
      NameAssigner operation = new NameAssigner(selectedDocuments);

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

      dialog.setCancelable(true);
      try {
        dialog.run(true, true, operation);
      } catch (InvocationTargetException ex) {
        errorService.handleException("Could not re-assign node names", ex,
            ReassignNodeNamesHandler.class);
      } catch (InterruptedException ex) {
        errorService.handleException(
            "Could not re-assign node names because thread was interrupted", ex,
            ReassignNodeNamesHandler.class);
        // Re-interrupt thread to restore the interrupted state
        Thread.currentThread().interrupt();
      }
    }
  }


  @CanExecute
  public boolean canExecute() {
    return projectManager.getProject().getCorpusGraphs().stream()
        .anyMatch(cg -> !cg.getDocuments().isEmpty());
  }

}
