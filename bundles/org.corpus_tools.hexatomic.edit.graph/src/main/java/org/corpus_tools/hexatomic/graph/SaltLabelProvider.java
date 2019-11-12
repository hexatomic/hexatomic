package org.corpus_tools.hexatomic.graph;

import com.google.common.base.Joiner;
import java.util.TreeMap;
import org.corpus_tools.salt.graph.Label;
import org.corpus_tools.salt.graph.LabelableElement;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.zest.core.viewers.EntityConnectionData;

public class SaltLabelProvider extends LabelProvider {
  @Override
  public String getText(Object element) {
    if (element instanceof LabelableElement) {
      LabelableElement node = (LabelableElement) element;
      TreeMap<String, String> labelsByQName = new TreeMap<>();
      for (Label l : node.getLabels()) {
        String qname = SaltUtil.createQName(l.getNamespace(), l.getName());
        labelsByQName.put(qname, qname + "=" + l.getValue());
      }
      return Joiner.on('\n').join(labelsByQName.values());
    }
    if (element instanceof EntityConnectionData) {
      EntityConnectionData test = (EntityConnectionData) element;
      return "";
    }
    throw new IllegalArgumentException("Object of type LabelableElement expectected, but got "
        + element.getClass().getSimpleName());
  }
}
