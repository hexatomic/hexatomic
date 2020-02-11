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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.extensions.notification.SaltNotificationFactory;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;

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
  ProjectChangeListener changeListener;

  @Inject
  ErrorService errorService;

  @Inject
  EPartService partService;
  
  @Inject
  UiStatusReport uiStatus;

  private SaltNotificationFactory notificationFactory;

  private final Set<Listener> allListeners = new LinkedHashSet<>();

  private boolean hasUnsavedChanges;

  public ProjectManager() {

  }

  @PostConstruct
  void postConstruct() {
    log.debug("Starting Project Manager");

    // Create an empty project
    this.project = SaltFactory.createSaltProject();
    this.location = Optional.empty();
    this.hasUnsavedChanges = true;

    // Allow to register a change listener with Salt
    notificationFactory = new SaltNotificationFactory();
    SaltFactory.setFactory(notificationFactory);
    notificationFactory.addListener(changeListener);
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
   * Return a document by its ID.
   * 
   * @param documentID The Salt ID
   * @return An optional document.
   */
  public Optional<SDocument> getDocument(String documentID) {
    return this.project.getCorpusGraphs().stream().map(g -> g.getNode(documentID))
        .filter(o -> o instanceof SDocument).map(o -> (SDocument) o).findFirst();
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

    project = SaltFactory.createSaltProject();
    location = Optional.of(path);
    try {
      project.loadCorpusStructure(path);
      events.send(Topics.CORPUS_STRUCTURE_CHANGED, path.toFileString());
    } catch (SaltException ex) {
      errorService.handleException(LOAD_ERROR_MSG + path.toString(), ex, ProjectManager.class);
    }
    hasUnsavedChanges = false;
    uiStatus.setDirty(false);
    uiStatus.setLocation(path.toFileString());
  }

  /**
   * Saves all documents and the corpus structure as Salt project to a new location.
   */
  public void saveTo(URI path) {
    if (path != null) {
      // Remember all loaded document graphs: saving the project will unlink the connection and we
      // need to restore it later.
      final List<String> loadedDocumentIds = project.getCorpusGraphs().stream()
          .flatMap(cg -> cg.getDocuments().stream()).filter(d -> d.getDocumentGraph() != null)
          .map(d -> d.getId()).collect(Collectors.toList());

      // Unsubscribe all listeners for the document changes
      final List<Listener> previousListeners = new LinkedList<>(this.allListeners);
      this.allListeners.clear();

      // Clear existing files from the folder
      Path outputDirectory = Paths.get(path.toFileString());
      try {
        Files.walkFileTree(outputDirectory, new SimpleFileVisitor<Path>() {
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (!dir.equals(outputDirectory)) {
              Files.delete(dir);
            }
            return FileVisitResult.CONTINUE;
          }

        });
      } catch (IOException ex) {
        log.warn("Could not delete output directory of Salt project {}", outputDirectory.toString(),
            ex);
      }

      // Save all files into the now empty folder
      project.saveSaltProject(path);

      location = Optional.of(path);

      // Reload the originally loaded documents
      for (String documentID : loadedDocumentIds) {
        Optional<SDocument> document = getDocument(documentID);
        if (document.isPresent()) {
          document.get().loadDocumentGraph();
          events.send(Topics.DOCUMENT_LOADED, document.get().getId());
        }
      }

      // Re-add the listeners
      this.allListeners.addAll(previousListeners);
      hasUnsavedChanges = false;
      uiStatus.setDirty(false);
      uiStatus.setLocation(path.toFileString());
    }

  }

  /**
   * Saves all loaded documents and the corpus structure as Salt project.
   */
  public void save() {
    if (location.isPresent()) {
      saveTo(location.get());
    }
  }

  /**
   * Closes the current project and creates a new, empty project.
   */
  public void close() {
    // Create an empty project
    this.project = SaltFactory.createSaltProject();
    events.send(Topics.CORPUS_STRUCTURE_CHANGED, null);
    hasUnsavedChanges = true;
    
    uiStatus.setDirty(true);
    uiStatus.setLocation(null);
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void unloadDocumentGraphWhenClosed(
      @UIEventTopic(Topics.DOCUMENT_CLOSED) String documentID) {

    // Check if any other editor is open for this document
    if (documentID != null) {
      Optional<SDocument> document = getDocument(documentID);
      if (document.isPresent()) {
        int counter = 0;
        for (MPart part : partService.getParts()) {
          String otherDocumentID =
              part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID);
          if (documentID.equals(otherDocumentID)) {
            counter++;
          }
        }

        if (counter <= 1) {
          // No other editor found, unload document graph if it can be located on disk
          // TODO: when saving projects is implemented, check if there are unsaved changes
          if (document.get().getDocumentGraphLocation() != null
              && document.get().getDocumentGraph() != null) {
            log.debug("Unloading document {}", documentID);
            document.get().setDocumentGraph(null);
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

      hasUnsavedChanges = true;
      uiStatus.setDirty(true);

      for (Listener l : allListeners) {
        l.notify(type, attribute, oldValue, newValue, container);
      }
    }

  }

}
