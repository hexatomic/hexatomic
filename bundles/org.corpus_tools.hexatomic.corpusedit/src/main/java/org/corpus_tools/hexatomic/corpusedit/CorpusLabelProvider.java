package org.corpus_tools.hexatomic.corpusedit;

import org.corpus_tools.salt.core.SNamedElement;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class CorpusLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		String result = null;
		
		if(element instanceof SNamedElement) {
			SNamedElement n = (SNamedElement) element;
			String name = n.getName();
			if(name == null || name.isEmpty()) {
				// Try the ID
				if(element instanceof IdentifiableElement) {
					result = ((IdentifiableElement) element).getId();
				} else {
					result = "<unnamed " + element.getClass().getSimpleName() + ">";
				}
			} else {
				result = n.getName();
			}
		}
		
		if(result == null || result.isEmpty()) {
			result = "<unknown>";
		}
		
		return result;
	}

}
