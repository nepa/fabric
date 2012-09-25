/** 18.09.2012 17:23 */
package fabric.module.midgen4j.rest.exceptions;

/**
 * Base class for custom exceptions thrown in the REST extension
 * of Fabric's MidGen4J module.
 *
 * @author seidel
 */
public class MidGen4JRESTException extends Exception
{
  /**
   * Default constructor for exception.
   */
  public MidGen4JRESTException()
  {
    // Empty implementation
  }

  /**
   * Parameterized constructor for exception.
   *
   * @param message Error message
   */
  public MidGen4JRESTException(String message)
  {
    super(message);
  }
}
