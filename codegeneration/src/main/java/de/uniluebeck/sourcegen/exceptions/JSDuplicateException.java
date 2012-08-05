package de.uniluebeck.sourcegen.exceptions;

/**
 * This exception can be thrown, if duplicate JavaScript
 * objects are added to an element in the Code Generation
 * Framework.
 *
 * @author seidel
 */
public class JSDuplicateException extends ValidationException
{
  /**
   * Create error message from String object.
   *
   * @param message Error message
   */
  public JSDuplicateException(final String message)
  {
    super(message);
  }
}
