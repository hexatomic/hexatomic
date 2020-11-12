package org.corpus_tools.hexatomic.grid.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.eclipse.emf.common.util.URI;

/**
 * A helper class for unit testing the grid editor.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class TestHelper {

  private static final String examplePath =
      "../org.corpus_tools.hexatomic.core.tests/src/main/resources/"
          + "org/corpus_tools/hexatomic/core/example-corpus/";

  /**
   * Creates a {@link GraphDataProvider} with fully resolved example data provided by the
   * {@link SampleGenerator}.
   * 
   * @return the graph data provider with fully resolved example data.
   */
  public static GraphDataProvider createDataProvider() {
    SDocument newDocument = SaltFactory.createSDocument();
    newDocument.setName("TEST_DOCUMENT");
    SampleGenerator.createDocumentStructure(newDocument);
    SDocumentGraph newGraph = newDocument.getDocumentGraph();
    GraphDataProvider fixtureProvider = new GraphDataProvider();
    fixtureProvider.setGraph(newGraph);
    STextualDS text = newGraph.getTextualDSs().get(0);
    fixtureProvider.setDsAndResolveGraph(text);
    return fixtureProvider;
  }

  /**
   * Retrieves the first in the list of {@link STextualDS} in the given graph.
   * 
   * @param graph the document graph for which to retrieve the first {@link STextualDS}.
   * @return the first {@link STextualDS} in the list in the given graph
   */
  public static STextualDS getFirstTextFromGraph(SDocumentGraph graph) {
    STextualDS text = graph.getTextualDSs().get(0);
    assertNotNull(text);
    return text;
  }

  /**
   * Loads an {@link SDocumentGraph} with example data.
   * 
   * @return the document graph with example data
   */
  public static SDocumentGraph retrieveGraph() {
    return retrieveGraph(examplePath);
  }

  /**
   * Loads an {@link SDocumentGraph} for the given path.
   * 
   * @param path the path for the Salt XML project file directory for which the first corpus graph's
   *        first document's document graph should be loaded.
   * @return the loaded document graph
   */
  public static SDocumentGraph retrieveGraph(String path) {
    File exampleProjectDirectory = new File(path);
    assertTrue(exampleProjectDirectory.isDirectory());
    URI exampleProjectUri = URI.createFileURI(exampleProjectDirectory.getAbsolutePath());
    SaltProject project = SaltFactory.createSaltProject();
    project.loadSaltProject(exampleProjectUri);
    SDocumentGraph graph =
        project.getCorpusGraphs().get(0).getDocuments().get(0).getDocumentGraph();
    assertNotNull(graph);
    return graph;
  }

}
