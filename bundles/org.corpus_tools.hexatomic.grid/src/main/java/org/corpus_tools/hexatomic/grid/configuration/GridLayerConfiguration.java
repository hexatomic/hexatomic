/*-
 * #%L
 * org.corpus_tools.hexatomic.grid
 * %%
 * Copyright (C) 2018 - 2020 Stephan Druskat,
 *                                     Thomas Krause
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
