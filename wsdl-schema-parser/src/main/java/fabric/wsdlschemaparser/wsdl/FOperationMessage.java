/** 05.07.2012 19:40 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * Abstract base class for all operation messages, that
 * is input, output and fault messages as described in
 * the WSDL 1.1 specification.
 *
 * @author seidel
 */
abstract public class FOperationMessage extends FWSDLElement
{
  /** Name of the operation message */
  protected String operationMessageName;

  /** QName value of the 'message' attribute */
  protected QName messageAttribute;

  /**
   * Set name of the operation message.
   *
   * @param operationMessageName Operation message name
   */
  public void setOperationMessageName(final String operationMessageName)
  {
    this.operationMessageName = operationMessageName;
  }

  /**
   * Get name of the operation message.
   *
   * @return Operation message name
   */
  public String getOperationMessageName()
  {
    return this.operationMessageName;
  }

  /**
   * Set QName value of the 'message' attribute, which
   * refers to the type of the message.
   *
   * @param messageAttribute QName value of the 'message'
   * attribute
   */
  public void setMessageAttribute(final QName messageAttribute)
  {
    this.messageAttribute = messageAttribute;
  }

  /**
   * Get QName value of the 'message' attribute, which
   * refers to the type of the message.
   *
   * @return QName value of the 'message' attribute
   */
  public QName getMessageAttribute()
  {
    return this.messageAttribute;
  }

  /**
   * Create a human-readable form of the operation message.
   * That is print the local part of its QName value (type)
   * and message name.
   *
   * @return String representation of FOperationMessage
   * object
   */
  @Override
  public String toString()
  {
    return this.messageAttribute.getLocalPart() + " " + this.operationMessageName;
  }
}
