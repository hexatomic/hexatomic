/*-
 * #%L
 * org.corpus_tools.hexatomic.corpusstructureeditor
 * %%
 * Copyright (C) 2018 - 2019 Stephan Druskat, Thomas Krause
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.corpus_tools.hexatomic.corpusedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.CommandParams;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
import org.corpus_tools.hexatomic.corpusedit.dnd.SaltObjectTreeDragSource;
import org.corpus_tools.hexatomic.corpusedit.dnd.SaltObjectTreeDropTarget;
import org.corpus_tools.salt.SaltFactory;
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
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

  private static final String ORG_ECLIPSE_SWTBOT_WIDGET_KEY = "org.eclipse.swtbot.widget.key";

  private static final String OPEN_WITH_PREFIX = "Open with ";


  private static final String OPEN_DOCUMENT_POPUP_MENU_ID =
      "org.corpus_tools.hexatomic.corpusedit.popupmenu.documents";
  private static final String ERROR_WHEN_DELETING_SUB_CORPUS_TITLE =
      "Error when deleting (sub-) corpus";
  private static final String ERROR_WHEN_DELETING_SUB_CORPUS_MSG =
      "Before deleting a (sub-) corpus, first delete all its child elements.";
  private static final String ERROR_WHEN_ADDING_DOCUMENT_TITLE = "Error when adding document";
  private static final String ERROR_WHEN_ADDING_DOCUMENT_MSG =
      "You can only create a document when a corpus or a sibling document is selected.";
  private static final String ERROR_WHEN_ADDING_SUBCORPUS_TITLE = "Error when adding (sub-) corpus";
  private static final String ERROR_WHEN_ADDING_SUBCORPUS_MSG =
      "You can only create a (sub-) corpus when a corpus graph or another corpus is selected.";

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
          n.getGraph().traverse(Arrays.asList(n), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST,
              "filter", new GraphTraverseHandler() {

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
                public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType,
                    String traversalId, SRelation<SNode, SNode> relation, SNode currNode,
                    long order) {
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

  @Inject
  private ErrorService errorService;

  TreeViewer treeViewer;

  @PostConstruct
  private void createPartControl(Composite parent, EMenuService menuService,
      ESelectionService selectionService, EModelService modelService, MApplication application,
      MPart thisPart) {
    parent.setLayout(new GridLayout(1, false));

    Composite compositeFilter = new Composite(parent, SWT.NONE);
    compositeFilter.setLayout(new GridLayout(2, false));
    compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

    Label lblNewLabel = new Label(compositeFilter, SWT.NONE);
    lblNewLabel.setText("Filter:");

    txtFilter = new Text(compositeFilter, SWT.BORDER);
    // Assign an ID to the text field
    txtFilter.setData(ORG_ECLIPSE_SWTBOT_WIDGET_KEY, "filter");
    txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtFilter.setToolTipText("Type to filter for corpus/document name");

    txtFilter.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        treeViewer.refresh();

      }
    });

    Composite composite = new Composite(parent, SWT.NONE);
    GridData gridComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
    gridComposite.minimumWidth = 300;
    gridComposite.minimumHeight = 200;
    composite.setLayoutData(gridComposite);
    composite.setLayout(new TreeColumnLayout());

    treeViewer = new TreeViewer(composite, SWT.BORDER);
    Tree tree = treeViewer.getTree();
    treeViewer.setColumnProperties(new String[] {"name"});
    treeViewer.setCellEditors(new CellEditor[] {new TextCellEditor(tree)});

    CorpusLabelProvider labelProvider = new CorpusLabelProvider();
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
    menuService.registerContextMenu(treeViewer.getControl(), OPEN_DOCUMENT_POPUP_MENU_ID);

    treeViewer.setLabelProvider(labelProvider);
    Transfer[] transferTypes = new Transfer[] {TextTransfer.getInstance()};
    treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes,
        new SaltObjectTreeDragSource(treeViewer));
    treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes,
        new SaltObjectTreeDropTarget(this, treeViewer));

    TreeViewerEditor.create(treeViewer, new ColumnViewerEditorActivationStrategy(treeViewer) {
      @Override
      protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
        return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
      }
    }, ColumnViewerEditor.DEFAULT);

    Composite compositeTools = new Composite(parent, SWT.NONE);
    compositeTools.setLayout(new RowLayout(SWT.HORIZONTAL));
    compositeTools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

    ToolBar toolBar = new ToolBar(compositeTools, SWT.FLAT | SWT.RIGHT);

    createAddMenu(toolBar);
    createDeleteMenu(toolBar);

    treeViewer.setContentProvider(new CorpusTreeProvider());
    treeViewer.setFilters(new ChildNameFilter());
    treeViewer.setInput(projectManager.getProject().getCorpusGraphs());

    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = treeViewer.getStructuredSelection();
        selectionService.setSelection(selection.getFirstElement());
      }
    });

    registerEditors(modelService, application, thisPart);
  }

  private void createAddMenu(ToolBar toolBar) {
    ToolItem addToolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
    addToolItem.setImage(ResourceManager.getPluginImage("org.corpus_tools.hexatomic.core",
        "icons/fontawesome/plus-solid.png"));
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
      errorService.showError(ERROR_WHEN_ADDING_SUBCORPUS_TITLE, ERROR_WHEN_ADDING_SUBCORPUS_MSG,
          this.getClass());
      return;
    }

    if (g != null) {

      int oldSize = g.getCorpora() == null ? 0 : g.getCorpora().size();
      String newCorpusName = "corpus_" + (oldSize + 1);
      SCorpus newCorpus;
      if (parent == null) {
        newCorpus = SaltFactory.createSCorpus();
        newCorpus.setName(newCorpusName);
        g.addNode(newCorpus);
      } else {
        newCorpus = g.createCorpus(parent, newCorpusName);
      }
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
      errorService.showError(ERROR_WHEN_ADDING_DOCUMENT_TITLE, ERROR_WHEN_ADDING_DOCUMENT_MSG,
          this.getClass());
      return;
    }

    if (parent != null) {
      int oldSize =
          parent.getGraph().getDocuments() == null ? 0 : parent.getGraph().getDocuments().size();
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
            projectManager.getProject()
                .removeCorpusGraph((SCorpusGraph) selection.getFirstElement());
            // select nothing
            treeViewer.setSelection(null);
            treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
          } else if (selection.getFirstElement() instanceof SCorpus) {
            SCorpus n = (SCorpus) selection.getFirstElement();

            boolean hasChildren = n.getOutRelations().stream().filter(
                (rel) -> rel instanceof SCorpusRelation || rel instanceof SCorpusDocumentRelation)
                .findAny().isPresent();
            if (hasChildren) {
              errorService.showError(ERROR_WHEN_DELETING_SUB_CORPUS_TITLE,
                  ERROR_WHEN_DELETING_SUB_CORPUS_MSG, this.getClass());
              return;
            }

            Optional<SNode> parent =
                n.getInRelations().stream().filter((rel) -> rel instanceof SCorpusRelation)
                    .findFirst().map(rel -> ((SCorpusRelation) rel).getSource());
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

            Optional<SNode> parent =
                n.getInRelations().stream().filter((rel) -> rel instanceof SCorpusDocumentRelation)
                    .findFirst().map(rel -> ((SCorpusDocumentRelation) rel).getSource());

            // Attempt to find the previous sibling document of the one that is deleted
            Optional<SDocument> previousDocument = Optional.empty();
            if (parent.isPresent()) {
              // Collect all siblings
              List<SDocument> siblings = parent.get().getOutRelations().stream()
                  .filter(rel -> rel instanceof SCorpusDocumentRelation)
                  .map(rel -> ((SCorpusDocumentRelation) rel).getTarget())
                  .collect(Collectors.toList());
              if (!siblings.isEmpty()) {
                // Find the position of the deleted document and use the index
                // to get the previous document (or select the first other if not found)
                int indexOfDocument = siblings.indexOf(n);
                if (indexOfDocument == 0) {
                  if (siblings.size() > 1) {
                    // select the next document in the list since we are deleting the first one
                    previousDocument = Optional.of(siblings.get(1));
                  }
                } else if (indexOfDocument > 0) {
                  previousDocument = Optional.of(siblings.get(indexOfDocument - 1));
                } else {
                  previousDocument = Optional.of(siblings.get(0));
                }
              }
            }

            if (previousDocument.isPresent()) {
              // select the previous corpus
              selectSaltObject(previousDocument.get(), true);
            } else if (parent.isPresent()) {
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

  /**
   * Selects a Salt object (like document, sub-corpus, etc.) in the overview.
   * 
   * @param object The object to select
   * @param reveal If true, make sure the object is visible by revealing it
   */
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
            public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
                SNode currNode, SRelation<SNode, SNode> relation, SNode fromNode, long order) {

              chain.add(currNode);
            }

            @Override
            public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
                SNode currNode, SRelation<SNode, SNode> relation, SNode fromNode, long order) {

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


  private void updateView() {
    if (treeViewer != null) {
      treeViewer.setInput(projectManager.getProject().getCorpusGraphs());
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeProjectChanged(@UIEventTopic(Topics.PROJECT_LOADED) String value) {
    updateView();
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void subscribeProjectLoaded(@UIEventTopic(Topics.PROJECT_CHANGED) Object element) {
    updateView();
  }

  private void registerEditors(EModelService modelService, MApplication application,
      MPart thisPart) {
    // Find all descriptors with the correct category
    List<MPartDescriptor> editorParts = application.getDescriptors().stream()
        .filter((p) -> OpenSaltDocumentHandler.EDITOR_TAG.equals(p.getCategory()))
        .collect(Collectors.toList());

    for (MPartDescriptor desc : editorParts) {
      // Create a menu item for this editor
      MHandledMenuItem menuItem = modelService.createModelElement(MHandledMenuItem.class);

      // Set menu item command to the general one to open any editor
      MCommand newCommand = modelService.createModelElement(MCommand.class);
      newCommand.setElementId(OpenSaltDocumentHandler.COMMAND_OPEN_DOCUMENT_ID);
      menuItem.setCommand(newCommand);

      // Set the correct editor ID as parameter
      MParameter paramEditorID = modelService.createModelElement(MParameter.class);
      paramEditorID.setName(CommandParams.EDITOR_ID);
      paramEditorID.setValue(desc.getElementId());
      menuItem.getParameters().add(paramEditorID);

      // Use the part descriptor name as title for the menu entry
      menuItem.setLabel(OPEN_WITH_PREFIX + desc.getLabel());

      // Add the new menu item to the popup menu
      for (MMenu menu : thisPart.getMenus()) {
        if (menu.getElementId().equals(OPEN_DOCUMENT_POPUP_MENU_ID)) {
          menu.getChildren().add(menuItem);
        }
      }
    }
  }
}
