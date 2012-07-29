/** 29.07.2012 02:20 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * Abstract base class for all binding operation messages,
 * that is input, output and fault messages as described
 * in the WSDL 1.1 specification.
 *
 * @author seidel
 */
public abstract class FBindingOperationMessage extends FWSDLElement
{
  /** Name of the binding operation message */
  protected String bindingOperationMessageName;

  /** Extensibility elements with per-message information */
  protected Set<FExtensibilityElement> perMessageInformations;

  /**
   * Set name of the binding operation message.
   *
   * @param bindingOperationMessageName Binding operation message name
   */
  public void setBindingOperationMessageName(final String bindingOperationMessageName)
  {
    this.bindingOperationMessageName = bindingOperationMessageName;
  }

  /**
   * Get name of the binding operation message.
   *
   * @return Binding operation message name
   */
  public String getBindingOperationMessageName()
  {
    return this.bindingOperationMessageName;
  }

  /**
   * Add extensibility element with per-message information.
   *
   * @param perMessageInformation Extensibility element with
   * per-message information
   */
  public void addPerMessageInformation(final FExtensibilityElement perMessageInformation)
  {
    this.perMessageInformations.add(perMessageInformation);
  }

  /**
   * Add a set of extensibility elements with per-message
   * information.
   *
   * @param perMessageInformations Set of extensibility
   * elements with per-message information
   */
  public void addPerMessageInformations(final HashSet<FExtensibilityElement> perMessageInformations)
  {
    this.perMessageInformations.addAll(perMessageInformations);
  }

  /**
   * Set extensibility elements with per-message information.
   *
   * @param perMessageInformations Set of extensibility
   * elements with per-message information
   */
  public void setPerMessageInformations(final HashSet<FExtensibilityElement> perMessageInformations)
  {
    this.perMessageInformations = perMessageInformations;
  }

  /**
   * Get extensibility elements with per-message information.
   *
   * @return Set of extensibility elements with per-message
   * information
   */
  public HashSet<FExtensibilityElement> getPerMessageInformations()
  {
    return (HashSet<FExtensibilityElement>)this.perMessageInformations;
  }

  /**
   * Return the number of extensibility elements with
   * per-message information that are defined within
   * the current binding operation message object.
   *
   * @return Number of extensibility elements with
   * per-message information
   */
  public int perMessageInformationCount()
  {
    return this.perMessageInformations.size();
  }

  /**
   * Create a human-readable form of the binding operation
   * message. Here we only print the name of the message.
   *
   * @return String representation of FBindingOperationMessage
   * object
   */
  @Override
  public String toString()
  {
    return this.bindingOperationMessageName;
  }

  /**
   * Compare binding operation message object with another
   * object of the same type, based on the attributes of
   * the current class.
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
      FBindingOperationMessage otherBindingOperationMessage = (FBindingOperationMessage)object;

      // Attribute values are equal
      if (this.bindingOperationMessageName.equals(otherBindingOperationMessage.getBindingOperationMessageName()) &&
          this.perMessageInformations.equals(otherBindingOperationMessage.getPerMessageInformations()))
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

    hash = 19 * hash + (this.bindingOperationMessageName != null ? this.bindingOperationMessageName.hashCode() : 0);
    hash = 19 * hash + (this.perMessageInformations != null ? this.perMessageInformations.hashCode() : 0);

    return hash;
  }
}
