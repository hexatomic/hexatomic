package org.corpus_tools.hexatomic.grid.internal.data;

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
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SAnnotation;
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
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueEmptyGraph() {
    assertEquals("Please select data source!", fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(1, 1));
    assertEquals(null, fixture.getDataValue(1, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueSetDsWithoutTokens() {
    STextualDS ds = mock(STextualDS.class);
    SDocumentGraph graph = mock(SDocumentGraph.class);
    when(ds.getGraph()).thenReturn(graph);
    fixture.setDsAndResolveGraph(ds);
    assertEquals("Data source contains no tokens!", fixture.getDataValue(0, 0));
    assertEquals(null, fixture.getDataValue(0, 1));
    fixture.getDataValue(1, 1);
    fixture.getDataValue(1, 0);
    verify(errorService, times(2)).handleException(
        matches("Index: 1, Size: 1|Index 1 out of bounds for length 1"),
        isA(IndexOutOfBoundsException.class), eq(GraphDataProvider.class));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueDefaultExample() {
    fixture.setDsAndResolveGraph(exampleText);

    assertEquals("Is", fixture.getDataValue(0, 0));
    assertEquals("this", fixture.getDataValue(0, 1));
    assertEquals("example", fixture.getDataValue(0, 2));
    assertEquals("more", fixture.getDataValue(0, 3));
    assertEquals("complicated", fixture.getDataValue(0, 4));
    assertEquals("than", fixture.getDataValue(0, 5));
    assertEquals("it", fixture.getDataValue(0, 6));
    assertEquals("appears", fixture.getDataValue(0, 7));
    assertEquals("to", fixture.getDataValue(0, 8));
    assertEquals("be", fixture.getDataValue(0, 9));
    assertEquals("?", fixture.getDataValue(0, 10));

    assertEquals("be", fixture.getDataValue(1, 0));
    assertEquals("this", fixture.getDataValue(1, 1));
    assertEquals("example", fixture.getDataValue(1, 2));
    assertEquals("more", fixture.getDataValue(1, 3));
    assertEquals("complicated", fixture.getDataValue(1, 4));
    assertEquals("than", fixture.getDataValue(1, 5));
    assertEquals("it", fixture.getDataValue(1, 6));
    assertEquals("appear", fixture.getDataValue(1, 7));
    assertEquals("to", fixture.getDataValue(1, 8));
    assertEquals("be", fixture.getDataValue(1, 9));
    assertEquals("?", fixture.getDataValue(1, 10));

    assertEquals("VBZ", fixture.getDataValue(2, 0));
    assertEquals("DT", fixture.getDataValue(2, 1));
    assertEquals("NN", fixture.getDataValue(2, 2));
    assertEquals("RBR", fixture.getDataValue(2, 3));
    assertEquals("JJ", fixture.getDataValue(2, 4));
    assertEquals("IN", fixture.getDataValue(2, 5));
    assertEquals("PRP", fixture.getDataValue(2, 6));
    assertEquals("VBZ", fixture.getDataValue(2, 7));
    assertEquals("TO", fixture.getDataValue(2, 8));
    assertEquals("VB", fixture.getDataValue(2, 9));
    assertEquals(".", fixture.getDataValue(2, 10));

    assertEquals("contrast-focus", fixture.getDataValue(3, 0));
    assertEquals("topic", fixture.getDataValue(3, 4));
    assertEquals("topic", fixture.getDataValue(3, 5));
    assertEquals("topic", fixture.getDataValue(3, 6));
    assertEquals("topic", fixture.getDataValue(3, 7));
    assertEquals("topic", fixture.getDataValue(3, 8));
    assertEquals("topic", fixture.getDataValue(3, 9));
    assertEquals("topic", fixture.getDataValue(3, 10));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getDataValue(int, int)}.
   */
  @Test
  final void testGetDataValueOverlappingExample() {
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertEquals("Overlapping", fixture.getDataValue(0, 0));
    assertEquals("spans", fixture.getDataValue(0, 1));
    assertEquals(",", fixture.getDataValue(0, 2));
    assertEquals("etc", fixture.getDataValue(0, 3));
    assertEquals(".", fixture.getDataValue(0, 4));

    assertEquals("six_tok_anno_0", fixture.getDataValue(1, 0));
    assertNull(fixture.getDataValue(1, 1));
    assertEquals("six_tok_anno_2", fixture.getDataValue(1, 2));
    assertEquals("six_tok_anno_3", fixture.getDataValue(1, 3));
    assertEquals("six_tok_anno_4", fixture.getDataValue(1, 4));

    assertEquals("val_span_1", fixture.getDataValue(2, 0));
    assertEquals("val_span_2", fixture.getDataValue(2, 1));
    assertEquals("val_span_2", fixture.getDataValue(2, 2));
    assertEquals("val_span_2", fixture.getDataValue(2, 3));
    assertEquals("val_span_2", fixture.getDataValue(2, 4));

    assertEquals("val_span_3", fixture.getDataValue(3, 0));
    assertEquals("val_span_3", fixture.getDataValue(3, 1));
    assertEquals("val_span_4", fixture.getDataValue(3, 2));
    assertEquals("val_span_5", fixture.getDataValue(3, 3));
    assertEquals("val_span_5", fixture.getDataValue(3, 4));

    assertNull(fixture.getDataValue(4, 0));
    assertEquals("val_span_new1", fixture.getDataValue(4, 1));
    assertEquals("val_span_new1", fixture.getDataValue(4, 2));
    assertEquals("val_span_new2", fixture.getDataValue(4, 3));
    assertEquals("val_span_new2", fixture.getDataValue(4, 4));
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getColumnCount()}.
   */
  @Test
  final void testGetColumnCount() {
    assertEquals(1, fixture.getColumnCount());


    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(4, fixture.getColumnCount());

    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getColumnCount());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#getRowCount()}.
   */
  @Test
  final void testGetRowCount() {
    assertEquals(1, fixture.getRowCount());

    fixture.setDsAndResolveGraph(exampleText);
    assertEquals(11, fixture.getRowCount());

    fixture.setDsAndResolveGraph(overlappingExampleText);
    assertEquals(5, fixture.getRowCount());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#setDataValue(int, int, Object)}.
   * 
   * <p>
   * This tests a successful change of value in an {@link SAnnotation}.
   * </p>
   */
  @Test
  final void testSetDataValue() {
    fixture.setDsAndResolveGraph(exampleText);

    fixture.setDataValue(2, 2, "test");
    assertEquals("test", fixture.getDataValue(2, 2));
    assertEquals("test",
        exampleGraph.getSortedTokenByText().get(2).getAnnotation("salt::pos").getValue());
  }

  /**
   * Test method for
   * {@link org.corpus_tools.hexatomic.grid.internal.data.GraphDataProvider#setDataValue(int, int, Object)}.
   * 
   * <p>
   * This tests whether an exception is thrown when trying to set a value to a cell that doesn't
   * exist.
   * </p>
   */
  @Test
  final void testSetDataValueThrowsException() {
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
    fixture.setDsAndResolveGraph(exampleText);

    assertEquals("contrast-focus", fixture.getDataValue(3, 0));
    assertEquals(ColumnType.SPAN_ANNOTATION, fixture.getColumns().get(3).getColumnType());
    assertEquals("Is", fixture.getDataValue(0, 0));
    assertEquals(ColumnType.TOKEN_TEXT, fixture.getColumns().get(0).getColumnType());
    assertEquals("be", fixture.getDataValue(1, 0));
    assertEquals(ColumnType.TOKEN_ANNOTATION, fixture.getColumns().get(1).getColumnType());
  }

  @Test
  final void testCreateAnnotationOnEmptySpanCell() {
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertNull(fixture.getNode(4, 0));
    assertNull(fixture.getDataValue(4, 0));
    fixture.setDataValue(4, 0, "test");
    SStructuredNode node = fixture.getNode(4, 0);
    assertNotNull(node);
    assertTrue(node instanceof SSpan);
    assertEquals(1, overlappingExampleGraph.getOverlappedTokens(node).size());
    assertEquals(0, overlappingExampleGraph.getSortedTokenByText()
        .indexOf(overlappingExampleGraph.getOverlappedTokens(node).get(0)));
    assertEquals("test", fixture.getDataValue(4, 0));

  }

  @Test
  final void testCreateAnnotationOnEmptyTokenCell() {
    fixture.setDsAndResolveGraph(overlappingExampleText);

    assertNull(fixture.getNode(1, 1));
    assertNull(fixture.getDataValue(1, 1));
    fixture.setDataValue(1, 1, "test");
    SStructuredNode node = fixture.getNode(1, 1);
    assertNotNull(node);
    assertTrue(node instanceof SToken);
    assertSame(overlappingExampleGraph.getSortedTokenByText().get(1), node);
    assertEquals("test", fixture.getDataValue(1, 1));

  }

  @Test
  final void testRemoveAnnotationOnSetToEmpty() {
    fixture.setDsAndResolveGraph(overlappingExampleText);

    // Remove single cell span annotation and delete span
    assertEquals("val_span_1", fixture.getDataValue(2, 0));
    final SStructuredNode originalNodeToRemove1 = fixture.getNode(2, 0);
    fixture.setDataValue(2, 0, null);
    assertNull(fixture.getDataValue(2, 0));
    assertNull(fixture.getNode(2, 0));
    assertNull(overlappingExampleGraph.getNode(originalNodeToRemove1.getId()));

    // Remove data from one span cell in multi-cell annotation and remove span
    assertEquals("val_span_2", fixture.getDataValue(2, 1));
    final SStructuredNode originalNodeToRemove2 = fixture.getNode(2, 1);
    fixture.setDataValue(2, 1, null);
    assertNull(fixture.getDataValue(2, 1));
    assertNull(fixture.getNode(2, 1));
    assertNull(fixture.getDataValue(2, 2));
    assertNull(fixture.getNode(2, 2));
    assertNull(fixture.getDataValue(2, 3));
    assertNull(fixture.getNode(2, 3));
    assertNull(fixture.getDataValue(2, 4));
    assertNull(fixture.getNode(2, 4));
    assertNull(overlappingExampleGraph.getNode(originalNodeToRemove2.getId()));

    // Remove token annotation but don't remove token
    assertEquals("six_tok_anno_3", fixture.getDataValue(1, 3));
    final SStructuredNode nodeNotToRemove = fixture.getNode(1, 3);
    fixture.setDataValue(1, 3, null);
    assertNull(fixture.getDataValue(1, 3));
    assertNull(fixture.getNode(1, 3));
    assertEquals(nodeNotToRemove, overlappingExampleGraph.getNode(nodeNotToRemove.getId()));
  }

  @Test
  final void testAddNewSpanAnnotationOnEmptyCellWithNamespace() {
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
