/** 08.07.2012 00:26 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * Interface for a single port type element in a WSDL
 * document. This file defines all method signatures
 * for FPortTypeImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface FPortType
{
  /*****************************************************************
   * FPortTypeFactory inner class
   *****************************************************************/

  public static final class FPortTypeFactory
  {
    /** Factory instance for Singleton pattern */
    private static FPortTypeFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FPortTypeFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FPortTypeFactory object
     */
    public static synchronized FPortTypeFactory getInstance()
    {
      if (null == FPortTypeFactory.instance)
      {
        FPortTypeFactory.instance = new FPortTypeFactory();
      }

      return FPortTypeFactory.instance;
    }

    /**
     * Create a new FPortTypeImpl object with the given
     * port type name.
     *
     * @param portTypeName Name of the service interface
     *
     * @return FPortTypeImpl object
     */
    public FPortTypeImpl create(final String portTypeName)
    {
      return new FPortTypeImpl(portTypeName);
    }
  }

  /*****************************************************************
   * FPortType outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FPortTypeFactory factory = FPortTypeFactory.getInstance();

  public void setPortTypeName(final String portTypeName);
  public String getPortTypeName();

  public void addOperation(final FOperation operation);
  public void addOperations(final HashSet<FOperation> operations);
  public void setOperations(final HashSet<FOperation> operations);
  public HashSet<FOperation> getOperations();

  public int operationCount();
}
