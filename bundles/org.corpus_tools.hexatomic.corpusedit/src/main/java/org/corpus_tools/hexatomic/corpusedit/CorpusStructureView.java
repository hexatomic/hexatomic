package org.corpus_tools.hexatomic.corpusedit;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.ResourceManager;

public class CorpusStructureView {

	private final class ChildNameFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (txtFilter.isEnabled()) {
				String filterText = txtFilter.getText().trim().toLowerCase();
				if (!filterText.isEmpty() && element instanceof SNode) {

					// Check if element or any of its children has a matching name.
					// If we exclude parents based on their name only, their children won't be
					// included.
					final AtomicBoolean found = new AtomicBoolean(false);

					SNode n = (SNode) element;
					n.getGraph().traverse(Arrays.asList(n), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "filter",
							new GraphTraverseHandler() {

								@Override
								public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
										SNode currNode, SRelation<SNode, SNode> relation, SNode fromNode, long order) {
									if (currNode.getName() != null) {
										if (currNode.getName().toLowerCase().contains(filterText)) {
											found.set(true);
										}
									}

								}

								@Override
								public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
										SNode currNode, SRelation<SNode, SNode> relation, SNode fromNode, long order) {

								}

								@Override
								public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
										SRelation<SNode, SNode> relation, SNode currNode, long order) {
									// search as long no valid child was found
									return !found.get();
								}
							});

					return found.get();
				}
			}
			return true;
		}
	}

	private Text txtFilter;

	@Inject
	private ProjectManager projectManager;

	private TreeViewer treeViewer;

	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		txtFilter = new Text(parent, SWT.BORDER);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtFilter.setToolTipText("Type to filter for corpus/document name");

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.minimumWidth = 300;
		gd_composite.minimumHeight = 200;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new TreeColumnLayout());

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		treeViewer.setLabelProvider(new CorpusLabelProvider());

		Composite composite_tools = new Composite(parent, SWT.NONE);
		composite_tools.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite_tools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		ToolBar toolBar = new ToolBar(composite_tools, SWT.FLAT | SWT.RIGHT);

		ToolItem tltmAdd = new ToolItem(toolBar, SWT.DROP_DOWN);
		
		Menu addMenu = new Menu(tltmAdd.getParent().getShell());
		MenuItem addCorpusGraph = new MenuItem(addMenu, SWT.NONE);
		addCorpusGraph.setText("Corpus Graph");
		addCorpusGraph.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core", "icons/fontawesome/project-diagram-solid.png"));

		
		tltmAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					Rectangle rect = tltmAdd.getBounds();
					Point pt = tltmAdd.getParent().toDisplay(new Point(rect.x, rect.y));
					addMenu.setLocation(pt);
					addMenu.setVisible(true);
				}
			}
		});
		tltmAdd.setImage(
				ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core", "icons/fontawesome/plus-solid.png"));
		tltmAdd.setText("Add");

		ToolItem tltmDelete = new ToolItem(toolBar, SWT.NONE);
		tltmDelete.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/trash-alt-regular.png"));
		tltmDelete.setText("Delete");
		treeViewer.setContentProvider(new CorpusTreeProvider());
		treeViewer.setFilters(new ChildNameFilter());

		txtFilter.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				treeViewer.refresh();

			}
		});

		treeViewer.setInput(projectManager.getProject().getCorpusGraphs());

	}

	@Inject
	@Optional
	private void subscribeProjectChanged(@UIEventTopic(ProjectManager.TOPIC_PROJECT_CHANGED) String path) {
		if (treeViewer != null) {
			treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
		}

	}
}
