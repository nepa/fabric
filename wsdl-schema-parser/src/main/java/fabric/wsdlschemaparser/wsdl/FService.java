/** 05.07.2012 18:54 */
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
public class FService extends FWSDLElement
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
  public FService(final String serviceName)
  {
    this.serviceName = serviceName;
    this.ports = new HashSet<FPort>();
  }

  /**
   * Set name of the service.
   *
   * @param serviceName Name of the service
   */
  public void setServiceName(final String serviceName)
  {
    this.serviceName = serviceName;
  }

  /**
   * Get name of the service.
   *
   * @return Name of the service
   */
  public String getServiceName()
  {
    return this.serviceName;
  }

  /**
   * Add a single port (endpoint) to the service.
   *
   * @param port Port to add
   */
  public void addPort(final FPort port)
  {
    this.ports.add(port);
  }

  /**
   * Add a set of ports (endpoints) to the service.
   *
   * @param ports Set of ports to add
   */
  public void addPorts(final HashSet<FPort> ports)
  {
    this.ports.addAll(ports);
  }

  /**
   * Set ports (endpoints) of the service.
   *
   * @param ports Set of ports
   */
  public void setPorts(final HashSet<FPort> ports)
  {
    this.ports = ports;
  }

  /**
   * Get ports (endpoints) of the service.
   *
   * @return Ports of the service
   */
  public HashSet<FPort> getPorts()
  {
    return (HashSet<FPort>)this.ports;
  }

  /**
   * Print service in a human-readable form. That is the
   * name of the service, as well as a list of all ports
   * (endpoints) that are defined within the service.
   *
   * @return String representation of FService object
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
}
