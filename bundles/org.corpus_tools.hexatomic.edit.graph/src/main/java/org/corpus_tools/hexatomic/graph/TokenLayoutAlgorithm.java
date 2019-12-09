package org.corpus_tools.hexatomic.graph;

import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class TokenLayoutAlgorithm extends AbstractLayoutAlgorithm {

  private double averageNodeWidth;
  
  public TokenLayoutAlgorithm(int styles) {
    super(styles);
  }
  
  

  @Override
  public void setLayoutArea(double x, double y, double width, double height) {
    throw new RuntimeException("Operation not implemented");
  }

  @Override
  protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  protected void applyLayoutInternal(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY,
      double boundsWidth, double boundsHeight) {
    double x = 0.0;
    for (int index = 0; index < entitiesToLayout.length; index++) {
      InternalNode n = entitiesToLayout[index++];
      

      n.setInternalLocation(x, boundsY);
      x += this.averageNodeWidth / 2.0;
      x += n.getLayoutEntity().getWidthInLayout();
      
      fireProgressEvent(index, entitiesToLayout.length);
    
    }

    updateLayoutLocations(entitiesToLayout);
    fireProgressEvent(entitiesToLayout.length, entitiesToLayout.length);

  }

  @Override
  protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider, double x, double y, double width,
      double height) {
    double sum = 0.0;
    for(int index=0; index < entitiesToLayout.length; index++) {
      InternalNode n = entitiesToLayout[index++];
      sum += n.getLayoutEntity().getWidthInLayout();
    }
    
    this.averageNodeWidth = sum / (double) entitiesToLayout.length;

  }

  @Override
  protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
      InternalRelationship[] relationshipsToConsider) {
    // TODO Auto-generated method stub

  }

  @Override
  protected int getTotalNumberOfLayoutSteps() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected int getCurrentLayoutStep() {
    // TODO Auto-generated method stub
    return 0;
  }

}
