package org.corpus_tools.hexatomic.graph.internal;

import java.util.Optional;
import java.util.Set;


public interface AnnotationFilter {

  /**
   * Get allowed annotation names to include in the view.
   * 
   * @return The the list of names or {@link Optional#empty()} when no filter should be applied.
   */
  Optional<Set<String>> getFilter();

}
