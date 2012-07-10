/** 11.07.2012 01:46 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * Interface for a single service definition in a WSDL
 * document. This file defines all method signatures
 * for FServiceImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface FService
{
  /*****************************************************************
   * FServiceFactory inner class
   *****************************************************************/

  public static final class FServiceFactory
  {
    /** Factory instance for Singleton pattern */
    private static FServiceFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FServiceFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FServiceFactory object
     */
    public static synchronized FServiceFactory getInstance()
    {
      if (null == FServiceFactory.instance)
      {
        FServiceFactory.instance = new FServiceFactory();
      }

      return FServiceFactory.instance;
    }

    /**
     * Create a new FServiceImpl object with the given
     * service name.
     *
     * @param serviceName Name of the service
     *
     * @return FServiceImpl object
     */
    public FServiceImpl create(final String serviceName)
    {
      return new FServiceImpl(serviceName);
    }
  }

  /*****************************************************************
   * FService outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FServiceFactory factory = FServiceFactory.getInstance();

  public void setServiceName(final String serviceName);
  public String getServiceName();

  public void addPort(final FPort port);
  public void addPorts(final HashSet<FPort> ports);
  public void setPorts(final HashSet<FPort> ports);
  public HashSet<FPort> getPorts();
  
  public int portCount();
}
