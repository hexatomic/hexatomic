package org.corpus_tools.hexatomic.corpusedit.dnd;

import org.corpus_tools.hexatomic.corpusedit.CorpusStructureView;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class SaltObjectTreeDropTarget extends ViewerDropAdapter {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SaltObjectTreeDropTarget.class);

	CorpusStructureView corpusView;
	
	public SaltObjectTreeDropTarget(CorpusStructureView corpusView, TreeViewer treeViewer) {
		super(treeViewer);
		this.corpusView = corpusView;
	}

	@Override
	public boolean performDrop(Object data) {
		
		SCorpus newParent = (SCorpus) getCurrentTarget();
		log.info("Trying to drop {} into", data, newParent.getId());
		
		// get the document which is referred by the dropped ID
		String docID = (String) data;
		SNode doc = newParent.getGraph().getNode(docID);
		if(doc instanceof SDocument) {
			newParent.getGraph().removeNode(doc);
			newParent.getGraph().addDocument(newParent, (SDocument) doc);
			// select the new document
			corpusView.selectSaltObject(doc, true);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {	
		
		if(target instanceof SCorpus) {
			return true;
		} else {
			return false;
		}
		
	}
	
}