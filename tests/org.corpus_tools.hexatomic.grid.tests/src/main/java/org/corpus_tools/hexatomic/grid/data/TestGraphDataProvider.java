package org.corpus_tools.hexatomic.grid.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.data.Column.ColumnType;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GraphDataProvider}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
class TestGraphDataProvider {

  private ErrorService errorService;

  private static GraphDataProvider fixture = null;
  private SDocumentGraph exampleGraph;
  private STextualDS exampleText;
  private SDocumentGraph overlappingExampleGraph;
  private STextualDS overlappingExampleText;

  private static final String examplePath =
      "../org.corpus_tools.hexatomic.core.tests/src/main/resources/"
          + "org/corpus_tools/hexatomic/core/example-corpus/";
  private static final String overlappingExamplePath =
      "src/main/resources/org/corpus_tools/hexatomic/grid/overlapping-spans/";

  /**
   * Set up the fixture and models for each test.
   * 
   * @throws java.lang.Exception Any exception that is thrown during the setup phase.
   */
  @BeforeEach
  void setUp() throws Exception {
    fixture = new GraphDataProvider();
    exampleGraph = retrieveGraph(examplePath);
    overlappingExampleGraph = retrieveGraph(overlappingExamplePath);
    exampleText = getFirstTextFromGraph(exampleGraph);
    overlappingExampleText = getFirstTextFromGraph(overlappingExampleGraph);
    errorService = mock(ErrorService.class);
    fixture.errors = errorService;
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueEmptyGraph() {
    assertEquals(null, fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(1, 1));
    assertEquals(null, fixture.getDataValue(1, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueSetDsWithoutTokens() {
    STextualDS ds = mock(STextualDS.class);
    SDocumentGraph graph = mock(SDocumentGraph.class);
    fixture.setGraph(graph);
    fixture.setDsAndResolveGraph(ds);
    assertEquals(null, fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
    fixture.getDataValue(1, 1);
    fixture.getDataValue(1, 0);
    verify(errorService, times(2)).handleException(
        matches("Index: 1, Size: 1|Index 1 out of bounds for length 1"),
        isA(IndexOutOfBoundsException.class), eq(GraphDataProvider.class));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueDefaultExample() {
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);

    List<SToken> sortedTokens = exampleGraph.getSortedTokenByText();
    assertEquals(sortedTokens.get(0), fixture.getDataValue(0, 0));
    assertEquals(sortedTokens.get(1), fixture.getDataValue(0, 1));
    assertEquals(sortedTokens.get(2), fixture.getDataValue(0, 2));
    assertEquals(sortedTokens.get(3), fixture.getDataValue(0, 3));
    assertEquals(sortedTokens.get(4), fixture.getDataValue(0, 4));
    assertEquals(sortedTokens.get(5), fixture.getDataValue(0, 5));
    assertEquals(sortedTokens.get(6), fixture.getDataValue(0, 6));
    assertEquals(sortedTokens.get(7), fixture.getDataValue(0, 7));
    assertEquals(sortedTokens.get(8), fixture.getDataValue(0, 8));
    assertEquals(sortedTokens.get(9), fixture.getDataValue(0, 9));
    assertEquals(sortedTokens.get(10), fixture.getDataValue(0, 10));

    assertEquals(sortedTokens.get(0), fixture.getDataValue(1, 0));
    assertEquals(sortedTokens.get(1), fixture.getDataValue(1, 1));
    assertEquals(sortedTokens.get(2), fixture.getDataValue(1, 2));
    assertEquals(sortedTokens.get(3), fixture.getDataValue(1, 3));
    assertEquals(sortedTokens.get(4), fixture.getDataValue(1, 4));
    assertEquals(sortedTokens.get(5), fixture.getDataValue(1, 5));
    assertEquals(sortedTokens.get(6), fixture.getDataValue(1, 6));
    assertEquals(sortedTokens.get(7), fixture.getDataValue(1, 7));
    assertEquals(sortedTokens.get(8), fixture.getDataValue(1, 8));
    assertEquals(sortedTokens.get(9), fixture.getDataValue(1, 9));
    assertEquals(sortedTokens.get(10), fixture.getDataValue(1, 10));

    assertEquals(sortedTokens.get(0), fixture.getDataValue(2, 0));
    assertEquals(sortedTokens.get(1), fixture.getDataValue(2, 1));
    assertEquals(sortedTokens.get(2), fixture.getDataValue(2, 2));
    assertEquals(sortedTokens.get(3), fixture.getDataValue(2, 3));
    assertEquals(sortedTokens.get(4), fixture.getDataValue(2, 4));
    assertEquals(sortedTokens.get(5), fixture.getDataValue(2, 5));
    assertEquals(sortedTokens.get(6), fixture.getDataValue(2, 6));
    assertEquals(sortedTokens.get(7), fixture.getDataValue(2, 7));
    assertEquals(sortedTokens.get(8), fixture.getDataValue(2, 8));
    assertEquals(sortedTokens.get(9), fixture.getDataValue(2, 9));
    assertEquals(sortedTokens.get(10), fixture.getDataValue(2, 10));

    SNode span1 = exampleGraph.getNodesByName("IS_span1").get(0);
    SNode span2 = exampleGraph.getNodesByName("IS_span2").get(0);
    assertEquals(span1, fixture.getDataValue(3, 0));
    assertEquals(span2, fixture.getDataValue(3, 4));
    assertEquals(span2, fixture.getDataValue(3, 5));
    assertEquals(span2, fixture.getDataValue(3, 6));
    assertEquals(span2, fixture.getDataValue(3, 7));
    assertEquals(span2, fixture.getDataValue(3, 8));
    assertEquals(span2, fixture.getDataValue(3, 9));
    assertEquals(span2, fixture.getDataValue(3, 10));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueOverlappingExample() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    List<SToken> tokens = overlappingExampleGraph.getSortedTokenByText();

    assertEquals("Overlapping", overlappingExampleGraph.getText(fixture.getDataValue(0, 0)));
    assertEquals(tokens.get(0), fixture.getDataValue(0, 0));
    assertEquals("spans", overlappingExampleGraph.getText(fixture.getDataValue(0, 1)));
    assertEquals(tokens.get(1), fixture.getDataValue(0, 1));
    assertEquals(",", overlappingExampleGraph.getText(fixture.getDataValue(0, 2)));
    assertEquals(tokens.get(2), fixture.getDataValue(0, 2));
    assertEquals("etc", overlappingExampleGraph.getText(fixture.getDataValue(0, 3)));
    assertEquals(tokens.get(3), fixture.getDataValue(0, 3));
    assertEquals(".", overlappingExampleGraph.getText(fixture.getDataValue(0, 4)));
    assertEquals(tokens.get(4), fixture.getDataValue(0, 4));

    // Check that the tokens have the annotation as per column header
    String qualifiedName = fixture.getColumns().get(1).getColumnValue();
    assertEquals(tokens.get(0), fixture.getDataValue(1, 0));
    assertNotNull(fixture.getDataValue(1, 0).getAnnotation(qualifiedName));
    assertNull(fixture.getDataValue(1, 1));
    assertEquals(tokens.get(2), fixture.getDataValue(1, 2));
    assertNotNull(fixture.getDataValue(1, 2).getAnnotation(qualifiedName));
    assertEquals(tokens.get(3), fixture.getDataValue(1, 3));
    assertNotNull(fixture.getDataValue(1, 3).getAnnotation(qualifiedName));
    assertEquals(tokens.get(4), fixture.getDataValue(1, 4));
    assertNotNull(fixture.getDataValue(1, 4).getAnnotation(qualifiedName));

    SNode span1 = overlappingExampleGraph.getNodesByName("sSpan1").get(0);
    SNode span2 = overlappingExampleGraph.getNodesByName("sSpan2").get(0);
    SNode span3 = overlappingExampleGraph.getNodesByName("sSpan3").get(0);
    SNode span4 = overlappingExampleGraph.getNodesByName("sSpan4").get(0);
    SNode span5 = overlappingExampleGraph.getNodesByName("sSpan5").get(0);
    SNode new1 = overlappingExampleGraph.getNodesByName("sSpan6").get(0);
    SNode new2 = overlappingExampleGraph.getNodesByName("sSpan7").get(0);

    assertEquals(span1, fixture.getDataValue(2, 0));
    assertEquals(span2, fixture.getDataValue(2, 1));
    assertEquals(span2, fixture.getDataValue(2, 2));
    assertEquals(span2, fixture.getDataValue(2, 3));
    assertEquals(span2, fixture.getDataValue(2, 4));

    assertEquals(span3, fixture.getDataValue(3, 0));
    assertEquals(span3, fixture.getDataValue(3, 1));
    assertEquals(span4, fixture.getDataValue(3, 2));
    assertEquals(span5, fixture.getDataValue(3, 3));
    assertEquals(span5, fixture.getDataValue(3, 4));

    assertNull(fixture.getDataValue(4, 0));
    assertEquals(new1, fixture.getDataValue(4, 1));
    assertEquals(new1, fixture.getDataValue(4, 2));
    assertEquals(new2, fixture.getDataValue(4, 3));
    assertEquals(new2, fixture.getDataValue(4, 4));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getColumnCount()}.
   */
  @Test
  final void testGetColumnCount() {
    assertEquals(0, fixture.getColumnCount());

    fixture.setGraph(exampleGraph);
    assertEquals(0, fixture.getColumnCount());

    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(4, fixture.getColumnCount());

    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getColumnCount());
  }

  /**
   * Test method for {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#getRowCount()}.
   */
  @Test
  final void testGetRowCount() {
    assertEquals(0, fixture.getRowCount());

    fixture.setGraph(exampleGraph);
    assertEquals(0, fixture.getRowCount());

    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(11, fixture.getRowCount());

    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getRowCount());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#setDataValue(int, int, Object)}.
   * 
   * <p>
   * This tests a successful change of value in an {@link SAnnotation}.
   * </p>
   */
  @Test
  final void testSetDataValue() {
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);

    fixture.setDataValue(2, 2, "test");
    assertEquals("test", fixture.getDataValue(2, 2));
    assertEquals("test",
        exampleGraph.getSortedTokenByText().get(2).getAnnotation("salt::pos").getValue());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.data.GraphDataProvider#setDataValue(int, int, Object)}.
   * 
   * <p>
   * This tests whether an exception is thrown when trying to set a value to a cell that doesn't
   * exist.
   * </p>
   */
  @Test
  final void testSetDataValueThrowsException() {
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);
    fixture.setDataValue(20, 1, "test");
    verify(errorService).handleException(
        matches("Index: 20, Size: 4|Index 20 out of bounds for length 4"),
        isA(IndexOutOfBoundsException.class), eq(GraphDataProvider.class));
  }

  /**
   * Tests whether the correct column types are set during graph resolution.
   */
  @Test
  final void testColumnTypes() {
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);

    assertTrue(fixture.getDataValue(3, 0) instanceof SSpan);
    assertEquals(ColumnType.SPAN_ANNOTATION, fixture.getColumns().get(3).getColumnType());
    assertTrue(fixture.getDataValue(0, 0) instanceof SToken);
    assertEquals(ColumnType.TOKEN_TEXT, fixture.getColumns().get(0).getColumnType());
    assertTrue(fixture.getDataValue(1, 0) instanceof SToken);
    assertEquals(ColumnType.TOKEN_ANNOTATION, fixture.getColumns().get(1).getColumnType());
  }

  @Test
  final void testCreateAnnotationOnEmptySpanCell() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertNull(fixture.getDataValue(4, 0));
    assertNull(fixture.getDataValue(4, 0));
    fixture.setDataValue(4, 0, "test");
    SStructuredNode node = fixture.getDataValue(4, 0);
    assertNotNull(node);
    assertTrue(node instanceof SSpan);
    assertEquals(1, overlappingExampleGraph.getOverlappedTokens(node).size());
    assertEquals(0, overlappingExampleGraph.getSortedTokenByText()
        .indexOf(overlappingExampleGraph.getOverlappedTokens(node).get(0)));
    assertEquals(node, fixture.getDataValue(4, 0));

  }

  @Test
  final void testCreateAnnotationOnEmptyTokenCell() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertNull(fixture.getDataValue(1, 1));
    assertNull(fixture.getDataValue(1, 1));
    fixture.setDataValue(1, 1, "test");
    SStructuredNode node = fixture.getDataValue(1, 1);
    assertNotNull(node);
    assertTrue(node instanceof SToken);
    assertSame(overlappingExampleGraph.getSortedTokenByText().get(1), node);
  }

  @Test
  final void testRemoveAnnotationOnSetToEmpty() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    // Remove single cell span annotation and delete span
    final SStructuredNode originalNodeToRemove1 = fixture.getDataValue(2, 0);
    fixture.setDataValue(2, 0, null);
    assertNull(fixture.getDataValue(2, 0));
    assertNull(overlappingExampleGraph.getNode(originalNodeToRemove1.getId()));

    // Remove data from one span cell in multi-cell annotation and remove span
    final SStructuredNode originalNodeToRemove2 = fixture.getDataValue(2, 1);
    fixture.setDataValue(2, 1, null);
    assertNull(fixture.getDataValue(2, 1));
    assertNull(fixture.getDataValue(2, 2));
    assertNull(fixture.getDataValue(2, 3));
    assertNull(fixture.getDataValue(2, 4));
    assertNull(overlappingExampleGraph.getNode(originalNodeToRemove2.getId()));

    // Remove token annotation but don't remove token
    final SStructuredNode nodeNotToRemove = fixture.getDataValue(1, 3);
    fixture.setDataValue(1, 3, null);
    assertNull(fixture.getDataValue(1, 3));
    assertEquals(nodeNotToRemove, overlappingExampleGraph.getNode(nodeNotToRemove.getId()));
  }

  @Test
  final void testAddNewSpanAnnotationOnEmptyCellWithNamespace() {
    fixture.setGraph(overlappingExampleGraph);
    fixture.setDsAndResolveGraph(overlappingExampleText);

    fixture.setDataValue(4, 0, "ABC");
    // Pick the span that's been created last, i.e., that has none of the known IDs
    SSpan addedSpan = null;
    for (SSpan span : overlappingExampleGraph.getSpans()) {
      List<String> knownIds = Arrays.asList(
          new String[] {"sSpan1", "sSpan2", "sSpan3", "sSpan4", "sSpan5", "sSpan6", "sSpan7"});
      if (!knownIds.contains(span.getId())) {
        addedSpan = span;
      }
    }
    assertNotNull(addedSpan);
    assertEquals("ABC", fixture.getDataValue(4, 0));
    assertEquals(8, overlappingExampleGraph.getSpans().size());
    SAnnotation annotation = addedSpan.getAnnotation("five", "span_2");
    assertNotNull(annotation);
    assertEquals("ABC", annotation.getValue());
  }

  @Test
  final void testAddNewSpanAnnotationOnEmptyCellWithNullNamespace() {
    // Prepare extended example corpus
    exampleGraph.getTextualDSs().get(0).setText(exampleText.getText() + " Yes it is.");
    exampleText = exampleGraph.getTextualDSs().get(0);
    exampleGraph.createToken(exampleText, 56, 59);
    final SToken token = exampleGraph.createToken(exampleText, 60, 62);
    fixture.setGraph(exampleGraph);
    fixture.setDsAndResolveGraph(exampleText);

    fixture.setDataValue(3, 12, "ABC");
    assertEquals("ABC", fixture.getDataValue(3, 12));
    assertEquals(4, exampleGraph.getSpans().size());
    SSpan addedSpan = null;
    for (SSpan span : exampleGraph.getSpans()) {
      List<String> knownIds = Arrays.asList(new String[] {"IS_span1", "IS_span2", "sSpan3"});
      if (!knownIds.contains(span.getId())) {
        addedSpan = span;
      }
    }
    assertNotNull(addedSpan);
    List<SToken> overlappedTokens = exampleGraph.getOverlappedTokens(addedSpan);
    assertEquals(1, overlappedTokens.size());
    assertEquals(token, overlappedTokens.get(0));
    SAnnotation annotation = addedSpan.getAnnotation(null, "Inf-Struct");
    assertNotNull(annotation);
    assertEquals("ABC", annotation.getValue());

  }

  private SDocumentGraph retrieveGraph(String path) {
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

  private STextualDS getFirstTextFromGraph(SDocumentGraph graph) {
    STextualDS text = graph.getTextualDSs().get(0);
    assertNotNull(text);
    return text;
  }

}
