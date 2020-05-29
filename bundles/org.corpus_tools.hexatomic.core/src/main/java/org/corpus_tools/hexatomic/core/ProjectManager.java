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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.exceptions.SaltException;
import org.corpus_tools.salt.exceptions.SaltResourceException;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.extensions.notification.SaltNotificationFactory;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.corpus_tools.salt.util.SaltUtil;
import org.corpus_tools.salt.util.internal.persistence.SaltXML10Writer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
  UiStatusReport uiStatus;

  @Inject
  UISynchronize sync;

  private SaltNotificationFactory notificationFactory;

  private final Set<Listener> allListeners = new LinkedHashSet<>();
  private boolean listenersActive = true;

  private boolean hasUnsavedChanges;

  public ProjectManager() {

  }

  @PostConstruct
  void postConstruct() {
    log.debug("Starting Project Manager");

    // Create an empty project
    this.project = SaltFactory.createSaltProject();
    this.location = Optional.empty();
    this.hasUnsavedChanges = false;

    // Allow to register a change listener with Salt
    notificationFactory = new SaltNotificationFactory();
    SaltFactory.setFactory(notificationFactory);
    notificationFactory.addListener(new ProxyListener());

  }

  /**
   * Adds a Salt notification listener for all updates on the Salt project.
   * 
   * @param listener The listener to add
   */
  public void addListener(Listener listener) {
    allListeners.add(listener);
  }

  /**
   * Removes a Salt notification listener.
   * 
   * @param listener The listener to remove
   */
  public void removeListener(Listener listener) {
    if (listener != null) {
      allListeners.remove(listener);
    }
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
   * Return a document by its ID. The document graph might not be loaded.
   * 
   * @param documentID The Salt ID
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
            .filter(o -> o instanceof SDocument).map(o -> (SDocument) o).findFirst();

    if (loadDocumentGraph && result.isPresent() && result.get().getDocumentGraph() == null) {
      if (result.get().getDocumentGraphLocation() == null) {
        // We can't load the non-existing document graph from disk, create a new empty one
        result.get().createDocumentGraph();
      } else {
        Path saltFile = Paths.get(result.get().getDocumentGraphLocation().toFileString());
        if (Files.exists(saltFile)) {
          // Load the existing document graph from disk
          try {
            // Deactivate listeners to avoid partial updates during load
            listenersActive = false;
            // Load document
            result.get().loadDocumentGraph();
            // Re-enable listeners and notify them of the loaded document
            listenersActive = true;
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
      project.loadCorpusStructure(path);
      events.send(Topics.PROJECT_LOADED, path.toFileString());
    } catch (SaltException ex) {
      errorService.handleException(LOAD_ERROR_MSG + path.toString(), ex, ProjectManager.class);
    }
    hasUnsavedChanges = false;
    uiStatus.setDirty(false);
    uiStatus.setLocation(path.toFileString());
  }

  /**
   * Close all open editors.
   */
  private void closeOpenEditors() {

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

  /**
   * Saves all documents and the corpus structure as Salt project to the specified location.
   * 
   * @param path The file URI of the location to save to.
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
              .map(d -> d.getId()).collect(Collectors.toSet());

      IRunnableWithProgress operation = new IRunnableWithProgress() {

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
          listenersActive = false;

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
          if ((project.getCorpusGraphs() != null) && (project.getCorpusGraphs().size() > 0)) {

            // Store all documents and copy them from the original location if necessary.
            // When storing the same location, we can assume we did not change the document graph
            // and can skip copying it.
            for (SDocument doc : documents) {
              if (monitor.isCanceled()) {
                monitor.done();
                return;
              }

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
                    sync.asyncExec(() -> {
                      errorService.handleException("Could not copy Salt document " + doc.getId(),
                          ex, ProjectManager.class);
                    });
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
            listenersActive = true;
            hasUnsavedChanges = false;

          });

          monitor.done();

        }
      };

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

      dialog.setCancelable(true);
      try {
        dialog.run(true, true, operation);
      } catch (InvocationTargetException | InterruptedException ex) {
        errorService.handleException("Could not save project", ex, ProjectManager.class);
      }
    }
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
                .map(d -> d.getDocumentGraphLocation()).collect(Collectors.toList());
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
      log.warn("Could not delete output directory of Salt project {}", outputDirectory.toString(),
          ex);
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

    uiStatus.setDirty(false);
    uiStatus.setLocation(null);

    events.send(Topics.PROJECT_LOADED, null);
  }

  /**
   * Check if how many editors are currently open for the given document.
   * 
   * @param document The document to check.
   * @return
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
      if (document.isPresent()) {
        if (getNumberOfOpenEditors(document.get()) <= 1) {
          // No other editor found, unload document graph if it can be located on disk
          if (document.get().getDocumentGraphLocation() != null
              && document.get().getDocumentGraph() != null) {
            log.debug("Unloading document {}", documentID);
            listenersActive = false;
            document.get().setDocumentGraph(null);
            listenersActive = true;
          }
        }
      }
    }
  }


  /**
   * We can't add listeners to existing objects in the corpus graph, so iterate over the internal
   * list of all registered listeners and notify them.
   *
   */
  private final class ProxyListener implements Listener {

    @Override
    public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
        Object newValue, Object container) {

      sync.syncExec(() -> {
        if (listenersActive) {
          hasUnsavedChanges = true;
          uiStatus.setDirty(true);

          for (Listener l : allListeners) {
            l.notify(type, attribute, oldValue, newValue, container);
          }
        }
      });
    }

  }

}
