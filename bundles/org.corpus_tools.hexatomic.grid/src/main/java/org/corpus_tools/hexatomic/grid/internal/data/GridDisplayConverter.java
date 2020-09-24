/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat, Thomas Krause
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

package org.corpus_tools.hexatomic.grid.internal.data;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.hexatomic.grid.internal.data.Column.ColumnType;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.ContextualDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * Converts Salt node cell values to display values, i.e., the annotation value of the annotation
 * represented by the respective annotation column, or the token text for the respective token text
 * column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridDisplayConverter extends ContextualDisplayConverter {

  protected final GraphDataProvider bodyDataProvider;

  /**
   * Converter setting the body data provider field, or throws RuntimeException if the provider is
   * <code>null</code>.
   * 
   * @param bodyDataProvider The body data provider
   */
  public GridDisplayConverter(GraphDataProvider bodyDataProvider) {
    if (bodyDataProvider == null) {
      throw new IllegalArgumentException(
          "The data provider passed to " + this.getClass().getSimpleName() + " must not be null.");
    }
    this.bodyDataProvider = bodyDataProvider;
  }

  @Override
  public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object canonicalValue) {
    if (cell == null || canonicalValue == null) {
      return null;
    }
    final Column column = bodyDataProvider.getColumns().get(cell.getColumnIndex());
    SNode node = null;
    if (canonicalValue instanceof Node) {
      node = (SNode) SaltHelper.resolveDelegation(canonicalValue);
    } else {
      return null;
    }
    if (node instanceof SToken) {
      SToken token = (SToken) node;
      // Check which column type the cell has
      if (column.getColumnType() == ColumnType.TOKEN_TEXT) {
        return token.getGraph().getText(token);
      } else {
        return getAnnotationForNode(token, column);
      }
    } else if (node instanceof SSpan) {
      SSpan span = (SSpan) node;
      return getAnnotationForNode(span, column);
    } else {
      throw new HexatomicRuntimeException(
          "Cell " + cell + " must contain SSpan or SToken. Contained: "
          + canonicalValue.getClass().getCanonicalName());
    }
  }

  private Object getAnnotationForNode(SNode node, Column column) {
    String qualifiedName = column.getColumnValue();
    SAnnotation annotation = node.getAnnotation(qualifiedName);
    if (annotation == null) {
      return null;
    }
    return node.getAnnotation(qualifiedName).getValue();
  }

  @Override
  public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object displayValue) {
    // Conversion needs to be handled downstream in GraphDataProvider.
    return displayValue;
  }

}
