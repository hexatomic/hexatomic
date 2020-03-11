package org.corpus_tools.hexatomic.grid.configuration;

import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;

/**
 * Customization configuration for a {@link CompositeLayer}.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 *
 */
public class GridLayerConfiguration extends DefaultGridLayerConfiguration {

  public GridLayerConfiguration(CompositeLayer gridLayer) {
    super(gridLayer);
  }

  @Override
  protected void addAlternateRowColoringConfig(CompositeLayer gridLayer) {
    // Not implemented to avoid alternating white/grey layout
  }


}
