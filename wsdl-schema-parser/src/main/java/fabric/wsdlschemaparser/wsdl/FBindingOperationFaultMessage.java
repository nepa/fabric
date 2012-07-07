/** 03.07.2012 17:07 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * This class represents a fault message for operations
 * in WSDL port type bindings. It basically is a container
 * for the name of the message.
 *
 * @author seidel
 */
public class FBindingOperationFaultMessage extends FBindingOperationMessage
{
  /**
   * Parameterized constructor creates a new binding operation
   * fault message with the given message name.
   *
   * @param bindingOperationMessageName Name of the binding
   * operation message
   */
  public FBindingOperationFaultMessage(final String bindingOperationMessageName)
  {
    this.bindingOperationMessageName = bindingOperationMessageName;
    this.perMessageInformations = new HashSet<FExtensibilityElement>();
  }
}
