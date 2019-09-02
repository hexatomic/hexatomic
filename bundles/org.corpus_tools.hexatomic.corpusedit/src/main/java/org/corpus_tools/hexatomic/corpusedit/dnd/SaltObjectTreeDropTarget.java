package org.corpus_tools.hexatomic.corpusedit.dnd;

import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class SaltObjectTreeDropTarget extends ViewerDropAdapter {

	/**
	 * 
	 */
	private final TreeViewer treeViewer;
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SaltObjectTreeDropTarget.class);

	public SaltObjectTreeDropTarget(TreeViewer treeViewer) {
		super(treeViewer);
		this.treeViewer = treeViewer;
	}

	@Override
	public boolean performDrop(Object data) {
		
		SCorpus newParent = (SCorpus) getCurrentTarget();
		log.info("Trying to drop {} into", data, newParent.getId());
		
		// get the document which is referred by the dropped ID
		String docID = (String) data;
		SNode doc = newParent.getGraph().getNode(docID);
		if(doc instanceof SDocument) {
			// this will automatically remove the document from the original parent
			newParent.getGraph().addDocument(newParent, (SDocument) doc);
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