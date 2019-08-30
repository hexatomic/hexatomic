package org.corpus_tools.hexatomic.core;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;

/**
 * Manages creating, opening and saving projects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
@Singleton
public class ProjectManager {
	
	public static final String TOPIC_PROJECT_CHANGED = "TOPIC_PROJECT_CHANGED";

	private SaltProject project;
	
	@Inject
	private IEventBroker events;
	
	public ProjectManager() {
		// create an empty project
		this.project = SaltFactory.createSaltProject();
	}

	/**
	 * Retrieves the current single instance of a {@link SaltProject}.
	 * 
	 * Note that it is only guaranteed that the corpus graph is loaded.
	 * The single {@link SDocumentGraph} objects connected to the {@link SDocument} objects
	 * of the graph might need to be loaded manually.
	 * @return
	 */
	public SaltProject getProject() {
		return project;
	}
	
	/** Opens a salt projects from a given location on disk.
	 * 
	 * Only the corpus graph is loaded to avoid over-using the main memory.
	 * 
	 * @param path
	 */
	public void open(URI path) {
		project = SaltFactory.createSaltProject();
		// Only load the corpus structure, single documents need to be retrieved later on demand
		// to save main memory.
		// TODO error handling
		project.loadCorpusStructure(path);
		
		events.send(TOPIC_PROJECT_CHANGED, path.toFileString());

	}
	
	
}
