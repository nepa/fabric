/** 17.07.2012 14:56 */
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

  /**
   * Compare operation message object with another object
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
      FOperationMessage otherOperationMessage = (FOperationMessage)object;

      // Attribute values are equal
      if (this.operationMessageName.equals(otherOperationMessage.getOperationMessageName()) &&
          this.messageAttribute.equals(otherOperationMessage.getMessageAttribute()))
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

    hash = 47 * hash + (this.operationMessageName != null ? this.operationMessageName.hashCode() : 0);
    hash = 47 * hash + (this.messageAttribute != null ? this.messageAttribute.hashCode() : 0);

    return hash;
  }
}
