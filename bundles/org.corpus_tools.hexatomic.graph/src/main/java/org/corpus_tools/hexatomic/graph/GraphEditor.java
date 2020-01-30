/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
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

package org.corpus_tools.hexatomic.graph;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.corpus_tools.hexatomic.console.AtomicalConsole;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.graph.internal.GraphDragMoveAdapter;
import org.corpus_tools.hexatomic.graph.internal.RootTraverser;
import org.corpus_tools.hexatomic.graph.internal.SaltGraphContentProvider;
import org.corpus_tools.hexatomic.graph.internal.SaltGraphLayout;
import org.corpus_tools.hexatomic.graph.internal.SaltGraphStyler;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.extensions.notification.Listener.NOTIFICATION_TYPE;
import org.corpus_tools.salt.extensions.notification.graph.impl.NodeNotifierImpl;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutItem;
import org.eclipse.zest.layouts.LayoutStyles;

public class GraphEditor {

  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @Inject
  public GraphEditor() {

  }

  private Button btnIncludeSpans;
  private Table textRangeTable;
  private Text txtSegmentFilter;
  private Button btnIncludePointingRelations;

  private GraphViewer viewer;


  @Inject
  UISynchronize sync;

  @Inject
  ErrorService errors;

  private ListenerImplementation projectChangeListener;

  private SDocumentGraph getGraph() {
    String documentID = thisPart.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
    Optional<SDocument> doc = projectManager.getDocument(documentID);
    if (doc.isPresent()) {
      return doc.get().getDocumentGraph();
    }
    return null;
  }

  /**
   * Create a new graph viewer.
   * 
   * @param parent The parent SWT composite.
   * @param part The part this viewer belongs to.
   */
  @PostConstruct
  public void postConstruct(Composite parent, MPart part) {

    projectChangeListener = new ListenerImplementation();
    projectManager.addListener(projectChangeListener);

    parent.setLayout(new FillLayout(SWT.VERTICAL));

    SashForm mainSash = new SashForm(parent, SWT.VERTICAL);

    SashForm graphSash = new SashForm(mainSash, SWT.HORIZONTAL);

    viewer = new GraphViewer(graphSash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getGraphControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    viewer.setContentProvider(new SaltGraphContentProvider());
    viewer.setLabelProvider(
        new SaltGraphStyler(viewer.getGraphControl().getLightweightSystem().getRootFigure()));
    viewer.setLayoutAlgorithm(createLayout());
    viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_ANIMATION);
    viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
    viewer.getGraphControl().setDragDetect(true);

    Composite filterComposite = new Composite(graphSash, SWT.NONE);
    GridLayout gridLayoutFilterComposite = new GridLayout(1, false);
    gridLayoutFilterComposite.marginWidth = 0;
    filterComposite.setLayout(gridLayoutFilterComposite);

    Label lblFilterByAnnotation = new Label(filterComposite, SWT.NONE);
    lblFilterByAnnotation.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    lblFilterByAnnotation.setText("Filter by annotation type");

    btnIncludeSpans = new Button(filterComposite, SWT.CHECK);
    btnIncludeSpans.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    btnIncludeSpans.setSelection(false);
    btnIncludeSpans.setText("Include Spans");

    btnIncludePointingRelations = new Button(filterComposite, SWT.CHECK);
    btnIncludePointingRelations.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
    btnIncludePointingRelations.setSelection(true);
    btnIncludePointingRelations.setText("Include pointing relations");

    txtSegmentFilter = new Text(filterComposite, SWT.BORDER);
    txtSegmentFilter.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));

    textRangeTable =
        new Table(filterComposite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
    textRangeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    textRangeTable.setHeaderVisible(true);
    textRangeTable.setLinesVisible(true);
    textRangeTable.getHorizontalBar().setEnabled(true);
    textRangeTable.getVerticalBar().setEnabled(true);

    TableColumn tblclmnFilterBySegment = new TableColumn(textRangeTable, SWT.NONE);
    tblclmnFilterBySegment.setWidth(100);
    tblclmnFilterBySegment.setText("Filter by segment");
    graphSash.setWeights(new int[] {300, 100});

    textRangeTable.addSelectionListener(new UpdateViewListener(false));
    txtSegmentFilter.addModifyListener(new UpdateViewListener(true));
    btnIncludePointingRelations.addSelectionListener(new UpdateViewListener(true));
    btnIncludeSpans.addSelectionListener(new UpdateViewListener(true));

    registerGraphControlListeners();

    viewer.getControl().forceFocus();

    Document consoleDocument = new Document();
    SourceViewer consoleViewer = new SourceViewer(mainSash, null, SWT.V_SCROLL | SWT.H_SCROLL);
    consoleViewer.setDocument(consoleDocument);
    new AtomicalConsole(consoleViewer, sync, getGraph());
    mainSash.setWeights(new int[] {200, 100});

    updateView(true);

  }

  private void registerGraphControlListeners() {

    // Allow to drag the background area with the mouse
    GraphDragMoveAdapter.register(viewer.getGraphControl());

    // Disable the original scroll event when the mouse wheel is activated
    viewer.getGraphControl().addListener(SWT.MouseVerticalWheel,
        new org.eclipse.swt.widgets.Listener() {

          @Override
          public void handleEvent(Event event) {
            event.doit = false;
          }
        });

    // Add a mouse wheel listener that zooms instead of scrolling
    viewer.getGraphControl().addMouseWheelListener(new MouseWheelListener() {

      @Override
      public void mouseScrolled(MouseEvent e) {

        ScalableFigure figure = viewer.getGraphControl().getRootLayer();
        double oldScale = figure.getScale();
        Optional<Double> newScale = Optional.empty();
        if (e.count < 0) {
          newScale = Optional.of(oldScale * 0.75);
        } else {
          newScale = Optional.of(oldScale * 1.25);
        }

        if (newScale.isPresent()) {
          double clippedScale = Math.max(0.0625, Math.min(2.0, newScale.get()));

          if (clippedScale != oldScale) {

            Point originalViewLocation = viewer.getGraphControl().getViewport().getViewLocation();

            figure.setScale(clippedScale);
            viewer.getGraphControl().getViewport().validate();

            viewer.getGraphControl().getViewport()
                .setViewLocation(originalViewLocation.getScaled(clippedScale / oldScale));
            viewer.getGraphControl().getViewport().validate();

            Point originallyClicked = new Point(e.x, e.y);
            Point scaledClicked = originallyClicked.getScaled(clippedScale / oldScale);
            centerViewportToPoint(scaledClicked);
          }
        }

      }
    });
    // Center the view on the mouse cursor when it was double clicked
    viewer.getGraphControl().addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(MouseEvent e) {
      }

      @Override
      public void mouseDown(MouseEvent e) {
      }

      @Override
      public void mouseDoubleClick(MouseEvent e) {
        Point clickedInViewport = new Point(e.x, e.y);
        centerViewportToPoint(clickedInViewport);
      }
    });

    // React to arrow keys to move the displayed graph
    viewer.getGraphControl().addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        int diff = 25;
        if ((e.stateMask & SWT.SHIFT) != 0) {
          diff = 250;
        }
        Viewport viewPort = viewer.getGraphControl().getViewport();
        Point loc = viewPort.getViewLocation();

        if (e.keyCode == SWT.ARROW_LEFT) {
          loc.translate(-diff, 0);
        } else if (e.keyCode == SWT.ARROW_RIGHT) {
          loc.translate(+diff, 0);
        } else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.PAGE_DOWN) {
          loc.translate(0, +diff);
        } else if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.PAGE_UP) {
          loc.translate(0, -diff);
        }
        viewPort.setViewLocation(loc);

      }
    });
  }

  /**
   * Center the view around at the mouse cursor by getting the difference from the viewport center
   * and the mouse click position. This difference is added to the viewport location.
   * 
   * @param clickedInViewport The point where the mouse was clicked.
   */
  private void centerViewportToPoint(Point clickedInViewport) {
    Viewport viewPort = viewer.getGraphControl().getViewport();

    Point viewPortCenter =
        new Point(viewPort.getSize().width(), viewPort.getSize().height()).getScaled(0.5);
    Dimension diff = clickedInViewport.getDifference(viewPortCenter);

    viewPort.setViewLocation(viewPort.getViewLocation().getTranslated(diff));
  }

  @PreDestroy
  void preDestroy() {
    projectManager.removeListener(projectChangeListener);
  }

  @SuppressWarnings("unchecked")
  private void updateView(boolean recalculateSegments) {

    SDocumentGraph graph = getGraph();

    if (graph == null) {
      errors.showError("Unexpected error",
          "Annotation graph for selected document vanished. Please report this as a bug.",
          GraphEditor.class);
      return;
    }

    if (recalculateSegments) {
      // store the old segment selection
      List<Range<Long>> oldSelectedRanges = new LinkedList<>();
      for (TableItem item : textRangeTable.getSelection()) {
        oldSelectedRanges.add((Range<Long>) item.getData("range"));
      }

      updateSegments(graph);

      textRangeTable.deselectAll();
      textRangeTable.getColumn(0).pack();

      boolean selectedSomeOld = false;
      for (Range<Long> oldRange : oldSelectedRanges) {
        for (int idx = 0; idx < textRangeTable.getItems().length; idx++) {
          Range<Long> itemRange = (Range<Long>) textRangeTable.getItem(idx).getData("range");
          if (itemRange.isConnected(oldRange)) {
            textRangeTable.select(idx);
            selectedSomeOld = true;
          }
        }
      }
      if (!selectedSomeOld && textRangeTable.getItemCount() > 0) {
        textRangeTable.setSelection(0);
      }

    }

    // update the status check for each item
    for (int idx = 0; idx < textRangeTable.getItemCount(); idx++) {
      textRangeTable.getItem(idx).setChecked(textRangeTable.isSelected(idx));
    }

    viewer.setFilters(new Filter());

    if (viewer.getInput() != graph) {
      viewer.setInput(graph);
    }

    viewer.applyLayout();
  }


  private static Range<Long> getRangeForToken(SToken tok) {
    @SuppressWarnings("rawtypes")
    List<DataSourceSequence> overlappedDS =
        tok.getGraph().getOverlappedDataSourceSequence(tok, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
    if (overlappedDS != null && !overlappedDS.isEmpty()) {
      long start = overlappedDS.get(0).getStart().longValue();
      long end = overlappedDS.get(0).getEnd().longValue();
      if (start <= end) {
        return Range.closedOpen(start, end);
      } else {
        return Range.closedOpen(start, start);
      }
    }
    return null;
  }

  private static Multimap<STextualDS, Range<Long>> calculateSegments(SDocumentGraph graph,
      ViewerFilter filter) {

    LinkedHashMultimap<STextualDS, Range<Long>> result = LinkedHashMultimap.create();

    if (graph == null) {
      return result;
    }

    List<STextualDS> allTexts = new ArrayList<>(graph.getTextualDSs());
    allTexts.sort(new STextualDataSourceComparator());


    for (STextualDS ds : graph.getTextualDSs()) {

      if (ds.getText() == null) {
        continue;
      }

      TreeSet<Range<Long>> sortedRangesForDS = new TreeSet<>(new RangeStartComparator<>());


      DataSourceSequence<Integer> textSeq = new DataSourceSequence<Integer>();
      textSeq.setDataSource(ds);
      textSeq.setStart(ds.getStart());
      textSeq.setEnd(ds.getEnd());

      List<SToken> token = graph.getSortedTokenByText(graph.getTokensBySequence(textSeq));


      Optional<Long> rangeStart = Optional.empty();
      Optional<Long> rangeEnd = Optional.empty();
      SNode lastRoot = null;
      for (SToken t : token) {
        SNode currentRoot = RootTraverser.getRoot(t, filter);
        Range<Long> tokenRange = getRangeForToken(t);
        if (Objects.equal(lastRoot, currentRoot)) {
          // extend range
          if (!rangeStart.isPresent()) {
            rangeStart = Optional.of(tokenRange.lowerEndpoint());
          }
          if (!rangeEnd.isPresent() || rangeEnd.get() < tokenRange.upperEndpoint()) {
            rangeEnd = Optional.of(tokenRange.upperEndpoint());
          }
        } else {

          // add the completed range
          if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            sortedRangesForDS.add(Range.closedOpen(rangeStart.get(), rangeEnd.get()));
          }

          // begin new range
          rangeStart = Optional.of(tokenRange.lowerEndpoint());
          rangeEnd = Optional.of(tokenRange.upperEndpoint());
        }

        lastRoot = currentRoot;
      }
      // add the last range
      if (rangeStart.isPresent() && rangeEnd.isPresent()) {
        sortedRangesForDS.add(Range.closedOpen(rangeStart.get(), rangeEnd.get()));
      }


      result.putAll(ds, sortedRangesForDS);
    }

    return result;
  }

  private void updateSegments(SDocumentGraph graph) {
    textRangeTable.removeAll();

    ViewerFilter currentFilter = new RootFilter();

    Multimap<STextualDS, Range<Long>> segments = calculateSegments(graph, currentFilter);

    for (Map.Entry<STextualDS, Range<Long>> e : segments.entries()) {
      TableItem item = new TableItem(textRangeTable, SWT.NONE);

      long rangeStart = e.getValue().lowerEndpoint();
      long rangeEnd = e.getValue().upperEndpoint();

      String coveredText = e.getKey().getText().substring((int) rangeStart, (int) rangeEnd);

      item.setText(coveredText);
      item.setData("range", e.getValue());
      item.setData("text", e.getKey());
    }

  }

  private LayoutAlgorithm createLayout() {

    SaltGraphLayout layout = new SaltGraphLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING);

    org.eclipse.zest.layouts.Filter hierarchyFilter = new org.eclipse.zest.layouts.Filter() {

      @Override
      public boolean isObjectFiltered(LayoutItem object) {
        IdentifiableElement data = SaltGraphContentProvider.getData(object);
        if (data instanceof SStructuredNode || data instanceof SToken) {
          return false;
        } else if (data instanceof SDominanceRelation || data instanceof SSpanningRelation) {
          SRelation<?, ?> rel = (SRelation<?, ?>) data;
          if (rel.getTarget() instanceof SStructuredNode) {
            return false;
          }
        }
        return true;
      }
    };

    layout.setFilter(hierarchyFilter);

    return layout;
  }

  private static boolean isStructuralUpdate(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute,
      Object oldValue, Object newValue, Object container) {

    // Get the node for a label
    if (container instanceof org.corpus_tools.salt.graph.Label) {
      org.corpus_tools.salt.graph.Label lbl = (org.corpus_tools.salt.graph.Label) container;
      container = lbl.getContainer();
    }
    // Get the real node instance via the ID
    if (container instanceof NodeNotifierImpl) {
      NodeNotifierImpl n = (NodeNotifierImpl) container;
      if (n.getGraph() != null) {
        container = n.getGraph().getNode(n.getId());
      }
    }


    if (container instanceof STextualDS) {
      // A new text might have been set
      return true;
    } else if (type == NOTIFICATION_TYPE.ADD || type == NOTIFICATION_TYPE.REMOVE
        || type == NOTIFICATION_TYPE.REMOVE_ALL) {
      if (attribute == GRAPH_ATTRIBUTES.GRAPH_NODES
          || attribute == GRAPH_ATTRIBUTES.GRAPH_RELATIONS) {
        return true;
      }
    }


    return false;
  }


  private final class ListenerImplementation implements Listener {
    @Override
    public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue,
        Object newValue, Object container) {

      // Check if the ID of the changed object points to the same document as our annotation graph
      IdentifiableElement element = null;
      if (container instanceof IdentifiableElement) {
        element = (IdentifiableElement) container;
      } else if (container instanceof org.corpus_tools.salt.graph.Label) {
        element = ((org.corpus_tools.salt.graph.Label) container).getContainer();
      }
      if (element == null || element.getId() == null) {
        return;
      } else {
        URI elementUri = URI.createURI(element.getId());
        SDocumentGraph graph = getGraph();
        if (!elementUri.path().equals(graph.getPath().path())) {
          return;
        }
      }

      // Only recalculate segments when the structure of the graph is changed
      final boolean recalculateSegments =
          isStructuralUpdate(type, attribute, oldValue, newValue, container);

      sync.syncExec(() -> updateView(recalculateSegments));
    }
  }

  private class RootFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

      if (element instanceof SNode) {

        boolean include = false;

        SNode node = (SNode) element;

        if (txtSegmentFilter.getText().isEmpty() || (node instanceof SToken)) {
          include = true;
        } else {
          if (node.getAnnotations() != null) {
            for (SAnnotation anno : node.getAnnotations()) {
              if (anno.getName().contains(txtSegmentFilter.getText())) {
                include = true;
                break;
              }
            }
          }
        }

        if (node instanceof SSpan) {
          include = include && btnIncludeSpans.getSelection();
        }
        return include;

      } else {
        return true;
      }
    }

  }

  private class Filter extends ViewerFilter {

    private final Set<String> coveredTokenIDs;

    public Filter() {

      // Collect all tokens which are selected by the current ranges
      Set<SToken> coveredTokens = new HashSet<>();
      for (TableItem item : textRangeTable.getSelection()) {

        @SuppressWarnings("unchecked")
        Range<Long> itemRange = (Range<Long>) item.getData("range");
        STextualDS itemText = (STextualDS) item.getData("text");

        if (itemText.getGraph() != null) {

          DataSourceSequence<Number> seq = new DataSourceSequence<>();
          seq.setStart(itemRange.lowerEndpoint());
          seq.setEnd(itemRange.upperEndpoint());
          seq.setDataSource(itemText);
          List<SToken> t = itemText.getGraph().getTokensBySequence(seq);
          if (t != null) {
            coveredTokens.addAll(t);
          }
        }
      }
      coveredTokenIDs = new HashSet<>();
      for (SToken t : coveredTokens) {
        coveredTokenIDs.add(t.getId());
      }
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

      SDocumentGraph graph = getGraph();
      if (graph == null) {
        return false;
      }

      if (element instanceof SNode) {
        SNode node = (SNode) element;
        boolean include = false;

        // check if the node covers a currently selected range
        List<SToken> overlappedTokens = graph.getOverlappedTokens(node);
        for (SToken t : overlappedTokens) {
          if (coveredTokenIDs.contains(t.getId())) {
            include = true;
            break;
          }
        }

        if (node instanceof SSpan) {
          include = include && btnIncludeSpans.getSelection();
        }

        // additionally check for valid annotation
        if (include && !txtSegmentFilter.getText().isEmpty() && !(node instanceof SToken)) {
          if (node.getAnnotations() != null) {
            boolean annoFound = false;
            for (SAnnotation anno : node.getAnnotations()) {
              if (anno.getName().contains(txtSegmentFilter.getText())) {
                annoFound = true;
                break;
              }
            }
            include = annoFound;
          }
        }

        return include;
      } else if (element instanceof SRelation<?, ?>) {
        SRelation<?, ?> rel = (SRelation<?, ?>) element;
        boolean include = true;
        if (rel instanceof SPointingRelation) {
          include = btnIncludePointingRelations.getSelection();
        }
        return include;
      } else {
        return true;
      }
    }

  }

  private static class STextualDataSourceComparator implements Comparator<STextualDS> {

    @Override
    public int compare(STextualDS o1, STextualDS o2) {
      return ComparisonChain.start().compare(o1.getName(), o2.getName()).result();
    }
  }


  private static class RangeStartComparator<C extends Comparable<?>>
      implements Comparator<Range<C>> {

    @Override
    public int compare(Range<C> o1, Range<C> o2) {

      return ComparisonChain.start().compare(o1.lowerEndpoint(), o2.lowerEndpoint())
          .compare(o1.upperBoundType(), o2.upperBoundType()).result();
    }
  }

  private class UpdateViewListener implements SelectionListener, ModifyListener {

    private final boolean recalculateSegments;

    public UpdateViewListener(boolean recalculateSegments) {
      this.recalculateSegments = recalculateSegments;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      updateView(recalculateSegments);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      updateView(recalculateSegments);
    }

    @Override
    public void modifyText(ModifyEvent e) {
      updateView(recalculateSegments);
    }
  }

}


