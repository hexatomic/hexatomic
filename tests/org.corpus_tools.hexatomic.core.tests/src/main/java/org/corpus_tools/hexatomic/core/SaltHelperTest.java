package org.corpus_tools.hexatomic.core;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.corpus_tools.hexatomic.core.events.salt.NodeNotifierImpl;
import org.corpus_tools.hexatomic.core.events.salt.SaltNotificationFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SProcessingAnnotation;
import org.corpus_tools.salt.graph.Node;
import org.corpus_tools.salt.graph.impl.NodeImpl;
import org.corpus_tools.salt.impl.SaltFactoryImpl;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaltHelperTest {

  @BeforeEach
  public void setUp() throws Exception {

    // Use our notifying Salt factory
    SaltFactory.setFactory(new SaltNotificationFactory(mock(IEventBroker.class),
        mock(ProjectManager.class), mock(UISynchronize.class)));
  }

  /**
   * Create a document graph with complex labels and resolve the graph for the elements.
   */
  @Test
  void testGetGraphForObject() {
    final SDocument doc = SaltFactory.createSDocument();
    SampleGenerator.createPrimaryData(doc);
    SampleGenerator.createTokens(doc);
    
    final SDocumentGraph graph = doc.getDocumentGraph();
    final SProcessingAnnotation graphAnno =
        graph.createProcessingAnnotation("process", "the", "graph");

    final SToken token = graph.getTokens().get(0);
    final SAnnotation anno = token.createAnnotation("some", "name", "test");
    final SAnnotation nestedAnno = anno.createAnnotation("nested", "anno", "value");

    final STextualRelation textRel = graph.getTextualRelations().get(0);



    assertTrue(SaltHelper.getGraphForObject(graph).isPresent());
    assertTrue(SaltHelper.getGraphForObject(token).isPresent());
    assertTrue(SaltHelper.getGraphForObject(anno).isPresent());
    assertTrue(SaltHelper.getGraphForObject(nestedAnno).isPresent());
    assertTrue(SaltHelper.getGraphForObject(textRel).isPresent());
    assertTrue(SaltHelper.getGraphForObject(graphAnno).isPresent());

    assertEquals(graph, SaltHelper.getGraphForObject(graph).get());
    assertEquals(graph, SaltHelper.getGraphForObject(token).get());
    assertEquals(graph, SaltHelper.getGraphForObject(anno).get());
    assertEquals(graph, SaltHelper.getGraphForObject(nestedAnno).get());
    assertEquals(graph, SaltHelper.getGraphForObject(textRel).get());
    assertEquals(graph, SaltHelper.getGraphForObject(graphAnno).get());
    assertFalse(SaltHelper.getGraphForObject(doc).isPresent());
    assertFalse(SaltHelper.getGraphForObject(null).isPresent());
  }

  /**
   * Create a chain of delegated nodes.
   */
  @Test
  void testResolveDelegation() {
    SaltFactory.setFactory(new SaltFactoryImpl());
    final NodeNotifierImpl delegate = new NodeNotifierImpl(null);
    final Node actualNode = new NodeImpl(delegate);
    delegate.setOwner(actualNode);

    assertEquals(null, SaltHelper.resolveDelegation(null));
    assertEquals(actualNode, SaltHelper.resolveDelegation(actualNode));
    assertEquals(actualNode, SaltHelper.resolveDelegation(delegate));
  }
}
