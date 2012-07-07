/** 02.07.2012 16:38 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * This class represents a fault message for webservice
 * operations. It basically is a container for the name of
 * the message as well as the QName value of the 'message'
 * attribute (type).
 *
 * @author seidel
 */
public class FOperationFaultMessage extends FOperationMessage
{
  /**
   * Parameterized constructor creates a new fault message with
   * the given message name and QName value for the 'message'
   * attribute.
   *
   * @param operationMessageName Name of the operation message
   * @param messageAttribute QName value of 'message' attribute
   */
  public FOperationFaultMessage(final String operationMessageName, final QName messageAttribute)
  {
    this.operationMessageName = operationMessageName;
    this.messageAttribute = messageAttribute;
  }
}
