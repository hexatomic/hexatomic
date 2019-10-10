 
package org.corpus_tools.hexatomic.textviewer;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;


public class TextViewer {
	
	@Inject
	public TextViewer() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		Label lblText = new Label(parent, SWT.NONE);
		lblText.setText("Hello World");
		
		
	}
	
}