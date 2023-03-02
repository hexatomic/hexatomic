/*-
 * #%L
 * [bundle] Timeline Editor
 * %%
 * Copyright (C) 2018 - 2022 Stephan Druskat, Thomas Krause
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

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;

public class TliCornerDataProvider extends DefaultCornerDataProvider {

  public TliCornerDataProvider(IDataProvider columnHeaderDataProvider,
      IDataProvider rowHeaderDataProvider) {
    super(columnHeaderDataProvider, rowHeaderDataProvider);
  }

  @Override
  public Object getDataValue(int columnIndex, int rowIndex) {
    return "TLI";
  }

}
