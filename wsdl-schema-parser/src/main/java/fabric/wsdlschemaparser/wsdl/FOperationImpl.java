/** 19.07.2012 11:29 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a single webservice operation
 * with its input, output and fault messages.
 *
 * @author seidel
 */
public class FOperationImpl extends FWSDLElement implements FOperation
{
  /** Name of the operation */
  private String operationName;

  /** Type of the operation */
  private FOperationType operationType;

  /** Operation input message */
  private FOperationInputMessage inputMessage;

  /** Operation output message */
  private FOperationOutputMessage outputMessage;

  /** Set of operation fault messages */
  private Set<FOperationFaultMessage> faultMessages;

  /**
   * Parameterized constructor will create a new webservice
   * operation with the given name, type and messages.
   *
   * WSDL features implicit naming of webservice messages,
   * if names have not been defined explicitly. The method
   * takes care of this task automatically.
   *
   * For more information see:
   *   http://www.w3.org/TR/wsdl#_names
   *
   * A list of all operation types can be found in the
   * documentation of setOperationType().
   *
   * @param operationName Name of the operation
   * @param operationType Type of the operation
   * @param inputMessage Input message of operation (optional)
   * @param outputMessage Output message of operation (optional)
   *
   * @throws IllegalArgumentException Invalid operation type
   */
  public FOperationImpl(final String operationName, final FOperationType operationType,
          final FOperationInputMessage inputMessage, final FOperationOutputMessage outputMessage) throws IllegalArgumentException
  {
    // TODO: Do not access inputMessage/outputMessage unless we know it is not null!

    this.operationName = operationName;
    this.operationType = operationType;
    this.faultMessages = new HashSet<FOperationFaultMessage>();

    // One-way operation
    if (FOperationType.ONE_WAY == operationType)
    {
      // Set name to 'operation name', if none is set
      if (null == inputMessage.getOperationMessageName())
      {
        inputMessage.setOperationMessageName(operationName);
      }

      this.inputMessage = inputMessage;
    }
    // Request-response operation
    else if (FOperationType.REQUEST_RESPONSE == operationType)
    {
      // Set name to 'Request', if none is set
      if (null == inputMessage.getOperationMessageName())
      {
        inputMessage.setOperationMessageName("Request");
      }
      // Set name to 'Response', if none is set
      if (null == outputMessage.getOperationMessageName())
      {
        outputMessage.setOperationMessageName("Response");
      }

      this.inputMessage = inputMessage;
      this.outputMessage = outputMessage;
    }
    // Solicit-response operation
    else if (FOperationType.SOLICIT_RESPONSE == operationType)
    {
      // Set name to 'Solicit', if none is set
      if (null == outputMessage.getOperationMessageName())
      {
        outputMessage.setOperationMessageName("Solicit");
      }
      // Set name to 'Response', if none is set
      if (null == inputMessage.getOperationMessageName())
      {
        inputMessage.setOperationMessageName("Response");
      }

      this.outputMessage = outputMessage;
      this.inputMessage = inputMessage;
    }
    // Notification operation
    else if (FOperationType.NOTIFICATION == operationType)
    {
      // Set name to 'operation name', if none is set
      if (null == outputMessage.getOperationMessageName())
      {
        outputMessage.setOperationMessageName(operationName);
      }

      this.outputMessage = outputMessage;
    }
    // Invalid operation type
    else
    {
      throw new IllegalArgumentException(String.format("Type of webservice operation '%s' is invalid.", operationName));
    }
  }

  /**
   * Parameterized constructor will create a new webservice
   * operation with the given name, type, messages and faults.
   *
   * For more information see documentation of other constructor.
   *
   * @param operationName Name of the operation
   * @param operationType Type of the operation
   * @param inputMessage Input message of operation (optional)
   * @param outputMessage Output message of operation (optional)
   * @param faultMessages List of fault messages (optional)
   */
  public FOperationImpl(final String operationName, final FOperationType operationType,
          final FOperationInputMessage inputMessage, final FOperationOutputMessage outputMessage,
          final HashSet<FOperationFaultMessage> faultMessages)
  {
    this(operationName, operationType, inputMessage, outputMessage);
    this.faultMessages = faultMessages;
  }

  /**
   * Set name of the webservice operation.
   *
   * @param operationName Name of the operation
   */
  @Override
  public void setOperationName(final String operationName)
  {
    this.operationName = operationName;
  }

  /**
   * Get name of the webservice operation.
   *
   * @return Name of the operation
   */
  @Override
  public String getOperationName()
  {
    return this.operationName;
  }

  /**
   * Set type of the webservice operation. WSDL defines
   * four different method types:
   *
   *   - One-way:
   *       Endpoint receives a message
   *
   *   - Request-response:
   *       Endpoint receives a message and responds
   *
   *   - Solicit-response:
   *       Endpoint sends a message and receives a response
   *
   *   - Notification:
   *       Endpoint sends a message
   *
   * @param operationType Type of the operation
   */
  @Override
  public void setOperationType(final FOperationType operationType)
  {
    this.operationType = operationType;
  }

  /**
   * Get type of the webservice operation. WSDL defines
   * four different method types. For more details see
   * the documentation of setOperationType().
   *
   * @return Type of the operation
   */
  @Override
  public FOperationType getOperationType()
  {
    return this.operationType;
  }

  /**
   * Set input message of the operation.
   *
   * @param operationInputMessage Input message
   */
  @Override
  public void setInputMessage(final FOperationInputMessage operationInputMessage)
  {
    this.inputMessage = operationInputMessage;
  }

  /**
   * Get input message of the operation (if any).
   *
   * @return Input message or 'null' if none is set
   */
  @Override
  public FOperationInputMessage getInputMessage()
  {
    return this.inputMessage;
  }

  /**
   * Set output message of the operation.
   *
   * @param operationOutputMessage Output message
   */
  @Override
  public void setOutputMessage(final FOperationOutputMessage operationOutputMessage)
  {
    this.outputMessage = operationOutputMessage;
  }

  /**
   * Get output message of the operation (if any).
   *
   * @return Output message or 'null' if none is set
   */
  @Override
  public FOperationOutputMessage getOutputMessage()
  {
    return this.outputMessage;
  }

  /**
   * Add fault message to the operation.
   *
   * @param operationFaultMessage Fault message
   */
  @Override
  public void addFaultMessage(final FOperationFaultMessage operationFaultMessage)
  {
    this.faultMessages.add(operationFaultMessage);
  }

  /**
   * Add a set of multiple fault messages to the operation.
   *
   * @param operationFaultMessages Set of fault messages
   */
  @Override
  public void addFaultMessages(final HashSet<FOperationFaultMessage> operationFaultMessages)
  {
    this.faultMessages.addAll(operationFaultMessages);
  }

  /**
   * Set fault messages of the operation.
   *
   * @param operationFaultMessages Set of fault messages
   */
  @Override
  public void setFaultMessages(final HashSet<FOperationFaultMessage> operationFaultMessages)
  {
    this.faultMessages = operationFaultMessages;
  }

  /**
   * Get fault messages of the operation (if any).
   *
   * @return Set of fault messages
   */
  @Override
  public HashSet<FOperationFaultMessage> getFaultMessages()
  {
    return (HashSet<FOperationFaultMessage>)this.faultMessages;
  }

  /**
   * Get a specific fault message of the operation (if it exists).
   *
   * @param messageName Name of the fault message
   *
   * @return Fault message with the given name or 'null'
   * if it does not exist
   */
  @Override
  public FOperationFaultMessage getFaultMessage(final String messageName)
  {
    for (FOperationFaultMessage message: this.faultMessages)
    {
      if (messageName.equals(message.getOperationMessageName()))
      {
        return message;
      }
    }

    return null;
  }

  /**
   * Return the number of fault messages that are
   * defined within the current operation object.
   *
   * @return Number of fault messages
   */
  @Override
  public int faultMessageCount()
  {
    return this.faultMessages.size();
  }

  /**
   * Print a human-readable form of the webservice operation.
   * This is the method name, input/output and fault messages
   * (if any).
   *
   * @return String representation of FOperationImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    // Concatenate names of all fault messages
    String faults = "";
    for (FOperationFaultMessage faultMessage: this.faultMessages)
    {
      faults += faultMessage.getOperationMessageName() + ", ";
    }

    // Remove last comma and space from fault message string
    if (faults.length() > 1)
    {
      faults = faults.substring(0, faults.length() - 2);
    }

    // Print method name, input/output arguments and faults
    result += String.format("Operation: %s(%s): %s%s", this.operationName,
            (null != this.inputMessage ? this.inputMessage.toString() : "-"),
            (null != this.outputMessage ? this.outputMessage.getMessageAttribute().getLocalPart() : "-"),
            (("").equals(faults) ? "" : " [Faults: " + faults + "]"));

    return result;
  }

  /**
   * Compare operation object with another object of the same
   * type, based on the attributes of the current class.
   *
   * @param object Other object to compare with
   *
   * @return True if objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object object)
  {
    // Other object is null
    if (null == object)
    {
      return false;
    }

    // Catch self-comparison
    if (this == object)
    {
      return true;
    }

    // Objects are of the same class
    if (this.getClass() == object.getClass())
    {
      // Safe cast to desired type
      FOperationImpl otherOperation = (FOperationImpl)object;

      // Attribute values are equal
      if (this.operationName.equals(otherOperation.getOperationName()) &&
          this.operationType.equals(otherOperation.getOperationType()) &&

          // Input message is optional, so it may be 'null'
          (null == this.inputMessage && null == otherOperation.getInputMessage() ||
           null != this.inputMessage && this.inputMessage.equals(otherOperation.getInputMessage())) &&

          // Output message is optional, so it may be 'null'
          (null == this.outputMessage && null == otherOperation.getOutputMessage() ||
           null != this.outputMessage && this.outputMessage.equals(otherOperation.getOutputMessage())) &&

          // Fault messages are optional, so they may be 'null'
          (null == this.faultMessages && null == otherOperation.getFaultMessages() ||
           null != this.faultMessages && this.faultMessages.equals(otherOperation.getFaultMessages())))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Generate hash code for object comparison based on
   * the attributes of the current class.
   *
   * @return Hash code for current object
   */
  @Override
  public int hashCode()
  {
    int hash = 7;

    hash = 29 * hash + (this.operationName != null ? this.operationName.hashCode() : 0);
    hash = 29 * hash + (this.operationType != null ? this.operationType.hashCode() : 0);
    hash = 29 * hash + (this.inputMessage != null ? this.inputMessage.hashCode() : 0);
    hash = 29 * hash + (this.outputMessage != null ? this.outputMessage.hashCode() : 0);
    hash = 29 * hash + (this.faultMessages != null ? this.faultMessages.hashCode() : 0);

    return hash;
  }
}
