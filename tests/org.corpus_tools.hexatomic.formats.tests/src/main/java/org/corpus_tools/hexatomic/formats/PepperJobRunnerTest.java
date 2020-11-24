package org.corpus_tools.hexatomic.formats;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.common.JOB_STATUS;
import org.corpus_tools.pepper.common.PepperJob;
import org.corpus_tools.pepper.core.PepperJobImpl;
import org.corpus_tools.pepper.modules.DocumentController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PepperJobRunnerTest {

  private static final int TEST_NUMBER_OF_DOCUMENTS = 12;

  private final class DoNothingPepperJobRunner extends PepperJobRunner {
    private DoNothingPepperJobRunner(PepperJob job) {
      super(job);
    }

    @Override
    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException {
      // Do nothing, runJob is called directly
    }
  }

  private PepperJobImpl job;

  @BeforeEach
  void setUp() throws Exception {
    job = mock(PepperJobImpl.class);
  }

  /**
   * Test that the progress is reported with the correct number of documents.
   * 
   * @throws InterruptedException Can be interrupted.
   * @throws ExecutionException Should not be thrown
   */
  @Test
  void testReportProgress() throws InterruptedException, ExecutionException {

    // This mocked job directly jumps to importing the documents
    when(job.getStatus()).thenReturn(JOB_STATUS.IMPORTING_DOCUMENT_STRUCTURE);
    List<DocumentController> docControllers = new ArrayList<>();
    for (int i = 0; i < TEST_NUMBER_OF_DOCUMENTS; i++) {
      DocumentController controller = mock(DocumentController.class);
      when(controller.getGlobalStatus()).thenReturn(DOCUMENT_STATUS.COMPLETED);
      docControllers.add(controller);
    }
    when(job.getDocumentControllers()).thenReturn(docControllers);

    PepperJobRunner runner = new DoNothingPepperJobRunner(job);
    
    IProgressMonitor monitor = mock(IProgressMonitor.class);
    runner.runJob(monitor);
    
    verify(monitor).isCanceled();
    verify(monitor).beginTask(eq("Exporting 12 documents"), eq(TEST_NUMBER_OF_DOCUMENTS));
    for (int i = 0; i < TEST_NUMBER_OF_DOCUMENTS; i++) {
      verify(monitor).worked(1);
    }
    verify(monitor).done();
    verifyNoMoreInteractions(monitor);
  }

}
