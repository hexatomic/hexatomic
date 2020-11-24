package org.corpus_tools.hexatomic.formats;

import com.google.common.base.Joiner;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.core.PepperJobImpl;
import org.corpus_tools.pepper.modules.DocumentController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Base class for running {@link PepperJob} instances in the background and reported the status
 * progress.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
public abstract class PepperJobRunner implements IRunnableWithProgress {

  private final PepperJob job;

  protected PepperJobRunner(PepperJob job) {
    this.job = job;
  }

  public PepperJob getJob() {
    return job;
  }

  private void reportDocumentConversionProgress(IProgressMonitor monitor,
      Set<String> completedDocuments) {

    if (job instanceof PepperJobImpl) {
      PepperJobImpl pepperJobImpl = (PepperJobImpl) job;
      Set<DocumentController> activeDocuments =
          new LinkedHashSet<>(pepperJobImpl.getActiveDocuments());
      if (!activeDocuments.isEmpty()) {
        monitor.subTask(Joiner.on(", ")
            .join(activeDocuments.stream().map(d -> d.getDocument().getName()).iterator()));
      }

      for (DocumentController controller : pepperJobImpl.getDocumentControllers()) {
        DOCUMENT_STATUS docStatus = controller.getGlobalStatus();

        if ((docStatus == DOCUMENT_STATUS.COMPLETED || docStatus == DOCUMENT_STATUS.DELETED
            || docStatus == DOCUMENT_STATUS.FAILED)
            && completedDocuments.add(controller.getGlobalId())) {
          monitor.worked(1);
        }
      }
    }
  }

  /**
   * Checks for documents with non-completed status after pepper job has been executed.
   * 
   * @return
   */
  protected Set<String> getFailedDocuments() {
    Set<String> failedDocuments = new TreeSet<>();
    if (job instanceof PepperJobImpl) {
      PepperJobImpl pepperJobImpl = (PepperJobImpl) job;
      for (DocumentController docController : pepperJobImpl.getDocumentControllers()) {

        DOCUMENT_STATUS status = docController.getGlobalStatus();
        if (status != DOCUMENT_STATUS.COMPLETED) {
          failedDocuments.add(docController.getGlobalId());
        }
      }
    }
    return failedDocuments;
  }

  /**
   * Run the background Pepper job, report progress and wait until the job is finished.
   * 
   * @param monitor An Eclipse progress monitor which is used to show the progress to the user.
   * @throws InterruptedException Thrown when this function is waiting for the background job and is
   *         interrupted.
   * @throws ExecutionException Thrown for major execution errors.
   */
  protected void runJob(IProgressMonitor monitor)
      throws InterruptedException, ExecutionException {

    ExecutorService serviceExec = Executors.newSingleThreadExecutor();
    Future<?> background = serviceExec.submit(getJob()::convert);

    Optional<Integer> numberOfJobs = Optional.empty();
    // Create a set of already finished documents
    Set<String> completedDocuments = new HashSet<>();

    while (!background.isDone() && !background.isCancelled()) {
      if (monitor.isCanceled()) {
        // Cancel the Pepper job
        job.cancelConversion();
        return;
      }

      if (job instanceof PepperJobImpl) {
        PepperJobImpl pepperJobImpl = (PepperJobImpl) job;
        JOB_STATUS jobStatus = pepperJobImpl.getStatus();

        // Check if we can get the number of documents after the corpus structure has been
        // imported
        if (!numberOfJobs.isPresent() && jobStatus == JOB_STATUS.IMPORTING_DOCUMENT_STRUCTURE) {
          // We don't know how many documents are present previously but since exporting the
          // documents has started, we can get this number now
          numberOfJobs = Optional.of(pepperJobImpl.getDocumentControllers().size());
          monitor.beginTask("Exporting " + numberOfJobs.get() + " documents", numberOfJobs.get());
        }

        if (numberOfJobs.isPresent()) {
          // Report detailed status of the conversion progress of the documents
          reportDocumentConversionProgress(monitor, completedDocuments);
        }
      }
      Thread.sleep(1000);
    }

    monitor.done();

    // This will re-throw any possible major exception
    background.get();
  }

}
