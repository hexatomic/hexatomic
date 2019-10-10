
package org.corpus_tools.hexatomic.core.handlers;

import java.util.Optional;

import javax.inject.Named;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SDocument;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class OpenSaltDocumentHandler {

	public static final String DOCUMENT_ID = "org.corpus_tools.hexatomic.document-id";

	@Execute
	public void execute(ProjectManager projectManager, EModelService modelService,
			EPartService partService,
			@org.eclipse.e4.core.di.annotations.Optional @Named(DOCUMENT_ID) String documentID) {

		if (documentID == null) {
			return;
		}

		Optional<SDocument> document = projectManager.getDocument(documentID);

		if (document.isPresent()) {
			if(document.get().getDocumentGraph() == null) {
				// TODO: show progress indicator
				document.get().loadDocumentGraph();
			}

			// Create a new part from an editor part descriptor
			// TODO: choose which part to use from a parameter
			MPart editorPart = partService.createPart("org.corpus_tools.hexatomic.textviewer");
			editorPart.setLabel(document.get().getName());
			editorPart.getPersistedState().put(OpenSaltDocumentHandler.DOCUMENT_ID, document.get().getId());

			partService.showPart(editorPart, PartState.ACTIVATE);

		}

	}

}