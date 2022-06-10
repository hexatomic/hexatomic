package org.corpus_tools.hexatomic.graph;

/**
 * Configuring the graph display including its layout parameters.
 * 
 * @author Thomas Krause
 *
 */
public class GraphDisplayConfiguration {

  private double verticalNodeMargin = 0.8;
  private int minimalNodeHeight = 50;

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

  /**
   * Sets the minimum node height.
   * 
   * @return Node height in pixels.
   */
  public int getMinimalNodeHeight() {
    return minimalNodeHeight;
  }

  /**
   * Sets the minimum node height in pixels.
   * 
   * @param minimalNodeHeight Minimal node height in pixels.
   */
  public void setMinimalNodeHeight(int minimalNodeHeight) {
    this.minimalNodeHeight = minimalNodeHeight;
  }


}
