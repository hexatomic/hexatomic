/**
 * 
 */
package org.corpus_tools.hexatomic.edit.grid.data.access;

import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;

/**
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenColumnAccessor<R> implements IColumnAccessor<R> {
  
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TokenColumnAccessor.class);

  @Override
  public Object getDataValue(R rowObject, int columnIndex) {
    SToken token = (SToken) rowObject;
    return token.getGraph().getText(token);
  }

  @Override
  public void setDataValue(R rowObject, int columnIndex, Object newValue) {
    // TODO Auto-generated method stub
  }

  @Override
  public int getColumnCount() {
    return 1;
  }

}
