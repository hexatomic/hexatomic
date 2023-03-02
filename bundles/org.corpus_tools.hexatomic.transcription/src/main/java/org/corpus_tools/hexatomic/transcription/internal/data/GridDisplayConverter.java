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

package org.corpus_tools.hexatomic.transcription.internal.data;

import org.corpus_tools.hexatomic.core.SaltHelper;
import org.corpus_tools.hexatomic.core.errors.HexatomicRuntimeException;
import org.corpus_tools.salt.common.SToken;
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
 * @author Thomas Krause {@literal <thomas.krause@hu-berlin.de>}
 *
 */
public class GridDisplayConverter extends ContextualDisplayConverter {


  @Override
  public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object canonicalValue) {
    if (cell == null || canonicalValue == null) {
      return null;
    }

    if (canonicalValue instanceof String || canonicalValue instanceof Number) {
      // Nothing to convert
      return canonicalValue;
    } else if (canonicalValue instanceof Node) {
      SNode node = (SNode) SaltHelper.resolveDelegation(canonicalValue);
      if (node instanceof SToken) {
        SToken token = (SToken) node;
        return token.getGraph().getText(token);
      } else {
        throw new HexatomicRuntimeException(
            "Cell " + cell + " must contain SToken. Contained: "
                + canonicalValue.getClass().getCanonicalName());
      }
    } else {
      return null;
    }
  }

  @Override
  public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry,
      Object displayValue) {
    // Conversion needs to be handled downstream in GraphDataProvider.
    return displayValue;
  }

}
