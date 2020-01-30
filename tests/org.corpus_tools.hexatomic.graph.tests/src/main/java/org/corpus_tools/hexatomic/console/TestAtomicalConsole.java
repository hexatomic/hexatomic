package org.corpus_tools.hexatomic.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.text.source.SourceViewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestAtomicalConsole {

  private AtomicalConsole console;
  private SDocumentGraph graph;
  
  private UISynchronize sync;
  private SourceViewer view;
  
  @BeforeEach
  void setUp() throws Exception {
    
    sync = mock(UISynchronize.class);
    view = mock(SourceViewer.class);
    
    graph = SaltFactory.createSDocumentGraph();
    
    console = new AtomicalConsole(view, sync, graph);
  }

  @Test
  void testTokenize() {
    
    console.executeCommand("t This is an example \".\"");

    assertEquals(5, graph.getTokens());
  }

}
