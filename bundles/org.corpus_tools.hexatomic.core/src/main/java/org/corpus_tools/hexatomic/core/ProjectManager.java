package org.corpus_tools.hexatomic.core;

import javax.inject.Singleton;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.e4.core.di.annotations.Creatable;

/**
 * Manages creating, opening and saving projects.
 * 
 * @author Thomas Krause {@literal <krauseto@hu-berlin.de>}
 *
 */
@Creatable
@Singleton
public class ProjectManager {

	private SaltProject project;
	
	public ProjectManager() {
		// create an empty project
		this.project = SaltFactory.createSaltProject();
	}

	public SaltProject getProject() {
		return project;
	}
	
	
}
