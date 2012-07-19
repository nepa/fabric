/** 17.07.2012 15:00 */
package fabric.wsdlschemaparser.wsdl;

import javax.xml.namespace.QName;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;

/**
 * This class represents a single port definition in a
 * WSDL document (WSDL 2.0: endpoint element). A port
 * specifies a concrete address for a referenced binding.
 *
 * A port must not specify more than one address.
 * 
 * @author seidel
 */
public class FPortImpl extends FWSDLElement implements FPort
{
  /** Name of the port */
  private String portName;

  /** Reference to corresponding binding */
  private QName bindingReference;

  /** Extensibility element with address information */
  private FExtensibilityElement addressInformation;

  /**
   * Parameterized constructor creates a new port (endpoint)
   * with the given name, binding reference and address
   * information.
   *
   * @param portName Name of the port
   * @param bindingReference QName of referenced binding
   * @param addressInformation Extensibility element with
   * address information
   */
  public FPortImpl(final String portName, final QName bindingReference, final FExtensibilityElement addressInformation)
  {
    this.portName = portName;
    this.bindingReference = bindingReference;
    this.addressInformation = addressInformation;
  }

  /**
   * Set name of the port (endpoint).
   *
   * @param portName Name of the port
   */
  @Override
  public void setPortName(final String portName)
  {
    this.portName = portName;
  }

  /**
   * Get name of the port (endpoint).
   *
   * @return Name of the port
   */
  @Override
  public String getPortName()
  {
    return this.portName;
  }

  /**
   * Set QName of the referenced binding.
   *
   * @param bindingReference QName of referenced binding
   */
  @Override
  public void setBindingReference(final QName bindingReference)
  {
    this.bindingReference = bindingReference;
  }

  /**
   * Get QName of the referenced binding.
   *
   * @return QName of referenced binding
   */
  @Override
  public QName getBindingReference()
  {
    return this.bindingReference;
  }

  /**
   * Set extensibility element with address information.
   *
   * @param addressInformation Extensibility element
   * with address information
   */
  @Override
  public void setAddressInformation(final FExtensibilityElement addressInformation)
  {
    this.addressInformation = addressInformation;
  }

  /**
   * Get extensibility element with address information.
   *
   * @return Extensibility element with address information
   */
  @Override
  public FExtensibilityElement getAddressInformation()
  {
    return this.addressInformation;
  }

  /**
   * Print port (endpoint) in a human-readable form. That
   * is the name of the port, as well as its type (binding
   * reference) and endpoint address.
   *
   * @return String representation of FPortImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    // Try to get URL of service endpoint
    String endpointAddress = "";
    ExtensibilityElement element = this.addressInformation.getExtensibilityElement();
    if (element instanceof HTTPAddress)
    {
      HTTPAddress address = (HTTPAddress)element;
      endpointAddress += String.format("HTTP address '%s'", address.getLocationURI());
    }
    else if (element instanceof SOAPAddress)
    {
      SOAPAddress address = (SOAPAddress)element;
      endpointAddress += String.format("SOAP address '%s'", address.getLocationURI());
    }
    else
    {
      endpointAddress += "unknown address";
    }

    result += String.format("Endpoint (port): '%s' [Binding type '%s'] @ %s", this.portName,
            (null != this.bindingReference ? this.bindingReference.getLocalPart() : "undefined"),
            endpointAddress);

    return result;
  }

  /**
   * Compare port object with another object of the same
   * type, based on the attributes of the current class.
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
      FPortImpl otherPort = (FPortImpl)object;

      // Attribute values are equal
      if (this.portName.equals(otherPort.getPortName()) &&
          this.bindingReference.equals(otherPort.getBindingReference()) &&
          this.addressInformation.equals(otherPort.getAddressInformation()))
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

    hash = 37 * hash + (this.portName != null ? this.portName.hashCode() : 0);
    hash = 37 * hash + (this.bindingReference != null ? this.bindingReference.hashCode() : 0);
    hash = 37 * hash + (this.addressInformation != null ? this.addressInformation.hashCode() : 0);

    return hash;
  }
}
