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
import org.corpus_tools.hexatomic.core.undo.ChangeSet;
import org.corpus_tools.hexatomic.graph.internal.AnnotationFilterWidget;
import org.corpus_tools.hexatomic.graph.internal.GraphDragMoveAdapter;
import org.corpus_tools.hexatomic.graph.internal.GraphLayoutParameterWidget;
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
import org.corpus_tools.salt.common.STextOverlappingRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.progress.ProgressEvent;
import org.eclipse.zest.layouts.progress.ProgressListener;

/**
 * A part that allows to edit a graph. This includes a graph view and an interactive console for
 * editing the graph.
 * 
 * @author Thomas Krause {@literal krauseto@hu-berlin.de}
 *
 */
public class GraphEditor {

  private static final int ANNO_FILTER_HEIGHT = 120;

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphEditor.class);

  /**
   * The ID used as SWTBot widget key for the table of text ranges.
   */
  public static final String TEXT_RANGE_ID = "graph-editor/text-range";
  /**
   * The ID used as SWTBot widget key for the console.
   */
  public static final String CONSOLE_ID = "graph-editor/text-console";

  private static final String TEXT = "text";
  private static final String RANGE = "range";
  static final int DEFAULT_DIFF = 25;
  private static final String ORG_ECLIPSE_SWTBOT_WIDGET_KEY = "org.eclipse.swtbot.widget.key";

  @Inject
  ProjectManager projectManager;

  @Inject
  MPart thisPart;

  @Inject
  ErrorService errorService;

  @Inject
  Shell shell;

  private Button btnIncludeSpans;
  private Table textRangeTable;
  private Button btnIncludePointingRelations;

  private GraphViewer viewer;

  @Inject
  UISynchronize sync;

  @Inject
  ErrorService errors;

  @Inject
  private IEventBroker events;

  private ConsoleView consoleView;

  private final Filter graphFilter = new Filter();

  private final ScrollToFirstTokenListener scrollToFirstTokenListener =
      new ScrollToFirstTokenListener();

  private AnnotationFilterWidget annoFilterWidget;

  private SaltGraphLayout graphLayout;

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
    parent.setLayout(new FillLayout(SWT.VERTICAL));

    SashForm mainSash = new SashForm(parent, SWT.VERTICAL);

    final SashForm graphSash = new SashForm(mainSash, SWT.HORIZONTAL);

    viewer = new GraphViewer(graphSash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getGraphControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    viewer.setContentProvider(new SaltGraphContentProvider());
    graphLayout = createLayout();
    viewer.setLayoutAlgorithm(graphLayout);
    viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_ANIMATION);
    viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
    viewer.getGraphControl().setDragDetect(true);
    viewer.setFilters(graphFilter);

    Composite sideBar = new Composite(graphSash, SWT.NONE);
    sideBar.setLayout(GridLayoutFactory.swtDefaults().create());

    // Weights can only be set after all items of the sash have been added
    graphSash.setWeights(65, 35);

    constructFilterView(sideBar);
    constructSegmentFilter(sideBar);
    constructGraphParams(sideBar);

    registerGraphControlListeners();

    viewer.getControl().forceFocus();

    Document consoleDocument = new Document();
    SourceViewer consoleViewer = new SourceViewer(mainSash, null, SWT.V_SCROLL | SWT.H_SCROLL);
    consoleViewer.setDocument(consoleDocument);
    consoleViewer.getTextWidget().setData(ORG_ECLIPSE_SWTBOT_WIDGET_KEY, CONSOLE_ID);
    consoleView = new ConsoleView(consoleViewer, sync, projectManager, getGraph());
    mainSash.setWeights(85, 15);

    SDocumentGraph graph = getGraph();
    boolean scrollToFirstToken = graph != null && !graph.getTokens().isEmpty();
    updateView(true, scrollToFirstToken);
  }

  private void constructFilterView(Composite sideBar) {
    Group filterGroup = new Group(sideBar, SWT.SHADOW_ETCHED_IN);
    filterGroup.setLayoutData(GridDataFactory.defaultsFor(filterGroup).align(SWT.FILL, SWT.TOP)
        .grab(true, false).create());
    filterGroup.setLayout(new FillLayout());
    filterGroup.setText("Filter View");

    ExpandBar filterExpandBar = new ExpandBar(filterGroup, SWT.NONE);
    filterExpandBar.addExpandListener(new ExpandListener() {

      @Override
      public void itemExpanded(ExpandEvent e) {
        relayout();
      }

      @Override
      public void itemCollapsed(ExpandEvent e) {
        relayout();
      }

      private void relayout() {
        Display.getDefault().timerExec(1, sideBar::layout);
      }
    });


    Composite filterByType = new Composite(filterExpandBar, SWT.NONE);
    filterByType.setLayout(RowLayoutFactory.swtDefaults().type(SWT.VERTICAL).create());

    btnIncludeSpans = new Button(filterByType, SWT.CHECK);
    btnIncludeSpans.setLayoutData(RowDataFactory.swtDefaults().create());
    btnIncludeSpans.setSelection(false);
    btnIncludeSpans.setText("Spans");

    btnIncludePointingRelations = new Button(filterByType, SWT.CHECK);
    btnIncludeSpans.setLayoutData(RowDataFactory.swtDefaults().create());
    btnIncludePointingRelations.setSelection(true);
    btnIncludePointingRelations.setText("Pointing Relations");

    ExpandItem filterByTypeExpandItem = new ExpandItem(filterExpandBar, SWT.NONE);
    filterByTypeExpandItem.setText("Annotation Types");
    filterByTypeExpandItem.setControl(filterByType);
    filterByTypeExpandItem.setHeight(filterByType.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

    annoFilterWidget = new AnnotationFilterWidget(filterExpandBar, getGraph(), events);
    viewer.setLabelProvider(new SaltGraphStyler(
        viewer.getGraphControl().getLightweightSystem().getRootFigure(), annoFilterWidget));

    ExpandItem annoFilterExpandBar = new ExpandItem(filterExpandBar, SWT.NONE);
    annoFilterExpandBar.setText("Node Annotations");
    annoFilterExpandBar.setHeight(ANNO_FILTER_HEIGHT);
    annoFilterExpandBar.setControl(annoFilterWidget);
  }

  private void constructGraphParams(Composite sideBar) {
    ExpandBar paramExpandBar = new ExpandBar(sideBar, SWT.NONE);
    paramExpandBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
    paramExpandBar.addExpandListener(new ExpandListener() {

      @Override
      public void itemExpanded(ExpandEvent e) {
        relayout();
      }

      @Override
      public void itemCollapsed(ExpandEvent e) {
        relayout();
      }

      private void relayout() {
        Display.getDefault().timerExec(1, sideBar::layout);
      }
    });


    GraphLayoutParameterWidget widget = new GraphLayoutParameterWidget(paramExpandBar, this.events);

    ExpandItem paramExpandItem = new ExpandItem(paramExpandBar, SWT.NONE);
    paramExpandItem.setText("Display Configuration");
    paramExpandItem.setHeight(widget.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
    paramExpandItem.setControl(widget);
    // There seems to be a SWT bug under Linux (GTK) where changing the value on the scale does not
    // redraw the position of the slider. When the expand item is expanded once, the bug seems to be
    // avoided. So the workaround is to expand and unexpand the parameter panel when the expand bar
    // is resized.
    paramExpandBar.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        if (paramExpandItem.getExpanded()) {
          paramExpandItem.setExpanded(false);
          Display.getDefault().timerExec(1, () -> paramExpandItem.setExpanded(true));
        }
      }
    });
  }

  private void constructSegmentFilter(Composite sideBar) {
    textRangeTable = new Table(sideBar, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
    textRangeTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
    textRangeTable.setHeaderVisible(true);
    textRangeTable.setLinesVisible(true);
    textRangeTable.getHorizontalBar().setEnabled(true);
    textRangeTable.getVerticalBar().setEnabled(true);
    textRangeTable.setData(ORG_ECLIPSE_SWTBOT_WIDGET_KEY, TEXT_RANGE_ID);
    TableColumn tblclmnFilterBySegment = new TableColumn(textRangeTable, SWT.NONE);
    tblclmnFilterBySegment.setWidth(100);
    tblclmnFilterBySegment.setText("Filter by segment");


    textRangeTable.addSelectionListener(new UpdateViewListener(false));
    btnIncludePointingRelations.addSelectionListener(new UpdateViewListener(true));
    btnIncludeSpans.addSelectionListener(new UpdateViewListener(true));
  }

  private void registerGraphControlListeners() {

    // Allow to drag the background area with the mouse
    GraphDragMoveAdapter.register(viewer.getGraphControl());

    // Disable the original scroll event when the mouse wheel is activated
    viewer.getGraphControl().addListener(SWT.MouseVerticalWheel, event -> event.doit = false);

    // Add a mouse wheel listener that zooms instead of scrolling
    viewer.getGraphControl().addMouseWheelListener(new MouseZoomOrScrollListener(this));
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

  /**
   * Force update of the graph view.
   * 
   * @param recalculateSegments Allows to control whether to re-calculate the segments. This is a
   *        costly operation should be avoided when there is no structural change.
   * @param scrollToFirstToken If true, reset the view to show the first token after the update is
   *        complete.
   */
  @SuppressWarnings("unchecked")
  protected void updateView(final boolean recalculateSegments, final boolean scrollToFirstToken) {

    try {
      SDocumentGraph graph = getGraph();

      if (graph == null) {
        errors.showError("Unexpected error",
            "Annotation graph for selected document vanished. Please report this as a bug.",
            GraphEditor.class);
        return;
      }


      final boolean includeSpans = btnIncludeSpans.getSelection();

      final List<SegmentSelectionEntry> oldSelectedSegments = new LinkedList<>();
      final List<SegmentSelectionEntry> newSelectedSegments = new LinkedList<>();

      if (recalculateSegments) {
        // Store the old segment selection
        for (TableItem item : textRangeTable.getSelection()) {
          SegmentSelectionEntry entry = new SegmentSelectionEntry();
          entry.range = (Range<Long>) item.getData(RANGE);
          entry.text = (STextualDS) item.getData(TEXT);
          oldSelectedSegments.add(entry);
        }
      } else {
        // The ranges that will be selected will be the same as the current ones
        for (TableItem item : textRangeTable.getSelection()) {
          SegmentSelectionEntry selection = new SegmentSelectionEntry();
          selection.range = (Range<Long>) item.getData(RANGE);
          selection.text = (STextualDS) item.getData(TEXT);
          newSelectedSegments.add(selection);
        }
      }

      scheduleUpdateViewJob(newSelectedSegments, oldSelectedSegments, annoFilterWidget.getFilter(),
          includeSpans, graph, recalculateSegments, scrollToFirstToken);

    } catch (RuntimeException ex) {
      errors.handleException("Unexpected error when updating the graph editor view.", ex,
          GraphEditor.class);
    }
  }

  private void scheduleUpdateViewJob(List<SegmentSelectionEntry> newSelectedSegments,
      List<SegmentSelectionEntry> oldSelectedSegments, Optional<Set<String>> annotationFilters,
      boolean includeSpans, SDocumentGraph graph, boolean recalculateSegments,
      boolean scrollToFirstToken) {

    Job job = Job.create("Update graph view", monitor -> {
      monitor.beginTask("Updating graph view", IProgressMonitor.UNKNOWN);

      if (recalculateSegments) {
        monitor.subTask("Recalculating available segments");
        recalculateAvailableSegments(newSelectedSegments, oldSelectedSegments, annotationFilters,
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
      List<SegmentSelectionEntry> oldSelectedSegments, Optional<Set<String>> annotationFilters,
      boolean includeSpans, SDocumentGraph graph) {

    newSelectedSegments.clear();

    ViewerFilter currentFilter = new RootFilter(annotationFilters, includeSpans);

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
        item.setData(TEXT, e.getKey());
      }

      textRangeTable.deselectAll();
      textRangeTable.getColumn(0).pack();

      boolean selectedSomeOld = false;
      for (SegmentSelectionEntry oldSegment : oldSelectedSegments) {
        for (int idx = 0; idx < textRangeTable.getItems().length; idx++) {
          TableItem item = textRangeTable.getItem(idx);
          Range<Long> itemRange = (Range<Long>) item.getData(RANGE);
          STextualDS itemText = (STextualDS) item.getData(TEXT);
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
        selection.text = (STextualDS) textRangeTable.getItem(0).getData(TEXT);
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
      viewer.getGraphControl().getRootLayer().setScale(0.0);
      // We can only scroll to the first token after the layout has been applied,
      // which can be asynchronous
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

  private static TreeSet<Range<Long>> calculateSegmentsForText(STextualDS ds, SDocumentGraph graph,
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

  private SaltGraphLayout createLayout() {

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

  private static boolean hasMatchingAnnotation(SNode node, Set<String> annotationFilters) {
    if (annotationFilters == null || annotationFilters.isEmpty() || node instanceof SToken) {
      // If no filter is set or the type of node should always be included, always
      // return true
      return true;
    }
    return node.getAnnotations().parallelStream()
        .anyMatch(a -> annotationFilters.contains(a.getQName()));
  }

  void zoomGraphView(double factor, Point originallyClicked) {
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

  void scrollGraphView(int xoffset, int yoffset) {
    Viewport viewPort = viewer.getGraphControl().getViewport();
    Point loc = viewPort.getViewLocation();
    loc.translate(xoffset, yoffset);
    viewPort.setViewLocation(loc);
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

    private boolean isZoomInKey(KeyEvent e) {
      return (e.character == '+' || e.keyCode == SWT.KEYPAD_ADD) && (e.stateMask & SWT.CTRL) != 0;
    }

    private boolean isZoomOutKey(KeyEvent e) {
      return (e.character == '-' || e.keyCode == SWT.KEYPAD_SUBTRACT)
          && (e.stateMask & SWT.CTRL) != 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {

      Viewport viewPort = viewer.getGraphControl().getViewport();
      Point loc = viewPort.getViewLocation();

      if (isZoomInKey(e)) {
        zoomGraphView(2.0, loc);
      } else if (isZoomOutKey(e)) {
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


  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  protected void onCheckpointCreated(
      @UIEventTopic(Topics.ANNOTATION_CHECKPOINT_CREATED) Object element) {

    if (element instanceof ChangeSet) {
      ChangeSet changeSet = (ChangeSet) element;
      log.debug("Received ANNOTATION_CHANGED event for changeset {}", changeSet);

      // check graph updates contain changes for this graph
      if (changeSet.containsDocument(
          thisPart.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID))) {
        // Only relations with text coverage semantics can change the structure of the graph and
        // modify segments. Also, changes to the labels of textual relations (e.g. start/end) can
        // change the token structure.
        boolean recalculateSegments = changeSet.getChangedElements().stream().anyMatch(
            c -> c instanceof STextualRelation || c instanceof STextOverlappingRelation<?, ?>)
            || changeSet.getChangedContainers().stream()
                .anyMatch(STextualRelation.class::isInstance);

        updateView(recalculateSegments, false);
      }
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void onAnnotationFilterChanged(
      @UIEventTopic(AnnotationFilterWidget.ANNO_FILTER_CHANGED_TOPIC) Object sender) {
    if (sender == annoFilterWidget) {
      updateView(true, false);
    }
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void onCheckpointRestored(
      @UIEventTopic(Topics.ANNOTATION_CHECKPOINT_RESTORED) Object element) {
    updateView(true, false);
  }

  @Inject
  @org.eclipse.e4.core.di.annotations.Optional
  private void onGraphLayoutParamChanged(
      @UIEventTopic(GraphLayoutParameterWidget.PARAM_CHANGED_TOPIC) Object element) {
    if (element instanceof GraphDisplayConfiguration) {
      this.graphLayout.setConfig((GraphDisplayConfiguration) element);
    }
    updateView(false, false);
  }

  private class RootFilter extends ViewerFilter {

    private final Set<String> annotationFilters;
    private final boolean includeSpans;

    public RootFilter(Optional<Set<String>> annotationFilters, boolean includeSpans) {
      this.annotationFilters = annotationFilters.orElse(null);
      this.includeSpans = includeSpans;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

      if (element instanceof SNode) {
        SNode node = (SNode) element;

        boolean include = hasMatchingAnnotation(node, annotationFilters);

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
        return include && hasMatchingAnnotation(node, annoFilterWidget.getFilter().orElse(null));

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
      // We only start to scroll after the task has ended, but we still have to
      // implement all methods of the interface.
    }

    @Override
    public void progressUpdated(ProgressEvent e) {
      // We only start to scroll after the task has ended, but we still have to
      // implement all methods of the interface.
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
