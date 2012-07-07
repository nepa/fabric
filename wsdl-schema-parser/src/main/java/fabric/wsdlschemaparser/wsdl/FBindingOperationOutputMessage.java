/** 03.07.2012 17:08 */
package fabric.wsdlschemaparser.wsdl;

import java.util.HashSet;

/**
 * This class represents an output message for operations
 * in WSDL port type bindings. It basically is a container
 * for the name of the message.
 *
 * @author seidel
 */
public class FBindingOperationOutputMessage extends FBindingOperationMessage
{
  /**
   * Parameterized constructor creates a new binding operation
   * output message with the given message name.
   *
   * @param bindingOperationMessageName Name of the binding
   * operation message
   */
  public FBindingOperationOutputMessage(final String bindingOperationMessageName)
  {
    this.bindingOperationMessageName = bindingOperationMessageName;
    this.perMessageInformations = new HashSet<FExtensibilityElement>();
  }
}
