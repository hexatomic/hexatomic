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

package org.corpus_tools.hexatomic.grid.layers;

import org.corpus_tools.hexatomic.grid.data.NodeSpanningDataProvider;
import org.corpus_tools.hexatomic.grid.handlers.DisplayAnnotationRenameDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;

/**
 * A custom {@link SpanningDataLayer} registering custom command handlers.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 *
 */
public class GridSpanningDataLayer extends SpanningDataLayer {

  public GridSpanningDataLayer(NodeSpanningDataProvider spanningDataProvider) {
    super(spanningDataProvider);
  }

  @Override
  protected void registerCommandHandlers() {
    super.registerCommandHandlers();
    registerCommandHandler(new DisplayAnnotationRenameDialogCommandHandler(this));
  }

}
