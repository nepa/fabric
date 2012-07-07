/** 02.07.2012 16:38 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * This class represents an input message for webservice
 * operations. It basically is a container for the name of
 * the message as well as the QName value of the 'message'
 * attribute (type).
 *
 * @author seidel
 */
public class FOperationInputMessage extends FOperationMessage
{
  /**
   * Parameterized constructor creates a new input message with
   * the given message name and QName value for the 'message'
   * attribute.
   *
   * @param operationMessageName Name of the operation message
   * @param messageAttribute QName value of 'message' attribute
   */
  public FOperationInputMessage(final String operationMessageName, final QName messageAttribute)
  {
    this.operationMessageName = operationMessageName;
    this.messageAttribute = messageAttribute;
  }
}
