package org.corpus_tools.hexatomic.corpusedit;

import javax.annotation.PostConstruct;

import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public class CorpusStructureView {
	
	private Text txtFilter;

	@PostConstruct
	public void createPartControl(Composite parent) {
		System.out.println("Enter in SampleE4View postConstruct");
		parent.setLayout(new GridLayout(1, false));
		
		txtFilter = new Text(parent, SWT.BORDER);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtFilter.setToolTipText("Type to filter for corpus/document name");
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd_composite = new 	GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.minimumWidth = 300;
		gd_composite.minimumHeight = 200;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new TreeColumnLayout());
		
		TreeViewer treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		new Label(parent, SWT.NONE);
		treeViewer.setContentProvider(new CorpusTreeProvider());
		
		SCorpusGraph g = SampleGenerator.createCorpusStructure();
		
		treeViewer.setInput(g);
		

	}

}
