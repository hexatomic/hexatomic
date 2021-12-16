package org.corpus_tools.hexatomic.graph.internal;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

class AnnotationNameProposalProvider implements IContentProposalProvider {

  private final SDocumentGraph graph;

  private final Set<String> proposals = new TreeSet<>();

  AnnotationNameProposalProvider(SDocumentGraph graph) {
    super();
    this.graph = graph;

    calculateProposals();
  }

  private void calculateProposals() {
    this.proposals.clear();
    
    this.proposals.addAll(graph.getNodes().parallelStream().flatMap(n -> n.getAnnotations().stream())
        .map(a -> a.getQName()).collect(Collectors.toList()));
  }

  @Override
  public IContentProposal[] getProposals(String contents, int position) {
    if(position == 0) {
      return null;
    }
    String string2Find = contents.substring(0, position);
    Pattern pattern = Pattern.compile(string2Find, Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    IContentProposal[] filtered = proposals.stream().filter(qname -> pattern.matcher(qname).find())
        .map(ContentProposal::new).toArray(IContentProposal[]::new);
    return filtered.length == 0 ? null : filtered;

  }
}
