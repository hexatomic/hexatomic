package org.corpus_tools.hexatomic.core;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class TestProjectManager {

	private ProjectManager projectManager;

	private URI exampleProjectURI;

	@Before
	public void setUp() throws Exception {
		File exampleProjectDirectory = new File(
				"src/test/resources/org/corpus_tools/hexatomic/core/example-corpus/");
		assertTrue("Could not locate the example corpus files", exampleProjectDirectory.isDirectory());

		exampleProjectURI = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
		
		projectManager = new ProjectManager();


	}

	@Test(expected = NullPointerException.class)
	public void testOpenNull() {
		projectManager.open(null);
	}

	@Test
	public void testOpenExample() {
		projectManager.open(exampleProjectURI);

		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").isPresent());
		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc2").isPresent());

		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());
		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());

	}

}
