package org.corpus_tools.hexatomic.core.tests;

import static org.junit.Assert.*;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.junit.Before;
import org.junit.Test;

public class TestProjectManager {
	
	private ProjectManager projectManager;

	@Before
	public void setUp() throws Exception {
		projectManager = new ProjectManager();
	}

	@Test(expected = NullPointerException.class)
	public void testOpenNull() {
		projectManager.open(null);
	}
	
	@Test
	public void testOpen() {
		projectManager.open(null);
		assertNotEquals(projectManager.getProject(), null);
	}

}
