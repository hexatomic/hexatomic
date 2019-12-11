package org.corpus_tools.hexatomic.console;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class GraphAnnoConsoleViewer extends SourceViewer {
  
  private GraphAnnoConsole console;

  public GraphAnnoConsoleViewer(Composite parent, GraphAnnoConsole console) {
    super(parent, null, SWT.V_SCROLL | SWT.H_SCROLL);
    this.console = console;
    
    StyledText styledText = getTextWidget();
    styledText.setDoubleClickEnabled(true);
    styledText.setEditable(true);
  }

}
