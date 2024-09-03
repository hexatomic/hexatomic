/*-
 * #%L
 * org.corpus_tools.hexatomic.core
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.hexatomic.core.undo.ReversibleOperation;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.exceptions.SaltException;
import org.corpus_tools.salt.exceptions.SaltResourceException;
import org.corpus_tools.salt.util.SaltUtil;
import org.corpus_tools.salt.util.internal.persistence.SaltXML10Writer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

/**
 * Manages creating and opening Salt projects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
@Creatable
@Singleton
public class ProjectManager {

  private final class SaveToRunnable implements IRunnableWithProgress {
    private final Set<String> documentsToReload;
    private final URI path;

    private SaveToRunnable(Set<String> documentsToReload, URI path) {
      this.documentsToReload = documentsToReload;
      this.path = path;
    }

    @Override
    public void run(IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException {

      boolean savingToCurrentLocation =
          getLocation().isPresent() && getLocation().get().equals(path);

      // Collect all documents that need to be saved
      final List<SDocument> documents;
      if (savingToCurrentLocation) {
        // Only the loaded documents need to be saved
        documents = project.getCorpusGraphs().stream().flatMap(cg -> cg.getDocuments().stream())
            .filter(d -> d.getDocumentGraph() != null).collect(Collectors.toList());
      } else {
        // All documents need to be saved
        documents = project.getCorpusGraphs().stream().flatMap(cg -> cg.getDocuments().stream())
            .collect(Collectors.toList());
      }

      monitor.beginTask("Saving Salt project to " + path.toFileString(), documents.size() + 1);

      // Disable listeners for document changes while performing massive amounts of temporary
      // changes
      notificationFactory.setSuppressingEvents(true);

      Path outputDirectory = Paths.get(path.toFileString());
      if (!savingToCurrentLocation) {
        // Clear existing files from the folder, which is not the same as the current one
        clearSaltProjectFolder(outputDirectory);
      }

      outputDirectory.toFile().mkdirs();

      // Save the corpus structure file
      monitor.subTask("Saving corpus structure");
      URI saltProjectFile = path.appendSegment(SaltUtil.FILE_SALT_PROJECT);
      SaltXML10Writer writer = new SaltXML10Writer(saltProjectFile);
      writer.writeSaltProject(project);
      monitor.worked(1);

      // Load each document individually and persist it
      if ((project.getCorpusGraphs() != null) && (!project.getCorpusGraphs().isEmpty())) {

        // Store all documents and copy them from the original location if necessary.
        // When storing the same location, we can assume we did not change the document graph
        // and can skip copying it.
        loadAndPersistAllDocuments(documents, savingToCurrentLocation, monitor);
      }

      location = Optional.of(path);


      sync.asyncExec(() -> {
        // Reload the originally loaded documents when there is an active editor
        for (String documentID : documentsToReload) {
          Optional<SDocument> document = getDocument(documentID, true);
          if (document.isPresent()) {
            events.send(Topics.DOCUMENT_LOADED, document.get().getId());
          }
        }

        uiStatus.setDirty(false);
        uiStatus.setLocation(path.toFileString());

        // Enable the listeners again
        notificationFactory.setSuppressingEvents(false);
        hasUnsavedChanges = false;
        updateCanExecute();

      });

      monitor.done();

    }

    private void clearSaltProjectFolder(Path outputDirectory) {
      try {
        // Parse any existing project file to get all document files that need to be deleted
        SaltProject existingProject =
            SaltUtil.loadSaltProject(URI.createFileURI(outputDirectory.toString()));

        if (existingProject != null) {
          List<URI> allDocuments =
              existingProject.getCorpusGraphs().stream().flatMap(cg -> cg.getDocuments().stream())
                  .filter(d -> d.getDocumentGraphLocation() != null)
                  .map(SDocument::getDocumentGraphLocation).collect(Collectors.toList());
          // Delete the Salt XML files belonging to this document
          for (URI doc : allDocuments) {
            Path saltFile = Paths.get(doc.toFileString());
            if (Files.exists(saltFile)) {
              Files.delete(saltFile);
            }
          }
        }
      } catch (SaltResourceException ex) {
        // This is not a valid salt project folder, don't delete any files
      } catch (IOException ex) {
        log.warn("Could not delete output directory of Salt project {}", outputDirectory, ex);
      }
    }

    private URI getOutputPathForDocument(URI saltProjectFolder, SDocument doc) {
      URI docUri = saltProjectFolder;
      for (String seg : doc.getPath().segments()) {
        docUri = docUri.appendSegment(seg);
      }
      docUri = docUri.appendFileExtension(SaltUtil.FILE_ENDING_SALT_XML);
      return docUri;
    }

    private void loadAndPersistAllDocuments(List<SDocument> documents,
        boolean savingToCurrentLocation, IProgressMonitor monitor) {
      for (SDocument doc : documents) {
        if (monitor.isCanceled()) {
          monitor.done();
          return;
        }
        loadAndPersistSingleDocument(doc, savingToCurrentLocation, monitor);
      }
    }

    private void loadAndPersistSingleDocument(SDocument doc, boolean savingToCurrentLocation,
        IProgressMonitor monitor) {
      monitor.subTask(doc.getPath().toString());

      URI documentOutputUri = getOutputPathForDocument(path, doc);
      Path documentOutputPath = Paths.get(documentOutputUri.toFileString());
      if (savingToCurrentLocation) {
        if (doc.getDocumentGraph() == null) {
          // No need to save if not changed in memory
          log.debug("Save: Skipping unchanged document {} in same directory", doc.getId());
        } else {
          // Save to new location
          doc.saveDocumentGraph(documentOutputUri);
        }
      } else {
        if (doc.getDocumentGraph() == null && doc.getDocumentGraphLocation() != null) {
          // Copy the unchanged Salt XML file: this is much faster than deserializing and
          // serializing it
          try {
            documentOutputPath.toFile().getParentFile().mkdirs();
            Path sourceFile = Paths.get(doc.getDocumentGraphLocation().toFileString());
            if (Files.exists(sourceFile)) {
              Files.copy(sourceFile, documentOutputPath);
            }
          } catch (IOException ex) {
            monitor.done();
            sync.asyncExec(() -> errorService.handleException(
                "Could not copy Salt document " + doc.getId(), ex, ProjectManager.class));
            return;
          }
        } else {
          // Save to new location
          doc.saveDocumentGraph(documentOutputUri);
        }
      }

      // Report one finished document
      monitor.worked(1);
    }
  }

  private static final int MAX_UNDO_HISTORY_SIZE = 25;

  private static final String LOAD_ERROR_MSG = "Could not load salt project from ";

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ProjectManager.class);

  private SaltProject project;
  private Optional<URI> location = Optional.empty();

  @Inject
  IEventBroker events;

  @Inject
  ErrorService errorService;

  @Inject
  EPartService partService;

  @Inject
  MApplication application;

  @Inject
  UiStatusReport uiStatus;

  @Inject
  UISynchronize sync;

  @Inject
  SaltNotificationFactory notificationFactory;

  private boolean hasUnsavedChanges;

  private final Deque<ChangeSet> undoChangeSets = new LinkedList<>();
  private final Deque<ChangeSet> redoChangeSets = new LinkedList<>();

  private final List<ReversibleOperation> uncommittedChanges = new LinkedList<>();


  @PostConstruct
  void postConstruct() {
    log.debug("Starting Project Manager");

    // Set the factory before any Salt object is created
    SaltFactory.setFactory(notificationFactory);

    // Create an empty project
    this.project = SaltFactory.createSaltProject();
    this.location = Optional.empty();
    this.hasUnsavedChanges = false;
  }

  /**
   * Retrieves the current single instance of a {@link SaltProject}.
   * 
   * <p>
   * Note that it is only guaranteed that the corpus graph is loaded. The single
   * {@link SDocumentGraph} objects connected to the {@link SDocument} objects of the graph might
   * need to be loaded manually.
   * </p>
   * 
   * @return The current Salt project instance.
   */
  public SaltProject getProject() {
    return project;
  }

  /**
   * Sets the current single instance of a {@link SaltProject}.
   * 
   * @param project The new current Salt project instance.
   */
  public void setProject(SaltProject project) {
    closeOpenEditors();

    this.project = project;

    this.location = Optional.empty();
    hasUnsavedChanges = true;

    uiStatus.setDirty(true);
    uiStatus.setLocation(null);

    events.send(Topics.PROJECT_LOADED, null);
  }

  /**
   * Return a document by its ID. The document graph might not be loaded.
   * 
   * @param documentID The Salt ID
   * 
   * @return An optional document.
   * 
   * @see ProjectManager#getDocument(String, boolean)
   */
  public Optional<SDocument> getDocument(String documentID) {
    return getDocument(documentID, false);
  }

  /**
   * Return a document by its ID.
   * 
   * @param documentID The Salt ID
   * @param loadDocumentGraph If true, the corresponding document graph will be loaded.
   * @return An optional document.
   */
  public Optional<SDocument> getDocument(String documentID, boolean loadDocumentGraph) {
    Optional<SDocument> result =
        this.project.getCorpusGraphs().stream().map(g -> g.getNode(documentID))
            .filter(SDocument.class::isInstance).map(SDocument.class::cast).findFirst();

    if (loadDocumentGraph && result.isPresent() && result.get().getDocumentGraph() == null) {
      if (result.get().getDocumentGraphLocation() == null) {
        // We can't load the non-existing document graph from disk, create a new empty one
        result.get().createDocumentGraph();
      } else {
        Path saltFile = Paths.get(result.get().getDocumentGraphLocation().toFileString());
        if (Files.exists(saltFile)) {
          // Load the existing document graph from disk
          try {
            // Load document and suppress unneeded notifications
            notificationFactory.setSuppressingEvents(true);
            result.get().loadDocumentGraph();
            notificationFactory.setSuppressingEvents(false);
            // Send event notification of the loaded document
            events.send(Topics.DOCUMENT_LOADED, result.get().getId());
          } catch (SaltResourceException ex) {
            errorService.handleException(
                "Could not load document graph (the actual annotations for document " + documentID
                    + ").",
                ex, OpenSaltDocumentHandler.class);
          }
        } else {
          // Create a new empty document graph
          result.get().createDocumentGraph();
        }
      }
    }

    return result;
  }

  /**
   * Get the location of the current salt project if it is set.
   * 
   * @return The optional location as URI on the file system.
   */
  public Optional<URI> getLocation() {
    return location;
  }

  /**
   * Returns true if there are non-saved changes.
   * 
   * @return True for "dirty" state.
   */
  public boolean isDirty() {
    return hasUnsavedChanges;
  }

  /**
   * Opens a salt projects from a given location on disk.
   * 
   * <p>
   * Only the corpus graph is loaded to avoid over-using the main memory.
   * </p>
   * 
   * @param path The path top open
   */
  public void open(URI path) {

    closeOpenEditors();

    project = SaltFactory.createSaltProject();
    location = Optional.of(path);
    try {
      // Suppress superfluous notifications
      notificationFactory.setSuppressingEvents(true);
      project.loadCorpusStructure(path);
      notificationFactory.setSuppressingEvents(false);
      events.send(Topics.PROJECT_LOADED, path.toFileString());
    } catch (SaltException ex) {
      errorService.handleException(LOAD_ERROR_MSG + path.toString(), ex, ProjectManager.class);
    }
    hasUnsavedChanges = false;
    uiStatus.setDirty(false);
    uiStatus.setLocation(path.toFileString());
    updateCanExecute();
  }

  /**
   * Close all open editors.
   */
  private void closeOpenEditors() {

    IEclipseContext ctx = application.getContext();
    if (ctx != null && ctx.getActiveChild() != null) {
      try {
        for (MPart part : partService.getParts()) {
          String docID = part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
          if (docID != null && !docID.isEmpty()) {
            partService.hidePart(part);
          }
        }
      } catch (IllegalStateException ex) {
        // Ignore, this can happen when the application is closing
      }
    }
  }


  /**
   * Saves all documents and the corpus structure as Salt project to a new location.
   * 
   * @param path The file URI of the new location.
   * @param shell The SWT shell: needed to display the progress dialog.
   */
  public void saveTo(URI path, Shell shell) {
    if (path != null) {

      // Remember all loaded document graphs: saving the project will unlink the connection and
      // we need to restore it later.
      // Only re-load documents with at least one open editor.
      final Set<String> documentsToReload =
          project.getCorpusGraphs().stream().flatMap(cg -> cg.getDocuments().stream())
              .filter(d -> d.getDocumentGraph() != null).filter(d -> getNumberOfOpenEditors(d) > 0)
              .map(SDocument::getId).collect(Collectors.toSet());

      IRunnableWithProgress operation = new SaveToRunnable(documentsToReload, path);

      ProgressMonitorDialog dialog = createProgressMonitorDialog(shell);

      dialog.setCancelable(true);
      try {
        dialog.run(true, true, operation);
      } catch (InvocationTargetException ex) {
        errorService.handleException("Could not save project", ex, ProjectManager.class);
      } catch (InterruptedException ex) {
        errorService.handleException("Could not save project because thread was interrupted", ex,
            ProjectManager.class);
        // Re-interrupt thread to restore the interrupted state
        Thread.currentThread().interrupt();
      }
    }
  }


  /**
   * Helper method to create a new progress monitor. This is a method so it can be overwritten by
   * other implementations.
   * 
   * @param shell A SWT shell.
   * @return The newly created progress monitor.
   */
  public ProgressMonitorDialog createProgressMonitorDialog(Shell shell) {
    return new ProgressMonitorDialog(shell);
  }

  /**
   * Saves all loaded documents and the corpus structure as Salt project.
   * 
   * @param shell The SWT shell: needed to display the progress dialog.
   */
  public void save(Shell shell) {
    if (location.isPresent()) {
      saveTo(location.get(), shell);
    }
  }

  /**
   * Closes the current project and creates a new, empty project.
   */
  public void newProject() {

    closeOpenEditors();

    // Create an empty project
    this.location = Optional.empty();
    this.project = SaltFactory.createSaltProject();
    hasUnsavedChanges = false;
    this.uncommittedChanges.clear();
    this.undoChangeSets.clear();
    this.redoChangeSets.clear();

    uiStatus.setDirty(false);
    uiStatus.setLocation(null);

    events.send(Topics.PROJECT_LOADED, null);
    updateCanExecute();
  }


  /**
   * Adds a checkpoint. A user will be able to undo all changes made between checkpoints.
   */
  public void addCheckpoint() {
    addCheckpoint(true);
  }

  private void addCheckpoint(boolean isManual) {
    ChangeSet changes = null;
    if (!uncommittedChanges.isEmpty()) {
      // All recorded changes are reversable without saving and loading the whole document, create
      // a different kind of checkpoint using a copy of all uncomitted changes
      log.debug("Adding {} changes as checkpoint to undo list", uncommittedChanges.size());
      changes = new ChangeSet(uncommittedChanges);
      undoChangeSets.addFirst(changes);
      // Limit the maximum number changesets the user can undo
      while (undoChangeSets.size() > MAX_UNDO_HISTORY_SIZE) {
        undoChangeSets.removeLast();
      }
    }

    if (isManual) {
      // Invalidate all redo operations when the graph was changed manually
      redoChangeSets.clear();
    }

    // All uncommitted changes have been handled: restore internal recored state to default:
    uncommittedChanges.clear();

    if (changes != null) {
      events.send(Topics.ANNOTATION_CHECKPOINT_CREATED, changes);
    }
    updateCanExecute();
  }

  /**
   * Checks whether it is possible to perform an undo.
   * 
   * @return True if undo is possible.
   */
  public boolean canUndo() {
    return !undoChangeSets.isEmpty();
  }

  /**
   * Undoes all changes made in the last checkpoint.
   */
  public void undo() {

    // Make sure the last uncommitted changes are added as a checkpoint, but don't fire an event
    if (!uncommittedChanges.isEmpty()) {
      log.warn("Adding checkpoint for {} uncommited changes before performing undo.",
          uncommittedChanges.size());
      undoChangeSets.add(new ChangeSet(uncommittedChanges));
      uncommittedChanges.clear();
    }

    if (canUndo()) {
      // Restore all documents from the last checkpoint
      ChangeSet lastChangeSet = this.undoChangeSets.pop();
      // Iterate over the elements in reversed order
      ListIterator<ReversibleOperation> itLastChangeSet =
          lastChangeSet.getChanges().listIterator(lastChangeSet.getChanges().size());
      while (itLastChangeSet.hasPrevious()) {
        ReversibleOperation op = itLastChangeSet.previous();
        op.restore();
      }

      // All uncommitted changes recorded when restoring the previous state should be added
      // to the "redo" stack instead of the undo one.
      if (!uncommittedChanges.isEmpty()) {
        redoChangeSets.addFirst(new ChangeSet(uncommittedChanges));
        uncommittedChanges.clear();
      }

      // Performing an undo might unload documents, close the editors to avoid errors
      // and give the user a visually clue (s)he unloaded/removed a document.
      closeEditorsForRemovedDocuments();

      events.send(Topics.ANNOTATION_CHECKPOINT_RESTORED, lastChangeSet);
      updateCanExecute();
    }
  }

  /**
   * Undoes all changes made since the last checkpoint.
   */
  public void revertToLastCheckpoint() {

    if (!uncommittedChanges.isEmpty()) {
      // Group uncommitted changes in a changeset
      ChangeSet uncommitedChangeSet = new ChangeSet(uncommittedChanges);
      // Iterate over the elements in reversed order
      ListIterator<ReversibleOperation> itUncommitedChangeSet =
          uncommitedChangeSet.getChanges().listIterator(uncommitedChangeSet.getChanges().size());
      while (itUncommitedChangeSet.hasPrevious()) {
        ReversibleOperation op = itUncommitedChangeSet.previous();
        op.restore();
      }

      // Clear all reverted changes
      uncommittedChanges.clear();

      // Performing an undo might unload documents, close the editors to avoid errors
      // and give the user a visually clue (s)he unloaded/removed a document.
      closeEditorsForRemovedDocuments();

      events.send(Topics.ANNOTATION_CHECKPOINT_RESTORED, uncommitedChangeSet);
      updateCanExecute();
    }
  }

  /**
   * Checks whether it is possible to perform an redo.
   * 
   * @return True if redo is possible.
   */
  public boolean canRedo() {
    return !redoChangeSets.isEmpty();
  }

  /**
   * Redo all changes made in the last undo.
   */
  public void redo() {
    if (canRedo()) {
      ChangeSet lastChangeSet = this.redoChangeSets.pop();
      // Iterate over the elements in reversed order
      ListIterator<ReversibleOperation> itLastChangeSet =
          lastChangeSet.getChanges().listIterator(lastChangeSet.getChanges().size());
      while (itLastChangeSet.hasPrevious()) {
        ReversibleOperation op = itLastChangeSet.previous();
        op.restore();
      }

      addCheckpoint(false);

      // Performing an redo might unload documents, close the editors to avoid errors
      // and give the user a visually clue (s)he unloaded/removed a document.
      closeEditorsForRemovedDocuments();

      events.send(Topics.ANNOTATION_CHECKPOINT_RESTORED, lastChangeSet);
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  void subscribeUndoOperationAdded(
      @UIEventTopic(Topics.ANNOTATION_OPERATION_ADDED) Object element) {
    if (element instanceof ReversibleOperation) {
      uncommittedChanges.add((ReversibleOperation) element);
    }
  }

  /**
   * Close all open editors where the document has been unloaded or removed by the undo action.
   */
  private void closeEditorsForRemovedDocuments() {

    try {
      for (MPart part : partService.getParts()) {
        String docID = part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
        if (docID != null && !docID.isEmpty()) {
          Optional<SDocument> document = this.getDocument(docID);
          if (!document.isPresent() || document.get().getDocumentGraph() == null) {
            partService.hidePart(part);
          }
        }
      }
    } catch (IllegalStateException ex) {
      // Ignore, this can happen when the application is closing
    }
  }

  /**
   * Check if how many editors are currently open for the given document.
   * 
   * @param document The document to check.
   * @return The number of open editors.
   */
  private int getNumberOfOpenEditors(SDocument document) {
    int counter = 0;
    try {
      for (MPart part : partService.getParts()) {
        String otherDocumentID = part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
        if (document.getId().equals(otherDocumentID)) {
          counter++;
        }
      }
    } catch (IllegalStateException ex) {
      // Ignore, this can happen when the application is closing
    }
    return counter;
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void unloadDocumentGraphWhenClosed(
      @UIEventTopic(Topics.DOCUMENT_CLOSED) String documentID) {

    if (isDirty()) {
      // Do not cleanup if any change has been made to the project
      return;
    }

    // Check if any other editor is open for this document
    if (documentID != null) {
      Optional<SDocument> document = getDocument(documentID, false);
      if (document.isPresent() && getNumberOfOpenEditors(document.get()) <= 1
          && document.get().getDocumentGraphLocation() != null
          && document.get().getDocumentGraph() != null) {
        // No other editor found, unload document graph if it can be located on disk
        log.debug("Unloading document {}", documentID);
        // Suppress superfluous notifications
        notificationFactory.setSuppressingEvents(true);
        document.get().setDocumentGraph(null);
        notificationFactory.setSuppressingEvents(false);
      }
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void projectChanged(@UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {
    hasUnsavedChanges = true;
    uiStatus.setDirty(true);
  }

  /**
   * Re-evaluate all @CanExecute methods.
   */
  private void updateCanExecute() {
    events.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
  }
}
