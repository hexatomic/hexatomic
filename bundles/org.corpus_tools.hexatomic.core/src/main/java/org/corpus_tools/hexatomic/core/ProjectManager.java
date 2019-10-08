package org.corpus_tools.hexatomic.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.extensions.notification.SaltNotificationFactory;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;

/**
 * Manages creating and opening Salt projects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
@Singleton
public class ProjectManager {
	
	public static final String TOPIC_CORPUS_STRUCTURE_CHANGED = "TOPIC_CORPUS_STRUCTURE_CHANGED";

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProjectManager.class);
	
	private SaltProject project;
	
	@Inject
	private IEventBroker events;
	
	private SaltNotificationFactory notificationFactory;
	
	public ProjectManager() {
	
	}
	
	@PostConstruct
	private void postConstruct() {
		log.debug("Starting Project Manager");
		
		// Create an empty project		
		this.project = SaltFactory.createSaltProject();
		
		// Allow to register a change listener with Salt		
		notificationFactory = new SaltNotificationFactory();
		SaltFactory.setFactory(notificationFactory);
		// TODO: add change listener for undo/redo here
		
	}
	
	/**
	 * Adds a Salt notification listener for all updates on the Salt project.
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener) {
		notificationFactory.addListener(listener);
	}
	
	/**
	 * Removes a Salt notification listener.
	 * 
	 * @param listener
	 */
	public void removeListener(Listener listener) {
		notificationFactory.removeListener(listener);
	}

	/**
	 * Retrieves the current single instance of a {@link SaltProject}.
	 * 
	 * Note that it is only guaranteed that the corpus graph is loaded.
	 * The single {@link SDocumentGraph} objects connected to the {@link SDocument} objects
	 * of the graph might need to be loaded manually.
	 * 
	 * @return The current Salt project instance.
	 */
	public SaltProject getProject() {
		return project;
	}
	
	/** 
	 * Opens a salt projects from a given location on disk.
	 * 
	 * Only the corpus graph is loaded to avoid over-using the main memory.
	 * 
	 * @param path
	 */
	public void open(URI path) {
		project = SaltFactory.createSaltProject();
		// TODO Implement a user-visible error handling. 
		// There should be a generic way of displaying exceptions to the user (a dialog and some kind of log)
		// in the core bundle, so we can use this functionality here to show any unhandled exception.
		project.loadCorpusStructure(path);
		
		events.send(TOPIC_CORPUS_STRUCTURE_CHANGED, path.toFileString());

	}
	
	
}
