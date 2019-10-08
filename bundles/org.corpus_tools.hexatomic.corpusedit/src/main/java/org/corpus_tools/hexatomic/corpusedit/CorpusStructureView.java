package org.corpus_tools.hexatomic.corpusedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.corpusedit.dnd.SaltObjectTreeDragSource;
import org.corpus_tools.hexatomic.corpusedit.dnd.SaltObjectTreeDropTarget;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SCorpusDocumentRelation;
import org.corpus_tools.salt.common.SCorpusGraph;
import org.corpus_tools.salt.common.SCorpusRelation;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SGraph;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNamedElement;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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

	static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CorpusStructureView.class);

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

	TreeViewer treeViewer;

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
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, new SaltObjectTreeDragSource(treeViewer));
		treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, new SaltObjectTreeDropTarget(this, treeViewer));

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
		ToolItem addToolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		addToolItem.setImage(
				ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core", "icons/fontawesome/plus-solid.png"));
		addToolItem.setText("Add");
		Menu addMenu = new Menu(addToolItem.getParent().getShell());
		addToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					Rectangle rect = addToolItem.getBounds();
					Point pt = addToolItem.getParent().toDisplay(new Point(rect.x, rect.y));
					addMenu.setLocation(pt);
					addMenu.setVisible(true);
				} else {
					// trigger a default action based on the currently selected tree item
					StructuredSelection selected = (StructuredSelection) treeViewer.getSelection();
					if (selected.getFirstElement() instanceof SCorpus) {
						addDocument(toolBar.getShell());
					} else if (selected.getFirstElement() instanceof SCorpusGraph) {
						addCorpus(toolBar.getShell());
					} else if (selected.getFirstElement() instanceof SDocument) {
						// add a sibling document
						addDocument(toolBar.getShell());
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
				"icons/fontawesome/folder-regular.png"));
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

		int oldSize = projectManager.getProject().getCorpusGraphs() == null ? 0
				: projectManager.getProject().getCorpusGraphs().size();

		SCorpusGraph newGraph = projectManager.getProject().createCorpusGraph();
		newGraph.setName("corpus_graph_" + (oldSize + 1));

		selectSaltObject(newGraph, true);

	}

	private void addCorpus(Shell shell) {

		SCorpusGraph g = null;
		SCorpus parent = null;

		// get the selected corpus graph
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
		if (selection.getFirstElement() instanceof SCorpusGraph) {
			g = (SCorpusGraph) selection.getFirstElement();

		} else if (selection.getFirstElement() instanceof SCorpus) {
			parent = (SCorpus) selection.getFirstElement();
			g = parent.getGraph();

		} else {
			ErrorDialog.openError(shell, "Error when adding (sub-) corpus",
					"You can only create a (sub-) corpus when a corpus graph or another corpus is selected",
					new Status(Status.ERROR, "unknown", "Constraint in the Salt data model.")); // TODO Externalize
																								// string
			return;
		}

		if (g != null) {

			int oldSize = g.getCorpora() == null ? 0 : g.getCorpora().size();
			SCorpus newCorpus = g.createCorpus(parent, "corpus_" + (oldSize + 1));
			if (newCorpus != null) {
				selectSaltObject(newCorpus, true);
			}
		}

	}

	private void addDocument(Shell shell) {

		// get the selected corpus graph
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();

		SCorpus parent = null;

		if (selection.getFirstElement() instanceof SCorpus) {
			parent = (SCorpus) selection.getFirstElement();
		} else if (selection.getFirstElement() instanceof SDocument) {
			// create a sibling document with the same parent (sub-) corpus
			SDocument sibling = (SDocument) selection.getFirstElement();
			parent = sibling.getGraph().getCorpus(sibling);
		} else {
			ErrorDialog.openError(shell, "Error when adding document",
					"You can only create a document when a corpus or a sibling document is selected",
					new Status(Status.ERROR, "unknown", null));
			return;
		}

		if (parent != null) {
			int oldSize = parent.getGraph().getDocuments() == null ? 0 : parent.getGraph().getDocuments().size();
			SDocument newDocument = parent.getGraph().createDocument(parent, "document_" + (oldSize + 1));
			log.debug("Selecting created document");
			selectSaltObject(newDocument, true);
		}

	}

	private void createDeleteMenu(ToolBar toolBar) {
		ToolItem deleteToolItem = new ToolItem(toolBar, SWT.NONE);
		deleteToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
				if (selection != null) {
					if (selection.getFirstElement() instanceof SCorpusGraph) {
						projectManager.getProject().removeCorpusGraph((SCorpusGraph) selection.getFirstElement());
						// select nothing
						treeViewer.setSelection(null);
						treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
					} else if (selection.getFirstElement() instanceof SCorpus) {
						SCorpus n = (SCorpus) selection.getFirstElement();

						boolean hasChildren = n.getOutRelations().stream().filter(
								(rel) -> rel instanceof SCorpusRelation || rel instanceof SCorpusDocumentRelation)
								.findAny().isPresent();
						if (hasChildren) {
							ErrorDialog.openError(toolBar.getShell(), "Error when deleting (sub-) corpus",
									"Before deleting a (sub-) corpus, first delete all its child elements.",
									new Status(Status.ERROR, "unknown", "The selected (sub-) corpus has child elements"));
							return;
						}

						Optional<SNode> parent = n.getInRelations().stream()
								.filter((rel) -> rel instanceof SCorpusRelation).findFirst()
								.map(rel -> ((SCorpusRelation) rel).getSource());
						if (parent.isPresent()) {
							// select parent corpus
							selectSaltObject(parent.get(), true);
						} else {
							// use the corpus graph
							selectSaltObject(n.getGraph(), true);
						}

						n.getGraph().removeNode(n);

						treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
					} else if (selection.getFirstElement() instanceof SDocument) {
						SDocument n = (SDocument) selection.getFirstElement();

						Optional<SNode> parent = n.getInRelations().stream()
								.filter((rel) -> rel instanceof SCorpusDocumentRelation).findFirst()
								.map(rel -> ((SCorpusDocumentRelation) rel).getSource());
						if (parent.isPresent()) {
							// select parent corpus
							selectSaltObject(parent.get(), true);
						} else {
							// use the corpus graph
							selectSaltObject(n.getGraph(), true);
						}

						n.getGraph().removeNode(n);
						treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
					}
				}
			}
		});
		deleteToolItem.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
				"icons/fontawesome/trash-alt-regular.png"));
		deleteToolItem.setText("Delete");
	}

	public void selectSaltObject(SNamedElement object, boolean reveal) {
		if (object instanceof SGraph) {
			// graphs are top-level, just select the matching root element
			treeViewer.setSelection(new StructuredSelection(object), reveal);
		} else if (object instanceof SNode) {
			SNode n = (SNode) object;

			// try to get all parents of this node
			final LinkedList<Object> chain = new LinkedList<>();

			n.getGraph().traverse(Arrays.asList(n), GRAPH_TRAVERSE_TYPE.BOTTOM_UP_DEPTH_FIRST, "parents",
					new GraphTraverseHandler() {

						@Override
						public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
								SRelation<SNode, SNode> relation, SNode fromNode, long order) {

							chain.add(currNode);
						}

						@Override
						public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
								SRelation<SNode, SNode> relation, SNode fromNode, long order) {

						}

						@Override
						public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
								SRelation<SNode, SNode> relation, SNode currNode, long order) {
							return true;
						}
					});

			// add the corpus graph
			chain.add(n.getGraph());

			Collections.reverse(chain);
			TreePath path = new TreePath(chain.toArray());
			treeViewer.setSelection(new TreeSelection(path), reveal);

		}
	}

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private void subscribeProjectChanged(@UIEventTopic(ProjectManager.TOPIC_CORPUS_STRUCTURE_CHANGED) String path) {
		log.debug("Corpus Structure Viewer received update");
		if (treeViewer != null) {
			treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
			log.debug("Corpus Structure Viewer udpated");
		}

	}
}
