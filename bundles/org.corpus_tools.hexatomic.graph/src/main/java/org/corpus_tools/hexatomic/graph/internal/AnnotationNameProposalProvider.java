/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
 * %%
 * Copyright (C) 2018 - 2021 Stephan Druskat, Thomas Krause
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

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.graph.Label;
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

    this.proposals
        .addAll(graph.getNodes().parallelStream().flatMap(n -> n.getAnnotations().stream())
            .map(Label::getQName).collect(Collectors.toList()));
  }

  @Override
  public IContentProposal[] getProposals(String contents, int position) {
    if (position == 0) {
      return new IContentProposal[0];
    }
    String string2Find = contents.substring(0, position);
    Pattern pattern = Pattern.compile(string2Find, Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    return proposals.stream().filter(qname -> pattern.matcher(qname).find())
        .map(ContentProposal::new).toArray(IContentProposal[]::new);

  }
}
