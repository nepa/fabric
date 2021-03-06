/** 19.07.2012 11:31 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a single webservice operation defined
 * in the port type bindings of a WSDL document. Each method
 * may consist of an input, an output and multiple fault
 * messages, depending on its type (one-way, request-response,
 * solicit-response or notification).
 *
 * @author seidel
 */
public class FBindingOperationImpl extends FWSDLElement implements FBindingOperation
{
  /** Name of the binding operation */
  private String bindingOperationName;

  /** Extensibility elements with per-operation information */
  private Set<FExtensibilityElement> perOperationInformations;

  /** Binding operation input message */
  private FBindingOperationInputMessage inputMessage;

  /** Binding operation output message */
  private FBindingOperationOutputMessage outputMessage;

  /** Set of binding operation fault messages */
  private Set<FBindingOperationFaultMessage> faultMessages;

  /**
   * Parameterized constructor will create a new webservice
   * operation for the port type binding of a WSDL document
   * with the given name, type and messages.
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
   * @throws IllegalArgumentException Invalid operation type
   */
  public FBindingOperationImpl(final String bindingOperationName, final FOperationType bindingOperationType,
          final FBindingOperationInputMessage inputMessage, final FBindingOperationOutputMessage outputMessage) throws IllegalArgumentException
  {
    this.bindingOperationName = bindingOperationName;
    this.perOperationInformations = new HashSet<FExtensibilityElement>();
    this.faultMessages = new HashSet<FBindingOperationFaultMessage>();

    // One-way operation
    if (FOperationType.ONE_WAY == bindingOperationType)
    {
      // Set name to 'operation name', if none is set
      if (null == inputMessage.getBindingOperationMessageName())
      {
        inputMessage.setBindingOperationMessageName(bindingOperationName);
      }

      this.inputMessage = inputMessage;
    }
    // Request-response operation
    else if (FOperationType.REQUEST_RESPONSE == bindingOperationType)
    {
      // Set name to 'Request', if none is set
      if (null == inputMessage.getBindingOperationMessageName())
      {
        inputMessage.setBindingOperationMessageName("Request");
      }
      // Set name to 'Response', if none is set
      if (null == outputMessage.getBindingOperationMessageName())
      {
        outputMessage.setBindingOperationMessageName("Response");
      }

      this.inputMessage = inputMessage;
      this.outputMessage = outputMessage;
    }
    // Solicit-response operation
    else if (FOperationType.SOLICIT_RESPONSE == bindingOperationType)
    {
      // Set name to 'Solicit', if none is set
      if (null == outputMessage.getBindingOperationMessageName())
      {
        outputMessage.setBindingOperationMessageName("Solicit");
      }
      // Set name to 'Response', if none is set
      if (null == inputMessage.getBindingOperationMessageName())
      {
        inputMessage.setBindingOperationMessageName("Response");
      }

      this.outputMessage = outputMessage;
      this.inputMessage = inputMessage;
    }
    // Notification operation
    else if (FOperationType.NOTIFICATION == bindingOperationType)
    {
      // Set name to 'operation name', if none is set
      if (null == outputMessage.getBindingOperationMessageName())
      {
        outputMessage.setBindingOperationMessageName(bindingOperationName);
      }

      this.outputMessage = outputMessage;
    }
    // Invalid operation type
    else
    {
      throw new IllegalArgumentException(String.format("Type of webservice operation '%s' is invalid.", bindingOperationName));
    }
  }

  /**
   * Parameterized constructor will create a new webservice
   * operation for the port type binding of a WSDL document
   * with the given name, type, messages and faults.
   * 
   * For more information see documentation of other constructor.
   *
   * @param bindingOperationName Name of the operation
   * @param bindingOperationType Type of the operation
   * @param inputMessage Input message of operation (optional)
   * @param outputMessage Output message of operation (optional)
   * @param faultMessages Set of fault messages (optional)
   */
  public FBindingOperationImpl(final String bindingOperationName, final FOperationType bindingOperationType,
          final FBindingOperationInputMessage inputMessage, final FBindingOperationOutputMessage outputMessage,
          final HashSet<FBindingOperationFaultMessage> faultMessages)
  {
    this(bindingOperationName, bindingOperationType, inputMessage, outputMessage);
    this.faultMessages = faultMessages;
  }

  /**
   * Set name of the binding operation.
   *
   * @param bindingOperationName Name of the binding operation
   */
  @Override
  public void setBindingOperationName(final String bindingOperationName)
  {
    this.bindingOperationName = bindingOperationName;
  }

  /**
   * Get name of the binding operation.
   *
   * @return Name of the binding operation
   */
  @Override
  public String getBindingOperationName()
  {
    return this.bindingOperationName;
  }

  /**
   * Add extensibility element with per-operation information.
   *
   * @param perOperationInformation Extensibility element with
   * per-operation information
   */
  @Override
  public void addPerOperationInformation(final FExtensibilityElement perOperationInformation)
  {
    this.perOperationInformations.add(perOperationInformation);
  }

  /**
   * Add a set of extensibility elements with per-operation
   * information.
   *
   * @param perOperationInformations Set of extensibility
   * elements with per-operation information
   */
  @Override
  public void addPerOperationInformations(final HashSet<FExtensibilityElement> perOperationInformations)
  {
    this.perOperationInformations.addAll(perOperationInformations);
  }

  /**
   * Set extensibility elements with per-operation information.
   *
   * @param perOperationInformations Set of extensibility
   * elements with per-operation information
   */
  @Override
  public void setPerOperationInformations(final HashSet<FExtensibilityElement> perOperationInformations)
  {
    this.perOperationInformations = perOperationInformations;
  }

  /**
   * Get extensibility elements with per-operation information.
   *
   * @return Set of extensibility elements with per-operation
   * information
   */
  @Override
  public HashSet<FExtensibilityElement> getPerOperationInformations()
  {
    return (HashSet<FExtensibilityElement>)this.perOperationInformations;
  }

  /**
   * Return the number of extensibility elements with
   * per-operation information that are defined within
   * the current binding operation object.
   *
   * @return Number of extensibility elements with
   * per-operation information
   */
  @Override
  public int perOperationInformationCount()
  {
    return this.perOperationInformations.size();
  }

  /**
   * Set input message of the binding operation.
   *
   * @param bindingOperationInputMessage Input message
   */
  @Override
  public void setInputMessage(final FBindingOperationInputMessage bindingOperationInputMessage)
  {
    this.inputMessage = bindingOperationInputMessage;
  }

  /**
   * Get input message of the binding operation (if any).
   *
   * @return Input message or 'null' if none is set
   */
  @Override
  public FBindingOperationInputMessage getInputMessage()
  {
    return this.inputMessage;
  }

  /**
   * Set output message of the binding operation.
   *
   * @param bindingOperationOutputMessage Output message
   */
  @Override
  public void setOutputMessage(final FBindingOperationOutputMessage bindingOperationOutputMessage)
  {
    this.outputMessage = bindingOperationOutputMessage;
  }

  /**
   * Get output message of the binding operation (if any).
   *
   * @return Output message or 'null' if none is set
   */
  @Override
  public FBindingOperationOutputMessage getOutputMessage()
  {
    return this.outputMessage;
  }

  /**
   * Add fault message to the binding operation.
   *
   * @param bindingOperationFaultMessage Fault message
   */
  @Override
  public void addFaultMessage(final FBindingOperationFaultMessage bindingOperationFaultMessage)
  {
    this.faultMessages.add(bindingOperationFaultMessage);
  }

  /**
   * Add a set of multiple fault messages to the binding
   * operation.
   *
   * @param bindingOperationFaultMessages Set of fault messages
   */
  @Override
  public void addFaultMessages(final HashSet<FBindingOperationFaultMessage> bindingOperationFaultMessages)
  {
    this.faultMessages.addAll(bindingOperationFaultMessages);
  }

  /**
   * Set fault messages of the binding operation.
   *
   * @param bindingOperationFaultMessages Set of fault messages
   */
  @Override
  public void setFaultMessages(final HashSet<FBindingOperationFaultMessage> bindingOperationFaultMessages)
  {
    this.faultMessages = bindingOperationFaultMessages;
  }

  /**
   * Get fault messages of the binding operation (if any).
   *
   * @return Set of fault messages
   */
  @Override
  public HashSet<FBindingOperationFaultMessage> getFaultMessages()
  {
    return (HashSet<FBindingOperationFaultMessage>)this.faultMessages;
  }

  /**
   * Get a specific fault message of the binding operation
   * (if it exists).
   *
   * @param messageName Name of the fault message
   *
   * @return Fault message with the given name or 'null'
   * if it does not exist
   */
  @Override
  public FBindingOperationFaultMessage getFaultMessage(final String messageName)
  {
    for (FBindingOperationFaultMessage message: this.faultMessages)
    {
      if (messageName.equals(message.getBindingOperationMessageName()))
      {
        return message;
      }
    }

    return null;
  }

  /**
   * Return the number of fault messages that are defined
   * within the current binding operation object.
   *
   * @return Number of fault messages
   */
  @Override
  public int faultMessageCount()
  {
    return this.faultMessages.size();
  }

  /**
   * Print a human-readable form of the binding operation.
   *
   * @return String representation of FBindingOperationImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    // Concatenate names of all fault messages
    String faults = "";
    for (FBindingOperationFaultMessage faultMessage: this.faultMessages)
    {
      faults += faultMessage.getBindingOperationMessageName() + ", ";
    }

    // Remove last comma and space from fault message string
    if (faults.length() > 1)
    {
      faults = faults.substring(0, faults.length() - 2);
    }

    // Print method name, input/output arguments and faults
    result += String.format("Operation: %s(%s): %s%s", this.bindingOperationName,
            (null != this.inputMessage ? this.inputMessage.toString() : "-"),
            (null != this.outputMessage ? this.outputMessage.toString() : "-"),
            (("").equals(faults) ? "" : " [Faults: " + faults + "]"));

    return result;
  }

  /**
   * Compare binding operation object with another object
   * of the same type, based on the attributes of the
   * current class.
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
      FBindingOperationImpl otherBindingOperation = (FBindingOperationImpl)object;

      // Attribute values are equal
      if (this.bindingOperationName.equals(otherBindingOperation.getBindingOperationName()) &&
          this.perOperationInformations.equals(otherBindingOperation.getPerOperationInformations()) &&

          // Input message is optional, so it may be 'null'
          (null == this.inputMessage && null == otherBindingOperation.getInputMessage() ||
           null != this.inputMessage && this.inputMessage.equals(otherBindingOperation.getInputMessage())) &&

          // Output message is optional, so it may be 'null'
          (null == this.outputMessage && null == otherBindingOperation.getOutputMessage() ||
           null != this.outputMessage && this.outputMessage.equals(otherBindingOperation.getOutputMessage())) &&

          // Fault messages are optional, so they may be 'null'
          (null == this.faultMessages && null == otherBindingOperation.getFaultMessages() ||
           null != this.faultMessages && this.faultMessages.equals(otherBindingOperation.getFaultMessages())))
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
    int hash = 5;

    hash = 17 * hash + (this.bindingOperationName != null ? this.bindingOperationName.hashCode() : 0);
    hash = 17 * hash + (this.perOperationInformations != null ? this.perOperationInformations.hashCode() : 0);
    hash = 17 * hash + (this.inputMessage != null ? this.inputMessage.hashCode() : 0);
    hash = 17 * hash + (this.outputMessage != null ? this.outputMessage.hashCode() : 0);
    hash = 17 * hash + (this.faultMessages != null ? this.faultMessages.hashCode() : 0);

    return hash;
  }
}
