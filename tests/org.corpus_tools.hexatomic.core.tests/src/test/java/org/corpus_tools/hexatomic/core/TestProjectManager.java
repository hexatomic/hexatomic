package org.corpus_tools.hexatomic.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

public class TestProjectManager {

	private ProjectManager projectManager;

	private URI exampleProjectURI;

	private MockEventBroker events;

	private ErrorService errorService;

	@Before
	public void setUp() throws Exception {
		File exampleProjectDirectory = new File("src/test/resources/org/corpus_tools/hexatomic/core/example-corpus/");
		assertTrue("Could not locate the example corpus files", exampleProjectDirectory.isDirectory());

		exampleProjectURI = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());

		events = new MockEventBroker();
		errorService = new ErrorService();

		projectManager = new ProjectManager();
		projectManager.events = events;
		projectManager.errorService = errorService;

		projectManager.postConstruct();

	}

	@Test(expected = NullPointerException.class)
	public void testOpenInvalid() {

		projectManager.open(URI.createFileURI("nonExistingPath"));

		projectManager.open(null);
	}

	@Test
	public void testOpenExample() {

		assertEquals(projectManager.getProject().getCorpusGraphs().size(), 0);

		projectManager.open(exampleProjectURI);

		assertEquals(projectManager.getProject().getCorpusGraphs().size(), 1);

		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc1").isPresent());
		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus1/doc2").isPresent());

		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());
		assertTrue(projectManager.getDocument("salt:/rootCorpus/subCorpus2/doc3").isPresent());

	}

	@Test
	public void testEventOnOpen() {

		assertNull(events.lastTopic);
		projectManager.open(exampleProjectURI);
		assertEquals(ProjectManager.TOPIC_CORPUS_STRUCTURE_CHANGED, events.lastTopic);

	}

}
