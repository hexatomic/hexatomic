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

package org.corpus_tools.hexatomic.grid.data;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.ContextualDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * Converts Salt node cell values to display values, i.e., the annotation value of the annotation
 * represented by the respective column.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class AnnotationDisplayConverter extends ContextualDisplayConverter {

  private final LabelAccumulator labelAccumulator;

  public AnnotationDisplayConverter(LabelAccumulator labelAccumulator) {
    this.labelAccumulator = labelAccumulator;
  }

  @Override
  public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object canonicalValue) {
    SNode node = null;
    if (canonicalValue instanceof Node) {
      node = (SNode) SaltHelper.resolveDelegation(canonicalValue);
    }
    if (node == null) {
      // throw new RuntimeException("Expected column to contain node, but is something else.");
      return null;
    }
    String qualifiedName = labelAccumulator.getQNameForColumn(cell.getColumnIndex());
    SAnnotation annotation = node.getAnnotation(qualifiedName);
    if (annotation == null) {
      return null;
    }
    return node.getAnnotation(qualifiedName).getValue();
  }

  @Override
  public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object displayValue) {
    // Not implemented
    return null;
  }


}
