/** 01.08.2012 02:16 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * This class is a wrapper for Java's stock ExtensibilityElement
 * class, which is used by the WSDL4J library to provide binding
 * details in WSDL documents.
 *
 * Extensibility elements are utilized to specify the concrete
 * grammar for the input, output and fault messages of a binding
 * operation. Furthermore, per-operation binding information and
 * per-binding information may be specified in WSDL.
 *
 * @author seidel
 */
public class FExtensibilityElement extends FWSDLElement
{
  /** Wrapped ExtensibilityElement object */
  private ExtensibilityElement extensibilityElement;

  /**
   * Parameterized constructor creates a new object,
   * wrapping the provided ExtensibilityElement
   * object.
   *
   * @param extensibilityElement Object to be wrapped
   */
  public FExtensibilityElement(final ExtensibilityElement extensibilityElement)
  {
    this.extensibilityElement = extensibilityElement;
  }

  /**
   * Set ExtensibilityElement object to be wrapped.
   *
   * @param extensibilityElement Object to be wrapped
   */
  public void setExtensibilityElement(final ExtensibilityElement extensibilityElement)
  {
    this.extensibilityElement = extensibilityElement;
  }

  /**
   * Get wrapped ExtensibilityElement object.
   *
   * @return Wrapped ExtensibilityElement object
   */
  public ExtensibilityElement getExtensibilityElement()
  {
    return this.extensibilityElement;
  }

  /**
   * Set type of wrapped ExtensibilityElement object.
   *
   * @param elementType QName value for element type
   */
  public void setElementType(final QName elementType)
  {
    this.extensibilityElement.setElementType(elementType);
  }

  /**
   * Get type of wrapped ExtensibilityElement object.
   *
   * @return Element type of wrapped object
   */
  public QName getElementType()
  {
    return this.extensibilityElement.getElementType();
  }

  /**
   * Set 'required' flag of wrapped ExtensibilityElement object.
   *
   * @param isRequired Boolean value for 'required' flag
   */
  public void setRequired(final boolean isRequired)
  {
    this.extensibilityElement.setRequired(isRequired);
  }

  /**
   * Get 'required' flag of wrapped ExtensibilityElement object.
   *
   * @return Boolean value of 'required' flag
   */
  public boolean getRequired()
  {
    return this.extensibilityElement.getRequired();
  }

  /**
   * This method is an alias for getRequired() and is
   * solely provided for convenience.
   *
   * @return Boolean value of 'required' flag
   */
  public boolean isRequired()
  {
    return this.getRequired();
  }

  /**
   * Get the class of the wrapped ExtensibilityElement object
   * and return its simple name. This method will simply call
   * 'getClass().getSimpleName()' on the object.
   *
   * @return Simple class name of wrapped object
   */
  public String getImplementationName()
  {
    return this.extensibilityElement.getClass().getSimpleName();
  }

  /**
   * Create a human-readable form of the wrapped ExtensibilityElement
   * object. For the sake of simplicity, we only print the name of
   * the element's implementation class here.
   *
   * @return String representation of FExtensibilityElement object
   */
  @Override
  public String toString()
  {
    return this.getImplementationName();
  }

  /**
   * Compare extensibility element object with another object
   * of the same type, based on the attributes of the current
   * class.
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
      FExtensibilityElement otherExtensibilityElement = (FExtensibilityElement)object;

      // Attribute values are equal
      if (this.extensibilityElement.equals(otherExtensibilityElement.getExtensibilityElement()))
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

    hash = 13 * hash + (this.extensibilityElement != null ? this.extensibilityElement.hashCode() : 0);

    return hash;
  }
}
