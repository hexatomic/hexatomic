
package org.corpus_tools.hexatomic.textviewer;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextViewer {


	@Inject
	private ProjectManager projectManager;
	
	@Inject
	public TextViewer() {

	}
	

	@PostConstruct
	public void postConstruct(Composite parent, MPart part) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// Get the document graph for this editor
		String documentID = part.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
		Optional<SDocument> document = projectManager.getDocument(documentID);
		
		if(document.isPresent()) {
			SDocumentGraph graph = document.get().getDocumentGraph();
			for(STextualDS text : graph.getTextualDSs()) {
				Text textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
				textField.setText(text.getText());
			}
		}

	}

}