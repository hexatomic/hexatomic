
package org.corpus_tools.hexatomic.textviewer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TextViewer {

	@Inject
	public TextViewer() {

	}

	@PostConstruct
	public void postConstruct(Composite parent, MPart part) {
		
		String documentID = part.getPersistedState().get("org.corpus_tools.hexatomic.document-id");

		Label lblText = new Label(parent, SWT.NONE);
		lblText.setText("Hello " + documentID);

	}

}