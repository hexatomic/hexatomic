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

package org.corpus_tools.hexatomic.graph.internal;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.corpus_tools.hexatomic.styles.ColorPalette;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * Defines colors and other styles for Salt graph elements.
 * 
 * @author Thomas Krause
 *
 */
public class SaltGraphStyler extends LabelProvider implements ISelfStyleProvider, IFigureProvider {

  private final ShortestPathConnectionRouter pointingConnectionRouter;
  private final IFigure figure;

  private final FontMetrics fontMetrics;

  private final AnnotationFilter filter;

  /**
   * Construct a new instance.
   * 
   * @param figure The figure this style is applied to.
   * @param filter A filter that can limit the shown annotations
   */
  public SaltGraphStyler(IFigure figure, AnnotationFilter filter) {
    this.figure = figure;
    this.filter = filter;
    this.pointingConnectionRouter = new ShortestPathConnectionRouter(figure);
    GC gc = new GC(Display.getCurrent());
    fontMetrics = gc.getFontMetrics();
    gc.dispose();
  }

  @Override
  public String getText(Object element) {

    if (element instanceof SAnnotationContainer) {
      SAnnotationContainer container = (SAnnotationContainer) element;
      TreeMap<String, String> labelsByQName = new TreeMap<>();
      for (SAnnotation l : container.getAnnotations()) {
        String qname = SaltUtil.createQName(l.getNamespace(), l.getName());
        labelsByQName.put(qname, qname + "=" + l.getValue());
      }
      List<String> labels = new LinkedList<>(labelsByQName.values());
      return Joiner.on('\n').join(labels);
    }
    if (element instanceof EntityConnectionData) {
      return "";
    }
    throw new IllegalArgumentException("Object of type SAnnotationContainer expected, but got "
        + element.getClass().getSimpleName());
  }


  @Override
  public void selfStyleConnection(Object element, GraphConnection connection) {

    connection.setLineWidth(3);

    if (element instanceof SPointingRelation) {

      SPointingRelation pointing = (SPointingRelation) element;

      SStructuredNode saltSource = pointing.getSource();
      SStructuredNode saltTarget = pointing.getTarget();

      if (saltSource instanceof SToken && saltTarget instanceof SToken) {
        // Make sure the pointing relation is routed above the tokens with two bend points
        Bendpoint bpSource = () -> getBendpointLocation(connection, false);
        
        Bendpoint bpTarget = () -> getBendpointLocation(connection, true);
              
        this.pointingConnectionRouter.setConstraint(connection.getConnectionFigure(),
            Arrays.asList(bpSource, bpTarget));
      }

      connection.setLineColor(ColorPalette.BLUE);
      connection.setLineStyle(SWT.LINE_DASH);
      connection.getConnectionFigure().setConnectionRouter(pointingConnectionRouter);

      // Find the label of the connection figure and add a new locator constraint, that places
      // the label in the midpoint of the middle edge segment.
      Connection connFigure = connection.getConnectionFigure();
      for (Object c : connFigure.getChildren()) {
        if (c instanceof org.eclipse.draw2d.Label) {
          org.eclipse.draw2d.Label connLabel = (org.eclipse.draw2d.Label) c;
          connFigure.getLayoutManager().setConstraint(connLabel,
              new MidpointOfMiddleSegmentLocator(connFigure, fontMetrics.getHeight()));
        }
      }

    } else if (element instanceof SDominanceRelation) {
      connection.setLineColor(ColorPalette.VERMILLION);
    } else if (element instanceof SSpanningRelation) {
      connection.setLineColor(ColorPalette.BLUISH_GREEN);
      connection.setLineStyle(SWT.LINE_DOT);
    }
  }

  /**
   * Calculate a bend point that lies between the two connected nodes.
   * 
   * @param connection The connection to calculate the bend point for.
   * @param forDestination If true the bend point is calculated for the target node
   * @return A bend point
   */
  private Point getBendpointLocation(GraphConnection connection, boolean forDestination) {
    GraphNode source = connection.getSource();
    GraphNode target = connection.getDestination();

    int sourceWidth = source.getSize().width;
    int targetWidth = target.getSize().width;

    Point sourceLoc = source.getLocation().getTranslated(new Dimension(sourceWidth / 2, 0));
    Point targetLoc =
        connection.getDestination().getLocation().getTranslated(new Dimension(targetWidth / 2, 0));

    // make the height dependent on the distance
    double nodeHeight = Math.max(source.getSize().height, target.getSize().height);
    double distance = Math.abs(targetLoc.getDifference(sourceLoc).preciseWidth());
    if (distance == 0) {
      distance = 0.001;
    }
    double factor = Math.min(1.0, Math.abs(distance / figure.getSize().preciseWidth()));
    int height = (int) (10.0 + nodeHeight * factor);

    if (forDestination) {
      if (sourceLoc.x < targetLoc.x) {
        // source -> target
        return new Point(targetLoc.x - (targetWidth / 2), sourceLoc.y - height);
      } else {
        // target <- source
        return new Point(targetLoc.x + (targetWidth / 2), sourceLoc.y - height);
      }
    } else {
      if (sourceLoc.x < targetLoc.x) {
        // source -> target
        return new Point(sourceLoc.x + (sourceWidth / 2), sourceLoc.y - height);
      } else {
        // target <- source
        return new Point(sourceLoc.x - (sourceWidth / 2), sourceLoc.y - height);
      }
    }
  }

  @Override
  public void selfStyleNode(Object element, GraphNode node) {
    // No custom graph node styling yet
  }

  @Override
  public IFigure getFigure(Object element) {
    if (element instanceof SNode) {
      NodeFigure currFigure = new NodeFigure((SNode) element, fontMetrics, filter);
      currFigure.setSize(currFigure.getPreferredSize());
      return currFigure;
    }
    return null;
  }

}
