/** 17.07.2012 15:05 */
package fabric.wsdlschemaparser.wsdl;

import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a single service definition in a WSDL
 * document. A service is a group of ports (endpoints), that
 * on their part each define a concrete service address.
 *
 * @author seidel
 */
public class FServiceImpl extends FWSDLElement implements FService
{
  /** Name of the service */
  private String serviceName;

  /** Set of service ports (endpoints) */
  private Set<FPort> ports;

  /**
   * Parameterized constructor creates a new service
   * with the given name.
   *
   * @param serviceName Name of the service
   */
  public FServiceImpl(final String serviceName)
  {
    this.serviceName = serviceName;
    this.ports = new HashSet<FPort>();
  }

  /**
   * Set name of the service.
   *
   * @param serviceName Name of the service
   */
  @Override
  public void setServiceName(final String serviceName)
  {
    this.serviceName = serviceName;
  }

  /**
   * Get name of the service.
   *
   * @return Name of the service
   */
  @Override
  public String getServiceName()
  {
    return this.serviceName;
  }

  /**
   * Add a single port (endpoint) to the service.
   *
   * @param port Port to add
   */
  @Override
  public void addPort(final FPort port)
  {
    this.ports.add(port);
  }

  /**
   * Add a set of ports (endpoints) to the service.
   *
   * @param ports Set of ports to add
   */
  @Override
  public void addPorts(final HashSet<FPort> ports)
  {
    this.ports.addAll(ports);
  }

  /**
   * Set ports (endpoints) of the service.
   *
   * @param ports Set of ports
   */
  @Override
  public void setPorts(final HashSet<FPort> ports)
  {
    this.ports = ports;
  }

  /**
   * Get ports (endpoints) of the service.
   *
   * @return Ports of the service
   */
  @Override
  public HashSet<FPort> getPorts()
  {
    return (HashSet<FPort>)this.ports;
  }

  /**
   * Return the number of ports that are defined
   * within the current service object.
   *
   * @return Number of ports
   */
  @Override
  public int portCount()
  {
    return this.ports.size();
  }

  /**
   * Print service in a human-readable form. That is the
   * name of the service, as well as a list of all ports
   * (endpoints) that are defined within the service.
   *
   * @return String representation of FServiceImpl object
   */
  @Override
  public String toString()
  {
    String result = "";

    result += String.format("Service: '%s'", this.serviceName);

    for (FPort endpoint: this.ports)
    {
      result += "\n\t" + endpoint.toString();
    }

    return result;
  }

  /**
   * Compare service object with another object of the same
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
      FServiceImpl otherService = (FServiceImpl)object;

      // Attribute values are equal
      if (this.serviceName.equals(otherService.getServiceName()) &&
          this.ports.equals(otherService.getPorts()))
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
    int hash = 3;

    hash = 31 * hash + (this.serviceName != null ? this.serviceName.hashCode() : 0);
    hash = 31 * hash + (this.ports != null ? this.ports.hashCode() : 0);

    return hash;
  }
}
