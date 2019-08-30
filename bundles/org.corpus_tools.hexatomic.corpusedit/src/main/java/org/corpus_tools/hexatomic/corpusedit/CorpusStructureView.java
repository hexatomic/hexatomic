package org.corpus_tools.hexatomic.corpusedit;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNamedElement;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;

public class CorpusStructureView {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CorpusStructureView.ChildNameFilter.class);

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

		Composite compositeFilter = new Composite(parent, SWT.NONE);
		compositeFilter.setLayout(new GridLayout(2, false));
		compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Label lblNewLabel = new Label(compositeFilter, SWT.NONE);
		lblNewLabel.setText("Filter:");

		txtFilter = new Text(compositeFilter, SWT.BORDER);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFilter.setToolTipText("Type to filter for corpus/document name");

		txtFilter.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				treeViewer.refresh();

			}
		});

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.minimumWidth = 300;
		gd_composite.minimumHeight = 200;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new TreeColumnLayout());

		CorpusLabelProvider labelProvider = new CorpusLabelProvider();

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		treeViewer.setColumnProperties(new String[] { "name" });
		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(tree) });
		treeViewer.setCellModifier(new ICellModifier() {

			@Override
			public void modify(Object element, String property, Object value) {
				if (element instanceof TreeItem) {
					TreeItem item = (TreeItem) element;
					if (item.getData() instanceof SNamedElement) {
						SNamedElement n = (SNamedElement) item.getData();
						n.setName(value.toString());
					}
				}

			}

			@Override
			public Object getValue(Object element, String property) {
				return labelProvider.getText(element);
			}

			@Override
			public boolean canModify(Object element, String property) {
				return true;
			}
		});
		tree.setLinesVisible(true);
		treeViewer.setLabelProvider(labelProvider);

		TreeViewerEditor.create(treeViewer, new ColumnViewerEditorActivationStrategy(treeViewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
			}
		}, ColumnViewerEditor.DEFAULT);

		Composite composite_tools = new Composite(parent, SWT.NONE);
		composite_tools.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite_tools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		ToolBar toolBar = new ToolBar(composite_tools, SWT.FLAT | SWT.RIGHT);

		createAddMenu(toolBar);
		createDeleteMenu(toolBar);

		treeViewer.setContentProvider(new CorpusTreeProvider());
		treeViewer.setFilters(new ChildNameFilter());

		treeViewer.setInput(projectManager.getProject().getCorpusGraphs());

	}

	private void createAddMenu(ToolBar toolBar) {
		ToolItem tltmAdd = new ToolItem(toolBar, SWT.DROP_DOWN);
		tltmAdd.setImage(
				ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core", "icons/fontawesome/plus-solid.png"));
		tltmAdd.setText("Add");
		Menu addMenu = new Menu(tltmAdd.getParent().getShell());
		tltmAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					Rectangle rect = tltmAdd.getBounds();
					Point pt = tltmAdd.getParent().toDisplay(new Point(rect.x, rect.y));
					addMenu.setLocation(pt);
					addMenu.setVisible(true);
				} else {
					// trigger a default action based on the currently selected tree item
					TreeSelection selected = (TreeSelection) treeViewer.getSelection();
					if(selected.getFirstElement() instanceof SCorpus) {
						addDocument(toolBar.getShell());
					} else if(selected.getFirstElement() instanceof SCorpusGraph) {
						addCorpus(toolBar.getShell());
					} else {
						// fallback to a corpus graph, which always can be added
						addCorpusGraph();
					}
				}
			}
		});

		MenuItem addCorpusGraph = new MenuItem(addMenu, SWT.NONE);
		addCorpusGraph.setText("Corpus Graph");
		addCorpusGraph.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/project-diagram-solid.png"));
		addCorpusGraph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCorpusGraph();
			}
		});

		MenuItem addCorpus = new MenuItem(addMenu, SWT.NONE);
		addCorpus.setText("(Sub-) Corpus");
		addCorpus.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/box-open-solid.png"));
		addCorpus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCorpus(toolBar.getShell());
			}
		});

		MenuItem addDocument = new MenuItem(addMenu, SWT.NONE);
		addDocument.setText("Document");
		addDocument.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/file-alt-regular.png"));
		addDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addDocument(toolBar.getShell());
			}
		});

	}

	private void addCorpusGraph() {
		SCorpusGraph newGraph = projectManager.getProject().createCorpusGraph();
		newGraph.setName("new_corpus_graph");
	}

	private void addCorpus(Shell shell) {
		// get the selected corpus graph
		TreeSelection selection = (TreeSelection) treeViewer.getSelection();
		if (selection.getFirstElement() instanceof SCorpusGraph) {
			SCorpusGraph g = (SCorpusGraph) selection.getFirstElement();
			g.createCorpus(null, "new_corpus");
		} else if (selection.getFirstElement() instanceof SCorpus) {
			SCorpus parent = (SCorpus) selection.getFirstElement();
			parent.getGraph().createCorpus(parent, "new_corpus");
		} else {
			ErrorDialog.openError(shell, "Error when adding (sub-) corpus",
					"You can only create a (sub-) corpus when a corpus graph or another corpus is selected",
					new Status(Status.ERROR, "unknown", null));
		}
	}

	private void addDocument(Shell shell) {
		// get the selected corpus graph
		TreeSelection selection = (TreeSelection) treeViewer.getSelection();
		if (selection.getFirstElement() instanceof SCorpus) {
			SCorpus parent = (SCorpus) selection.getFirstElement();
			parent.getGraph().createDocument(parent, "new_document");
		} else {
			ErrorDialog.openError(shell, "Error when adding document",
					"You can only create a document when a corpus is selected",
					new Status(Status.ERROR, "unknown", null));
		}
	}

	private void createDeleteMenu(ToolBar toolBar) {
		ToolItem tltmDelete = new ToolItem(toolBar, SWT.NONE);
		tltmDelete.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/trash-alt-regular.png"));
		tltmDelete.setText("Delete");
	}

	@Inject
	@Optional
	private void subscribeProjectChanged(@UIEventTopic(ProjectManager.TOPIC_PROJECT_CHANGED) String path) {
		if (treeViewer != null) {
			treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
		}

	}
}
