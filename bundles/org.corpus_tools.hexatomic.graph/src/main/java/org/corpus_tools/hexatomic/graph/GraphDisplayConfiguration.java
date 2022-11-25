/*-
 * #%L
 * org.corpus_tools.hexatomic.graph
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

package org.corpus_tools.hexatomic.graph;

/**
 * Configuring the graph display including its layout parameters.
 * 
 * @author Thomas Krause
 *
 */
public class GraphDisplayConfiguration {

  private double verticalNodeMargin = 0.8;
  private double horizontalTokenMargin = 0.5;
  private int tokenRankOffset = 1;

  /**
   * Get the vertical node margin.
   * 
   * @return The margin as fraction of the node height.
   */
  public double getVerticalNodeMargin() {
    return verticalNodeMargin;
  }

  /**
   * Sets the margin used to vertically separate nodes. The margin is given relative to the node
   * height. E.g. "0.8". is means 80% of the node height.
   * 
   * @param verticalNodeMargin Margin as fraction of the node height.
   */
  public void setVerticalNodeMargin(double verticalNodeMargin) {
    this.verticalNodeMargin = verticalNodeMargin;
  }

  public double getHorizontalTokenMargin() {
    return horizontalTokenMargin;
  }

  public void setHorizontalTokenMargin(double horizontalTokenMargin) {
    this.horizontalTokenMargin = horizontalTokenMargin;
  }

  public int getTokenRankOffset() {
    return tokenRankOffset;
  }

  public void setTokenRankOffset(int tokenRankOffset) {
    this.tokenRankOffset = tokenRankOffset;
  }
}
