/** 17.07.2012 12:52 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * Interface for a single webservice operation in a WSDL
 * document. This file defines all method signatures
 * for FOperationImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface FOperation
{
  /*****************************************************************
   * FOperationFactory inner class
   *****************************************************************/

  public static final class FOperationFactory
  {
    /** Factory instance for Singleton pattern */
    private static FOperationFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private FOperationFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return FOperationFactory object
     */
    public static synchronized FOperationFactory getInstance()
    {
      if (null == FOperationFactory.instance)
      {
        FOperationFactory.instance = new FOperationFactory();
      }

      return FOperationFactory.instance;
    }

    /**
     * Create a new FOperationImpl object with the given name,
     * type and messages.
     *
     * @param operationName Name of the operation
     * @param operationType Type of the operation
     * @param inputMessage Input message of operation (optional)
     * @param outputMessage Output message of operation (optional)
     *
     * @return FOperationImpl object
     */
    public FOperationImpl create(final String operationName, final FOperationType operationType,
            final FOperationInputMessage inputMessage, final FOperationOutputMessage outputMessage)
    {
      return new FOperationImpl(operationName, operationType, inputMessage, outputMessage);
    }

    /**
     * Create a new FOperationImpl object with the given name,
     * type, messages and faults.
     *
     * @param operationName Name of the operation
     * @param operationType Type of the operation
     * @param inputMessage Input message of operation (optional)
     * @param outputMessage Output message of operation (optional)
     * @param faultMessages List of fault messages (optional)
     *
     * @return FOperationImpl object
     */
    public FOperationImpl create(final String operationName, final FOperationType operationType,
            final FOperationInputMessage inputMessage, final FOperationOutputMessage outputMessage,
            final HashSet<FOperationFaultMessage> faultMessages)
    {
      return new FOperationImpl(operationName, operationType, inputMessage, outputMessage, faultMessages);
    }
  }

  /*****************************************************************
   * FOperation outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final FOperationFactory factory = FOperationFactory.getInstance();

  public void setOperationName(final String operationName);
  public String getOperationName();

  public void setOperationType(final FOperationType operationType);
  public FOperationType getOperationType();

  public void setInputMessage(final FOperationInputMessage operationInputMessage);
  public FOperationInputMessage getInputMessage();

  public void setOutputMessage(final FOperationOutputMessage operationOutputMessage);
  public FOperationOutputMessage getOutputMessage();

  public void addFaultMessage(final FOperationFaultMessage operationFaultMessage);
  public void addFaultMessages(final HashSet<FOperationFaultMessage> operationFaultMessages);
  public void setFaultMessages(final HashSet<FOperationFaultMessage> operationFaultMessages);
  public HashSet<FOperationFaultMessage> getFaultMessages();
  public FOperationFaultMessage getFaultMessage(final String messageName);

  public int faultMessageCount();
}
