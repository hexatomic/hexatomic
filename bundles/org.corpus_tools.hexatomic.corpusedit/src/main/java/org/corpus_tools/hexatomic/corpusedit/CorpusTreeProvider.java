package org.corpus_tools.hexatomic.corpusedit;

import java.util.List;

import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SaltProject;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class CorpusTreeProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SCorpusGraph) {
			SCorpusGraph g = (SCorpusGraph) inputElement;
			return g.getRoots().toArray();
		} else if (inputElement.getClass().isArray()) {
			return (Object[]) inputElement;
		} else if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		} else if (inputElement instanceof SaltProject) {
			SaltProject p = (SaltProject) inputElement;
			return p.getCorpusGraphs().toArray();
		} else if (inputElement instanceof SNode) {
			SNode n = (SNode) inputElement;

			return new SNode[] { n };
		} else {
			return null;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof SNode) {
			SNode n = (SNode) parentElement;

			List<SNode> children = n.getGraph().getChildren(n, null);
			return children.toArray();

		} else if (parentElement instanceof SCorpusGraph) {
			SCorpusGraph g = (SCorpusGraph) parentElement;
			List<SNode> roots = g.getRoots();
			return roots.toArray();
		} else if (parentElement instanceof SaltProject) {
			SaltProject p = (SaltProject) parentElement;
			List<SCorpusGraph> graphs = p.getCorpusGraphs();
			return graphs.toArray();
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SCorpusGraph) {
			return !((SCorpusGraph) element).getRoots().isEmpty();
		} else if (element instanceof SaltProject) {
			SaltProject p = (SaltProject) element;
			return !p.getCorpusGraphs().isEmpty();
		} else if (element instanceof SNode) {
			SNode n = (SNode) element;

			List<SNode> children = n.getGraph().getChildren(n, null);
			return !children.isEmpty();
		} else {
			return false;
		}
	}

}
