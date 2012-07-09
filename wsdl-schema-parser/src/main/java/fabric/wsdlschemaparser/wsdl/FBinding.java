/** 07.07.2012 22:03 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;
import javax.xml.namespace.QName;

/**
 * Interface for a single binding in a WSDL document. This
 * file defines all method signatures for FBindingImpl and
 * contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface FBinding
{
  /*****************************************************************
   * FBindingFactory inner class
   *****************************************************************/

  public static final class FBindingFactory
  {
    /** Factory instance for Singleton pattern */
    private static FBindingFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FBindingFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FBindingFactory object
     */
    public static synchronized FBindingFactory getInstance()
    {
      if (null == FBindingFactory.instance)
      {
        FBindingFactory.instance = new FBindingFactory();
      }

      return FBindingFactory.instance;
    }

    /**
     * Create a new FBindingImpl object with the given binding
     * name and port type reference.
     *
     * @param bindingName Name of the binding
     * @param portTypeReference QName of referenced port type
     *
     * @return FBindingImpl object
     */
    public FBindingImpl create(final String bindingName, final QName portTypeReference)
    {
      return new FBindingImpl(bindingName, portTypeReference);
    }
  }

  /*****************************************************************
   * FBinding outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FBindingFactory factory = FBindingFactory.getInstance();

  public void setBindingName(final String bindingName);
  public String getBindingName();

  public void setPortTypeReference(final QName portTypeReference);
  public QName getPortTypeReference();

  public void addPerBindingInformation(final FExtensibilityElement perBindingInformation);
  public void addPerBindingInformations(final HashSet<FExtensibilityElement> perBindingInformations);
  public void setPerBindingInformations(final HashSet<FExtensibilityElement> perBindingInformations);
  public HashSet<FExtensibilityElement> getPerBindingInformations();

  public void addBindingOperation(final FBindingOperation operation);
  public void addBindingOperations(final HashSet<FBindingOperation> operations);
  public void setBindingOperations(final HashSet<FBindingOperation> operations);
  public HashSet<FBindingOperation> getBindingOperations();

  public int operationCount();
}
