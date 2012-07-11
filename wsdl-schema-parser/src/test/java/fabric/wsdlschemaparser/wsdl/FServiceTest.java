/** 11.07.2012 02:00 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * Unit test for FService and FServiceFactory class.
 *
 * @author seidel
 */
public class FServiceTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FService service = new FServiceImpl("foobar");

    assertNotNull("FService object must not be null.", service);
    assertEquals("Service name must match initial value.", "foobar", service.getServiceName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    FService service = FService.factory.create("foobar");

    assertNotNull("FService object must not be null.", service);
    assertEquals("Service name must match initial value.", "foobar", service.getServiceName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FService service = FService.factory.create("foo");

    service.setServiceName("bar");
    assertEquals("Service name must match new value.", "bar", service.getServiceName());
  }

  /**
   * Test methods for port handling.
   */
  @Test(timeout = 1000)
  public void testPortHandling()
  {
    FService service = FService.factory.create("foo");

    QName qName = new QName("namespaceURI", "localPart");
    FExtensibilityElement addressInformation = new FExtensibilityElement(new UnknownExtensibilityElement());

    FPort firstPort = FPort.factory.create("firstPort", qName, addressInformation);
    FPort secondPort = FPort.factory.create("secondPort", qName, addressInformation);

    // Test addPort() and portCount()
    assertEquals("Port count must be zero.", 0, service.portCount());
    service.addPort(firstPort);
    assertEquals("Port count must be one.", 1, service.portCount());
    service.addPort(secondPort);
    assertEquals("Port count must be two.", 2, service.portCount());

    // Reset service object
    service = FService.factory.create("bar");

    // Test addPorts()
    HashSet<FPort> ports = new HashSet<FPort>();
    ports.add(firstPort);
    ports.add(secondPort);
    assertEquals("Port count must be zero.", 0, service.portCount());
    service.addPorts(ports);
    assertEquals("Port count must be two.", 2, service.portCount());

    // Reset service object
    service = FService.factory.create("foobar");

    // Test setPorts()
    assertEquals("Port count must be zero.", 0, service.portCount());
    service.setPorts(ports);
    assertEquals("Port count must be two.", 2, service.portCount());

    // Test getPorts()
    Iterator iterator = service.getPorts().iterator();
    while (iterator.hasNext())
    {
      FPort port = (FPort)iterator.next();

      assertTrue("Service must contain port that was added previously.", ports.contains(port));
    }
  }
}
