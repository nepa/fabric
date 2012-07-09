/** 08.07.2012 00:16 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * Interface for a single port definition in a WSDL
 * document. This file defines all method signatures
 * for FPortImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface FPort
{
  /*****************************************************************
   * FPortFactory inner class
   *****************************************************************/

  public static final class FPortFactory
  {
    /** Factory instance for Singleton pattern */
    private static FPortFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FPortFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FPortFactory object
     */
    public static synchronized FPortFactory getInstance()
    {
      if (null == FPortFactory.instance)
      {
        FPortFactory.instance = new FPortFactory();
      }

      return FPortFactory.instance;
    }

    /**
     * Create a new FPortImpl object with the given name,
     * binding reference and address information.
     *
     * @param portName Name of the port
     * @param bindingReference QName of referenced binding
     * @param addressInformation Extensibility element with
     * address information
     *
     * @return FPortImpl object
     */
    public FPortImpl create(final String portName, final QName bindingReference, final FExtensibilityElement addressInformation)
    {
      return new FPortImpl(portName, bindingReference, addressInformation);
    }
  }

  /*****************************************************************
   * FPort outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FPortFactory factory = FPortFactory.getInstance();

  public void setPortName(final String portName);
  public String getPortName();

  public void setBindingReference(final QName bindingReference);
  public QName getBindingReference();

  public void setAddressInformation(final FExtensibilityElement addressInformation);
  public FExtensibilityElement getAddressInformation();
}
