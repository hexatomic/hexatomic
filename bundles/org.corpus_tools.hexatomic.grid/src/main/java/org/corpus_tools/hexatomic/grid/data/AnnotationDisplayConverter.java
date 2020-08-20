/**
 * 
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
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
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
      throw new RuntimeException("Expected column to contain node, but is something else.");
    }
    String qName = labelAccumulator.getQNameForColumn(cell.getColumnIndex());
    SAnnotation annotation = node.getAnnotation(qName);
    if (annotation == null) {
      return "null";
    }
    return node.getAnnotation(qName).getValue();
  }

  @Override
  public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object displayValue) {
    // Not implemented
    return null;
  }


}
