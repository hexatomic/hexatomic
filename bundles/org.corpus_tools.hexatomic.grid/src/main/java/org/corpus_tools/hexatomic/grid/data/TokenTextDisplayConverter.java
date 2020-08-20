/**
 * 
 */
package org.corpus_tools.hexatomic.grid.data;

import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

/**
 * Converts the call value from {@link SToken} to the token text.
 * 
 * @author Stephan Druskat {@literal <mail@sdruskat.net>}
 */
public class TokenTextDisplayConverter extends DisplayConverter {

  @Override
  public Object canonicalToDisplayValue(Object canonicalValue) {
    if (canonicalValue instanceof SToken) {
      SToken token = (SToken) canonicalValue;
      return token.getGraph().getText(token);
    } else {
      throw new RuntimeException("Found a non-token in a column that is reserved for tokens!");
    }
  }

  @Override
  public Object displayToCanonicalValue(Object displayValue) {
    // Not implemented
    return null;
  }

}
