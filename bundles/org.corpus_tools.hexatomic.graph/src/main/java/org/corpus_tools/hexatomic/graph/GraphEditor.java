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
import java.util.Collection;
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
import org.corpus_tools.hexatomic.console.ConsoleView;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.hexatomic.core.Topics;
import org.corpus_tools.hexatomic.core.errors.ErrorService;
import org.corpus_tools.hexatomic.core.handlers.OpenSaltDocumentHandler;
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
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.corpus_tools.salt.graph.IdentifiableElement;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.progress.ProgressEvent;
import org.eclipse.zest.layouts.progress.ProgressListener;

public class GraphEditor {

  private static final String RANGE = "range";
  private static final int DEFAULT_DIFF = 25;
  private static final String ORG_ECLIPSE_SWTBOT_WIDGET_KEY = "org.eclipse.swtbot.widget.key";


  @Inject
  private ProjectManager projectManager;

  @Inject
  private MPart thisPart;

  @Inject
  ErrorService errorService;

  @Inject
  Shell shell;


  private Button btnIncludeSpans;
  private Table textRangeTable;
  private Text txtSegmentFilter;
  private Button btnIncludePointingRelations;

  private GraphViewer viewer;


  @Inject
  UISynchronize sync;

  @Inject
  ErrorService errors;

  @Inject
  private IEventBroker events;

  private ListenerImplementation projectChangeListener;

  private ConsoleView consoleView;


  private final Filter graphFilter = new Filter();

  private final ScrollToFirstTokenListener scrollToFirstTokenListener =
      new ScrollToFirstTokenListener();

  private String getDocumentId() {
    return thisPart.getPersistedState().get("org.corpus_tools.hexatomic.document-id");
  }

  private SDocumentGraph getGraph() {
    String documentID = getDocumentId();
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
    viewer.setFilters(graphFilter);

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
    btnIncludeSpans.setText("Include spans");

    btnIncludePointingRelations = new Button(filterComposite, SWT.CHECK);
    btnIncludePointingRelations.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
    btnIncludePointingRelations.setSelection(true);
    btnIncludePointingRelations.setText("Include pointing relations");

    txtSegmentFilter = new Text(filterComposite, SWT.BORDER);
    txtSegmentFilter.setMessage("Filter by node annotation name");
    txtSegmentFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

    textRangeTable =
        new Table(filterComposite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
    textRangeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    textRangeTable.setHeaderVisible(true);
    textRangeTable.setLinesVisible(true);
    textRangeTable.getHorizontalBar().setEnabled(true);
    textRangeTable.getVerticalBar().setEnabled(true);
    textRangeTable.setData(ORG_ECLIPSE_SWTBOT_WIDGET_KEY, "graph-editor/text-range");

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
    consoleViewer.getTextWidget().setData(ORG_ECLIPSE_SWTBOT_WIDGET_KEY,
        "graph-editor/text-console");
    consoleView = new ConsoleView(consoleViewer, sync, getGraph());
    mainSash.setWeights(new int[] {200, 100});

    updateView(true, true);

  }

  private void registerGraphControlListeners() {

    // Allow to drag the background area with the mouse
    GraphDragMoveAdapter.register(viewer.getGraphControl());

    // Disable the original scroll event when the mouse wheel is activated
    viewer.getGraphControl().addListener(SWT.MouseVerticalWheel, event -> event.doit = false);

    // Add a mouse wheel listener that zooms instead of scrolling
    viewer.getGraphControl().addMouseWheelListener(new MouseZoomOrScrollListener());
    // Center the view on the mouse cursor when it was double clicked
    viewer.getGraphControl().addMouseListener(new DoubleClickCenterListener());

    // React to arrow keys to scroll the viewport or CTRL plus/minus to zoom
    viewer.getGraphControl().addKeyListener(new ScrollAndZoomKeyListener());
  }

  /**
   * Center the view around the current position of the mouse cursor by getting the difference from
   * the viewport center and the mouse click position. This difference is added to the viewport
   * location.
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

    events.post(Topics.DOCUMENT_CLOSED,
        thisPart.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID));
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void documentLoaded(@UIEventTopic(Topics.DOCUMENT_LOADED) String changedDocumentId) {
    if (changedDocumentId != null && changedDocumentId.equals(getDocumentId())) {
      // Notify the console that there is a new annotation graph
      consoleView.setGraph(getGraph());
    }
  }

  @SuppressWarnings("unchecked")
  private void updateView(final boolean recalculateSegments, final boolean scrollToFirstToken) {

    try {
      SDocumentGraph graph = getGraph();

      if (graph == null) {
        errors.showError("Unexpected error",
            "Annotation graph for selected document vanished. Please report this as a bug.",
            GraphEditor.class);
        return;
      }

      final String segmentFilterText = txtSegmentFilter.getText();
      final boolean includeSpans = btnIncludeSpans.getSelection();

      final List<SegmentSelectionEntry> oldSelectedSegments = new LinkedList<>();
      final List<SegmentSelectionEntry> newSelectedSegments = new LinkedList<>();

      if (recalculateSegments) {
        // Store the old segment selection
        for (TableItem item : textRangeTable.getSelection()) {
          SegmentSelectionEntry entry = new SegmentSelectionEntry();
          entry.range = (Range<Long>) item.getData(RANGE);
          entry.text = (STextualDS) item.getData("text");
          oldSelectedSegments.add(entry);
        }
      } else {
        // The ranges that will be selected will be the same as the current ones
        for (TableItem item : textRangeTable.getSelection()) {
          SegmentSelectionEntry selection = new SegmentSelectionEntry();
          selection.range = (Range<Long>) item.getData(RANGE);
          selection.text = (STextualDS) item.getData("text");
          newSelectedSegments.add(selection);
        }
      }

      scheduleUpdateViewJob(newSelectedSegments, oldSelectedSegments, segmentFilterText,
          includeSpans, graph, recalculateSegments, scrollToFirstToken);


    } catch (RuntimeException ex) {
      errors.handleException("Unexpected error when updating the graph editor view.", ex,
          GraphEditor.class);
    }
  }

  private void scheduleUpdateViewJob(List<SegmentSelectionEntry> newSelectedSegments,
      List<SegmentSelectionEntry> oldSelectedSegments, String segmentFilterText,
      boolean includeSpans, SDocumentGraph graph, boolean recalculateSegments,
      boolean scrollToFirstToken) {

    Job job = Job.create("Update graph view", monitor -> {
      monitor.beginTask("Updating graph view", IProgressMonitor.UNKNOWN);

      if (recalculateSegments) {
        monitor.subTask("Recalculating available segments");
        recalculateAvailableSegments(newSelectedSegments, oldSelectedSegments, segmentFilterText,
            includeSpans, graph);
      }

      monitor.subTask("Showing selected segments in graph");

      graphFilter.updateSelectedSegments(newSelectedSegments);

      monitor.done();

      sync.asyncExec(() -> {

        // make sure the console view knows about the currently selected text
        if (newSelectedSegments.isEmpty()) {
          consoleView.setSelectedText(null);
        } else {
          consoleView.setSelectedText(newSelectedSegments.get(0).text);
        }

        // update the status check for each item
        for (int idx = 0; idx < textRangeTable.getItemCount(); idx++) {
          textRangeTable.getItem(idx).setChecked(textRangeTable.isSelected(idx));
        }

        updateGraphViewer(graph, scrollToFirstToken);

      });

    });
    job.schedule();
  }


  @SuppressWarnings("unchecked")
  private List<SegmentSelectionEntry> recalculateAvailableSegments(
      List<SegmentSelectionEntry> newSelectedSegments,
      List<SegmentSelectionEntry> oldSelectedSegments, String segmentFilterText,
      boolean includeSpans, SDocumentGraph graph) {

    newSelectedSegments.clear();

    ViewerFilter currentFilter = new RootFilter(segmentFilterText, includeSpans);

    final Multimap<STextualDS, Range<Long>> segments = calculateSegments(graph, currentFilter);

    sync.syncExec(() -> {
      textRangeTable.removeAll();
      for (Map.Entry<STextualDS, Range<Long>> e : segments.entries()) {
        TableItem item = new TableItem(textRangeTable, SWT.NONE);

        long rangeStart = e.getValue().lowerEndpoint();
        long rangeEnd = e.getValue().upperEndpoint();

        String coveredText = e.getKey().getText().substring((int) rangeStart, (int) rangeEnd);

        item.setText(coveredText);
        item.setData(RANGE, e.getValue());
        item.setData("text", e.getKey());
      }

      textRangeTable.deselectAll();
      textRangeTable.getColumn(0).pack();

      boolean selectedSomeOld = false;
      for (SegmentSelectionEntry oldSegment : oldSelectedSegments) {
        for (int idx = 0; idx < textRangeTable.getItems().length; idx++) {
          TableItem item = textRangeTable.getItem(idx);
          Range<Long> itemRange = (Range<Long>) item.getData(RANGE);
          STextualDS itemText = (STextualDS) item.getData("text");
          if (itemText == oldSegment.text && itemRange.isConnected(oldSegment.range)) {
            textRangeTable.select(idx);
            selectedSomeOld = true;

            SegmentSelectionEntry selection = new SegmentSelectionEntry();
            selection.range = itemRange;
            selection.text = itemText;
            newSelectedSegments.add(selection);
          }
        }
      }
      if (!selectedSomeOld && textRangeTable.getItemCount() > 0) {
        textRangeTable.setSelection(0);
        SegmentSelectionEntry selection = new SegmentSelectionEntry();
        selection.range = (Range<Long>) textRangeTable.getItem(0).getData(RANGE);
        selection.text = (STextualDS) textRangeTable.getItem(0).getData("text");
        newSelectedSegments.add(selection);
      }
    });

    return newSelectedSegments;
  }

  private void updateGraphViewer(SDocumentGraph graph, boolean scrollToFirstToken) {
    if (viewer.getInput() != graph) {
      viewer.setInput(graph);
    } else {
      viewer.refresh();
    }

    if (scrollToFirstToken) {
      viewer.getGraphControl().getRootLayer().setScale(0);
      // We can only scroll to the first token after the layout has been applied, which can be
      // asynchronous
      viewer.getGraphControl().getLayoutAlgorithm()
          .addProgressListener(this.scrollToFirstTokenListener);
    } else {
      viewer.getGraphControl().getLayoutAlgorithm()
          .removeProgressListener(this.scrollToFirstTokenListener);
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
      result.putAll(ds, calculateSegmentsForText(ds, graph, filter));
    }

    return result;
  }

  private static TreeSet<Range<Long>> calculateSegmentsForText(STextualDS ds,
      SDocumentGraph graph,
      ViewerFilter filter) {

    TreeSet<Range<Long>> sortedRangesForDS = new TreeSet<>(new RangeStartComparator<>());


    DataSourceSequence<Integer> textSeq = new DataSourceSequence<>();
    textSeq.setDataSource(ds);
    textSeq.setStart(ds.getStart());
    textSeq.setEnd(ds.getEnd());

    List<SToken> token = graph.getSortedTokenByText(graph.getTokensBySequence(textSeq));

    Optional<Range<Long>> range = Optional.empty();
    SNode lastRoot = null;
    for (SToken t : token) {
      SNode currentRoot = RootTraverser.getRoot(t, filter);
      Range<Long> tokenRange = getRangeForToken(t);
      if (tokenRange != null && Objects.equal(lastRoot, currentRoot)) {
        if (range.isPresent()) {
          // Extend existing range at the end
          long upper = Math.max(range.get().upperEndpoint(), tokenRange.upperEndpoint());
          range = Optional.of(Range.closedOpen(range.get().lowerEndpoint(), upper));
        } else {
          // Use initial token range
          range = Optional.of(tokenRange);
        }
      } else {
        // add the completed range
        if (range.isPresent()) {
          sortedRangesForDS.add(range.get());
        }
        // begin new range
        range = Optional.of(tokenRange);
      }

      lastRoot = currentRoot;
    }
    // add the last range
    if (range.isPresent()) {
      sortedRangesForDS.add(range.get());
    }

    return sortedRangesForDS;
  }


  private LayoutAlgorithm createLayout() {

    SaltGraphLayout layout = new SaltGraphLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING);

    org.eclipse.zest.layouts.Filter hierarchyFilter = object -> {
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
    };

    layout.setFilter(hierarchyFilter);

    return layout;
  }

  private static boolean hasMatchingAnnotation(SNode node, String segmentFilterText) {
    if (segmentFilterText == null || segmentFilterText.isEmpty() || node instanceof SToken) {
      // If no filter is set or the type of node should always be included, always return true
      return true;
    }
    if (node.getAnnotations() != null) {
      for (SAnnotation anno : node.getAnnotations()) {
        if (anno.getName().contains(segmentFilterText)) {
          // Annotation found
          return true;
        }
      }
    }

    // No matching annotation found
    return false;
  }

  private void zoomGraphView(double factor, Point originallyClicked) {
    ScalableFigure figure = viewer.getGraphControl().getRootLayer();
    double oldScale = figure.getScale();
    double newScale = oldScale * factor;


    double clippedScale = Math.max(0.0625, Math.min(2.0, newScale));

    if (clippedScale != oldScale) {

      Point originalViewLocation = viewer.getGraphControl().getViewport().getViewLocation();

      figure.setScale(clippedScale);
      viewer.getGraphControl().getViewport().validate();

      viewer.getGraphControl().getViewport()
          .setViewLocation(originalViewLocation.getScaled(clippedScale / oldScale));
      viewer.getGraphControl().getViewport().validate();

      Point scaledClicked = originallyClicked.getScaled(clippedScale / oldScale);
      centerViewportToPoint(scaledClicked);
    }
  }
  

  private void scrollGraphView(int xoffset, int yoffset) {
    Viewport viewPort = viewer.getGraphControl().getViewport();
    Point loc = viewPort.getViewLocation();
    loc.translate(xoffset, yoffset);
    viewPort.setViewLocation(loc);
  }

  private final class MouseZoomOrScrollListener implements MouseWheelListener {


    private void doScroll(MouseEvent e) {
      if (e.stateMask == SWT.SHIFT) {
        // Mouse wheel scrolled down
        if (e.count < 0) {
          // Scroll down
          scrollGraphView(0, +DEFAULT_DIFF);
        } else {
          // Scroll up
          scrollGraphView(0, -DEFAULT_DIFF);
        }
      } else if (e.stateMask == SWT.CTRL) {
        if (e.count < 0) {
          // Scroll left
          scrollGraphView(+DEFAULT_DIFF, 0);
        } else {
          // Scroll right
          scrollGraphView(-DEFAULT_DIFF, 0);
        }
      }
    }

    private void doZoom(MouseEvent e) {
      Point originallyClicked = new Point(e.x, e.y);
      if (e.count < 0) {
        zoomGraphView(0.75, originallyClicked);
      } else {
        zoomGraphView(1.25, originallyClicked);
      }
    }

    @Override
    public void mouseScrolled(MouseEvent e) {

      // If Shift or Ctrl are pressed while scrolling the mouse wheel, scroll rather than zoom
      if (e.stateMask == SWT.SHIFT || e.stateMask == SWT.CTRL) {
        doScroll(e);
      } else {
        doZoom(e);
      }
    }
  }

  private final class DoubleClickCenterListener implements MouseListener {
    @Override
    public void mouseUp(MouseEvent e) {
      // Only react to double clicks, ignore mouse up
    }

    @Override
    public void mouseDown(MouseEvent e) {
      // Only react to double clicks, ignore mouse down
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      Point clickedInViewport = new Point(e.x, e.y);
      centerViewportToPoint(clickedInViewport);
    }
  }

  private final class ScrollAndZoomKeyListener implements KeyListener {
    @Override
    public void keyReleased(KeyEvent e) {
      // Only react to key pressed, but not the released event
    }

    @Override
    public void keyPressed(KeyEvent e) {
      
      Viewport viewPort = viewer.getGraphControl().getViewport();
      Point loc = viewPort.getViewLocation();

      if (e.character == '+' && (e.stateMask & SWT.CTRL) != 0) {
        zoomGraphView(2.0, loc);
      } else if (e.character == '-' && (e.stateMask & SWT.CTRL) != 0) {
        zoomGraphView(0.5, loc);
      } else {
        // Prepare scroll
        int diff = DEFAULT_DIFF;
        if ((e.stateMask & SWT.SHIFT) != 0) {
          diff = DEFAULT_DIFF * 10;
        }

        if (e.keyCode == SWT.ARROW_LEFT) {
          scrollGraphView(-diff, 0);
        } else if (e.keyCode == SWT.ARROW_RIGHT) {
          scrollGraphView(+diff, 0);
        } else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.PAGE_DOWN) {
          scrollGraphView(0, +diff);
        } else if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.PAGE_UP) {
          scrollGraphView(0, -diff);
        }
      }
    }
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
      SDocumentGraph graph = getGraph();
      if (graph == null) {
        errors.showError("Unexpected error",
            "Annotation graph for subscribed document vanished. Please report this as a bug.",
            GraphEditor.class);
        return;
      }
      if (element == null || element.getId() == null) {
        return;
      } else {
        URI elementUri = URI.createURI(element.getId());
        if (!elementUri.path().equals(graph.getPath().path())) {
          return;
        }
      }

      sync.syncExec(() -> updateView(true, false));
    }
  }

  private class RootFilter extends ViewerFilter {

    private final String segmentFilterText;
    private final boolean includeSpans;

    public RootFilter(String segmentFilterText, boolean includeSpans) {
      this.segmentFilterText = segmentFilterText;
      this.includeSpans = includeSpans;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

      if (element instanceof SNode) {
        SNode node = (SNode) element;

        boolean include = hasMatchingAnnotation(node, segmentFilterText);

        if (node instanceof SSpan) {
          include = include && includeSpans;
        }
        return include;

      } else {
        return true;
      }
    }
  }

  private static class SegmentSelectionEntry {
    Range<Long> range;
    STextualDS text;
  }

  private class Filter extends ViewerFilter {

    private final Set<String> coveredTokenIDs = new HashSet<>();

    public void updateSelectedSegments(Collection<SegmentSelectionEntry> selectedSegments) {
      coveredTokenIDs.clear();

      // Collect all tokens which are selected by the current ranges
      Set<SToken> coveredTokens = new HashSet<>();
      for (SegmentSelectionEntry item : selectedSegments) {

        if (item.text.getGraph() != null) {

          DataSourceSequence<Number> seq = new DataSourceSequence<>();
          seq.setStart(item.range.lowerEndpoint());
          seq.setEnd(item.range.upperEndpoint());
          seq.setDataSource(item.text);
          List<SToken> t = item.text.getGraph().getTokensBySequence(seq);
          if (t != null) {
            coveredTokens.addAll(t);
          }
        }
      }

      for (SToken t : coveredTokens) {
        coveredTokenIDs.add(t.getId());
      }
    }


    private boolean overlapsSelectedRange(SDocumentGraph graph, SNode node) {
      List<SToken> overlappedTokens = graph.getOverlappedTokens(node);
      for (SToken t : overlappedTokens) {
        if (coveredTokenIDs.contains(t.getId())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

      SDocumentGraph graph = getGraph();
      if (graph == null) {
        return false;
      }

      if (element instanceof SNode) {
        SNode node = (SNode) element;

        // check if the node covers a currently selected range
        boolean include = overlapsSelectedRange(graph, node);

        if (node instanceof SSpan) {
          // If this is a span, the inclusion must be explicitly requested by the user
          include = include && btnIncludeSpans.getSelection();
        }
        // additionally check if the node has a matching annotation
        return include && hasMatchingAnnotation(node, txtSegmentFilter.getText());

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
      updateView(recalculateSegments, true);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      updateView(recalculateSegments, true);
    }

    @Override
    public void modifyText(ModifyEvent e) {
      updateView(recalculateSegments, true);
    }
  }

  private class ScrollToFirstTokenListener implements ProgressListener {

    @Override
    public void progressStarted(ProgressEvent e) {
      // We only start to scroll after the task has ended, but we still have to implement all
      // methods of the interface.
    }

    @Override
    public void progressUpdated(ProgressEvent e) {
      // We only start to scroll after the task has ended, but we still have to implement all
      // methods of the interface.
    }

    @Override
    public void progressEnded(ProgressEvent e) {

      Display.getCurrent().syncExec(() -> {
        ScalableFigure figure = viewer.getGraphControl().getRootLayer();
        // Get the height needed for showing all tree layers
        double newScale = (double) viewer.getGraphControl().getBounds().height
            / figure.getBounds().preciseHeight();
        figure.setScale(newScale);

        viewer.getGraphControl().getViewport().setViewLocation(0, 0);
        viewer.getGraphControl().getViewport().validate();
      });
    }
  }

}

