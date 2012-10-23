/** 07.10.2012 18:16 */
package fabric.module.midgen4j.exceptions;

/**
 * Base class for custom exceptions thrown in the MidGen4J module.
 *
 * @author seidel
 */
public class MidGen4JException extends Exception
{
  /**
   * Default constructor for exception.
   */
  public MidGen4JException()
  {
    // Empty implementation
  }

  /**
   * Parameterized constructor for exception.
   *
   * @param message Error message
   */
  public MidGen4JException(String message)
  {
    super(message);
  }
}
