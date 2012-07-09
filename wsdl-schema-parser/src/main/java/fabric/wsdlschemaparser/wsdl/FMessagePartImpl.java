/** 10.07.2012 01:24 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;

/**
 * This class represents one part of a surrounding
 * WSDL message. The type of a message part can be
 * specified in two ways: Using the 'element' or
 * the 'type' attribute.
 *
 * If the type of a message part is defined using
 * the 'element' attribute, a message MAY contain
 * MULTIPLE message parts (but all parts must use
 * the 'element' attribute then). This means we do
 * not reference a XSD type, but an element of the
 * XML Schema.
 *
 * On the other hand, if the type is specified
 * using the 'type' attribute, the XSD type system
 * is addressed directly. This means we can use any
 * global type definition of the WSDL's embedded
 * XML Schema (may it be a simple or a complex type).
 * In this case a message MUST only contain ONE
 * message part.
 * 
 * @author seidel
 */
public class FMessagePartImpl extends FWSDLElement implements FMessagePart
{
  /**
   * Possible, mutually exclusive attributes for a message part.
   */
  private enum FAttributeType
  {
    NONE,
    ELEMENT,
    TYPE
  }

  /** Name of the message part */
  private String partName;

  /** QName value of the 'element' attribute */
  private QName elementAttribute;
  
  /** QName value of the 'type' attribute */
  private QName typeAttribute;

  /**
   * Parameterized constructor creates a new message part with
   * the given name and QName value. If the message part has
   * 'element' attribute set, pass 'true' as third parameter.
   * If the message part has 'type' attribute set, use 'false'
   * respectively.
   *
   * @param partName Name of the message part
   * @param type QName to determine type of message part
   * @param hasElementAttribute True if message part has
   * 'element' attribute set, false if 'type' attribute
   * is set
   */
  public FMessagePartImpl(final String partName, final QName type, final boolean hasElementAttribute)
  {
    this.partName = partName;

    // Part has 'element' attribute set
    if (hasElementAttribute)
    {
      this.elementAttribute = type;
    }
    // Part has 'type' attribute set
    else
    {
      this.typeAttribute = type;
    }
  }

  /**
   * Parameterized constructor creates a new message part with
   * the given name and QName value. The last two arguments are
   * mutually exclusive, so only set one of them (pass 'null'
   * for the unused one). The constructor automatically sets the
   * correct type for the message part then.
   *
   * @param partName Name of the message part
   * @param elementName QName value of the 'element' attribute
   * @param typeName QName value of the 'type' attribute
   */
  public FMessagePartImpl(final String partName, final QName elementName, final QName typeName)
  {
    this.partName = partName;

    // Set QName value of the correct member variable
    if (FAttributeType.ELEMENT == this.getAttributeType(elementName, typeName))
    {
      this.elementAttribute = elementName;
    }
    else
    {
      this.typeAttribute = typeName;
    }
  }

  /**
   * Set name of the message part.
   *
   * @param partName Name of message part
   */
  @Override
  public void setPartName(final String partName)
  {
    this.partName = partName;
  }

  /**
   * Get name of the message part.
   *
   * @return name of message part
   */
  @Override
  public String getPartName()
  {
    return this.partName;
  }

  /**
   * Set QName value of the 'element' attribute.
   *
   * @param elementName QName value of the 'element' attribute
   */
  @Override
  public void setElementName(final QName elementName)
  {
    this.elementAttribute = elementName;
  }

  /**
   * Get QName value of the 'element' attribute.
   *
   * @return QName value of the 'element' attribute
   */
  @Override
  public QName getElementName()
  {
    return this.elementAttribute;
  }

  /**
   * Set QName value of the 'type' attribute.
   *
   * @param typeName QName value of the 'type' attribute
   */
  @Override
  public void setTypeName(final QName typeName)
  {
    this.typeAttribute = typeName;
  }

  /**
   * Get QName value of the 'type' attribute.
   *
   * @return QName value of the 'type' attribute
   */
  @Override
  public QName getTypeName()
  {
    return this.typeAttribute;
  }

  /**
   * Private helper method to validate the values of two QName
   * objects. The arguments are mutually exclusive, so only one
   * of them may actually have a QName value. The other one must
   * be 'null'. The method will check this condition and returns
   * a FAttributeType value that tells which argument is set.
   *
   * @param elementName QName value of 'element' attribute
   * @param typeName QName value of 'type' attribute
   *
   * @return Attribute that is set for message part
   *
   * @throws IllegalStateException Both attributes were set at
   * the same time
   */
  private FAttributeType getAttributeType(final QName elementName, final QName typeName) throws IllegalStateException
  {
    FAttributeType result = FAttributeType.NONE;

    // Both attributes must not be set at the same time
    if (null != elementName && null != typeName)
    {
      throw new IllegalStateException("Message part cannot have 'element' and 'type' attribute set at the same time.");
    }

    if (null != elementName)
    {
      result = FAttributeType.ELEMENT;
    }
    else if (null != typeName)
    {
      result = FAttributeType.TYPE;
    }

    return result;
  }

  /**
   * Public helper method to determine, whether the 'element'
   * attribute was set for the current message part.
   *
   * @return True if 'element' attribute was set, false
   * otherwise
   */
  @Override
  public boolean hasElementAttribute()
  {
    return (FAttributeType.ELEMENT == this.getAttributeType(this.elementAttribute, this.typeAttribute));
  }

  /**
   * Public helper method to determine, wheter the 'type'
   * attribute was set for the current message part.
   *
   * @return True if 'type' attribite was set, false
   * otherwise
   */
  @Override
  public boolean hasTypeAttribute()
  {
    return (FAttributeType.TYPE == this.getAttributeType(this.elementAttribute, this.typeAttribute));
  }
  
  /**
   * Create a human-readable form of the message part, that is
   * its name and the value of the 'element' or 'type' attribute.
   * 
   * @return String representation of FMessagePartImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    // Determine type of message part
    String type = "";
    if (this.hasElementAttribute())
    {
      type = String.format("element = '%s'", this.elementAttribute.getLocalPart());
    }
    else
    {
      type = String.format("type = '%s'", this.typeAttribute.getLocalPart());
    }

    result = String.format("Part: '%s' (%s)", this.partName, type);

    return result;
  }
}
