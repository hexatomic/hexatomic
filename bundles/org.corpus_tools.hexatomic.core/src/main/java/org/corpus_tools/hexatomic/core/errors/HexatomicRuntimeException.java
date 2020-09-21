package org.corpus_tools.hexatomic.core.errors;

/**
 * A generic runtime exception for errors thrown from the Hexatomic code base (as opposed to be
 * originated from external libraries).
 * 
 * @author Thomas Krause (krauseto@hu-berlin.de)
 *
 */
public class HexatomicRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a runtime exception with the given detailed message.
   * 
   * @param msg The message for this exception.
   */
  public HexatomicRuntimeException(String msg) {
    super(msg);
  }

}
