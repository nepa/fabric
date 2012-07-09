/** 07.07.2012 22:23 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * Interface for a single binding operation in a WSDL
 * document. This file defines all method signatures
 * for FBindingOperationImpl and contains a factory
 * mechanism to create such objects.
 *
 * @author seidel
 */
public interface FBindingOperation
{
  /*****************************************************************
   * FBindingOperationFactory inner class
   *****************************************************************/

  public static final class FBindingOperationFactory
  {
    /** Factory instance for Singleton pattern */
    private static FBindingOperationFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FBindingOperationFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FBindingOperationFactory object
     */
    public static synchronized FBindingOperationFactory getInstance()
    {
      if (null == FBindingOperationFactory.instance)
      {
        FBindingOperationFactory.instance = new FBindingOperationFactory();
      }

      return FBindingOperationFactory.instance;
    }

    /**
     * Create a new FBindingOperationImpl object with the
     * given name, type and messages.
     *
     * The binding operation type is solely used for implicit
     * message naming. It is not possible to retrieve the type
     * of a binding operation after creation, as the operation
     * type is not relevant for WSDL binding elements.
     *
     * @param bindingOperationName Name of the operation
     * @param bindingOperationType Type of the operation
     * @param inputMessage Input message of operation (optional)
     * @param outputMessage Output message of operation (optional)
     *
     * @return FBindingOperationImpl object
     */
    public FBindingOperationImpl create(final String bindingOperationName, final FOperationType bindingOperationType,
            final FBindingOperationInputMessage inputMessage, final FBindingOperationOutputMessage outputMessage)
    {
      return new FBindingOperationImpl(bindingOperationName, bindingOperationType, inputMessage, outputMessage);
    }

    /**
     * Create a new FBindingOperationImpl object with the
     * given name, type, messages and faults.
     *
     * The binding operation type is solely used for implicit
     * message naming. It is not possible to retrieve the type
     * of a binding operation after creation, as the operation
     * type is not relevant for WSDL binding elements.
     *
     * @param bindingOperationName Name of the operation
     * @param bindingOperationType Type of the operation
     * @param inputMessage Input message of operation (optional)
     * @param outputMessage Output message of operation (optional)
     * @param faultMessages Set of fault messages (optional)
     */
    public FBindingOperationImpl create(final String bindingOperationName, final FOperationType bindingOperationType,
            final FBindingOperationInputMessage inputMessage, final FBindingOperationOutputMessage outputMessage,
            final HashSet<FBindingOperationFaultMessage> faultMessages)
    {
      return new FBindingOperationImpl(bindingOperationName, bindingOperationType, inputMessage, outputMessage, faultMessages);
    }
  }

  /*****************************************************************
   * FBindingOperation outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FBindingOperationFactory factory = FBindingOperationFactory.getInstance();

  public void setBindingOperationName(final String bindingOperationName);
  public String getBindingOperationName();

  public void addPerOperationInformation(final FExtensibilityElement perOperationInformation);
  public void addPerOperationInformations(final HashSet<FExtensibilityElement> perOperationInformations);
  public void setPerOperationInformations(final HashSet<FExtensibilityElement> perOperationInformations);
  public HashSet<FExtensibilityElement> getPerOperationInformations();

  public void setInputMessage(final FBindingOperationInputMessage bindingOperationInputMessage);
  public FBindingOperationInputMessage getInputMessage();

  public void setOutputMessage(final FBindingOperationOutputMessage bindingOperationOutputMessage);
  public FBindingOperationOutputMessage getOutputMessage();

  public void addFaultMessage(final FBindingOperationFaultMessage bindingOperationFaultMessage);
  public void addFaultMessages(final HashSet<FBindingOperationFaultMessage> bindingOperationFaultMessages);
  public void setFaultMessages(final HashSet<FBindingOperationFaultMessage> bindingOperationFaultMessages);
  public HashSet<FBindingOperationFaultMessage> getFaultMessages();
  public FBindingOperationFaultMessage getFaultMessage(final String messageName);
}
