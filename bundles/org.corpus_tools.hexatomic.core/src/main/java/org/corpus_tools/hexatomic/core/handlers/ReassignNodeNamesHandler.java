package org.corpus_tools.hexatomic.core.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class ReassignNodeNamesHandler {

  @Inject
  ErrorService errorService;

  @Inject
  ProjectManager projectManager;

  protected class NameAssigner implements IRunnableWithProgress {

    List<String> documentIds;

    public NameAssigner(List<String> documentIds) {
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

        Optional<SDocument> document = projectManager.getDocument(docId, true);
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

  @Execute
  protected void execute(Shell shell) {
    boolean performReassign = MessageDialog.openQuestion(shell, "Re-assign Node Names",
        "Do you want to assign automatically generated names to all nodes and token? "
            + "This will be applied to all documents in all opened corpora. "
            + "Token will get the prefix \"t\" and a number (e.g. \"t1\") and "
            + "other nodes the prefix \"n\" and a number (e.g. \"n2\").");
    if (performReassign) {
      List<String> allDocuments = projectManager.getProject().getCorpusGraphs().stream()
          .flatMap(cg -> cg.getDocuments().stream()).map(d -> d.getId())
          .collect(Collectors.toList());
      NameAssigner operation = new NameAssigner(allDocuments);

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
