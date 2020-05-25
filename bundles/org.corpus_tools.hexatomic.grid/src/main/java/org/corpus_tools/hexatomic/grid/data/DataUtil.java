package org.corpus_tools.hexatomic.grid.data;

import org.corpus_tools.salt.util.SaltUtil;

/**
 * Helper class providing utility functions for the underlying data.
 * 
 * @author Stephan Druskat (mail@sdruskat.net)
 */
public class DataUtil {

  /**
   * Checks whether the passed string can be split at Salt's namespace separator and returns the
   * namespace part of the original string, i.e., the string preceding the separator.
   * 
   * @param qNameString The string to extract the namespace from, this should be a valid qualified
   *        annotation name in Salt
   * @return the part of the passed string preceding the Salt namespace separator, or
   *         <code>null</code>.
   */
  public static String getNamespaceFromQNameString(String qNameString) {
    String[] splitValue = qNameString.split(SaltUtil.NAMESPACE_SEPERATOR);
    if (splitValue.length == 2) {
      return splitValue[0];
    } else {
      return null;
    }
  }

  /**
   * @param qNameString The string to extract the namespace from, this should be a valid qualified
   *        annotation name in Salt
   * @return the part of the passed string that follows the first instance of the Salt namespace
   *         operator, or the original parameter if it doesn't contain the Salt namespace operator
   */
  public static String getNameFromQNameString(String qNameString) {
    String[] splitValue = qNameString.split(SaltUtil.NAMESPACE_SEPERATOR);
    if (splitValue.length == 2) {
      return splitValue[1];
    } else {
      return splitValue[0];
    }
  }

}
