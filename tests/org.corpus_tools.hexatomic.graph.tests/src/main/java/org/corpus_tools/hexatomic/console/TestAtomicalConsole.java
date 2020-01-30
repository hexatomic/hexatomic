package org.corpus_tools.hexatomic.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
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

    IDocument document = mock(IDocument.class);
    StyledText styledText = mock(StyledText.class);

    when(view.getDocument()).thenReturn(document);
    when(view.getTextWidget()).thenReturn(styledText);

    graph = SaltFactory.createSDocumentGraph();

    console = new AtomicalConsole(view, sync, graph);
  }

  @Test
  void testTokenize() {

    // Add initial tokens
    console.executeCommand("t This is an example \".\"");
    graph.sortTokenByText();
    List<SToken> tokens = graph.getTokens();
    assertEquals(5, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This is an example .", graph.getTextualDSs().get(0).getText());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("is", graph.getText(tokens.get(1)));
    assertEquals("an", graph.getText(tokens.get(2)));
    assertEquals("example", graph.getText(tokens.get(3)));
    assertEquals(".", graph.getText(tokens.get(4)));
    
    // Append two tokens
    console.executeCommand("t Not .");
    graph.sortTokenByText();
    tokens = graph.getTokens();
    assertEquals(7, tokens.size());
    assertEquals(1, graph.getTextualDSs().size());
    assertEquals("This is an example . Not .", graph.getTextualDSs().get(0).getText());
    assertEquals("This", graph.getText(tokens.get(0)));
    assertEquals("is", graph.getText(tokens.get(1)));
    assertEquals("an", graph.getText(tokens.get(2)));
    assertEquals("example", graph.getText(tokens.get(3)));
    assertEquals(".", graph.getText(tokens.get(4)));
    assertEquals("Not", graph.getText(tokens.get(5)));
    assertEquals(".", graph.getText(tokens.get(6)));
    
  }

}
