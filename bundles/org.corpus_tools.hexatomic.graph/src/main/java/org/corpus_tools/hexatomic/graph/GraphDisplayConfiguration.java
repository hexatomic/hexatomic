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
