/** 11.07.2012 01:29 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.namespace.QName;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import com.ibm.wsdl.extensions.http.HTTPAddressImpl;

/**
 * Unit test for FPort and FPortFactory class.
 *
 * @author seidel
 */
public class FPortTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FExtensibilityElement addressInformation = new FExtensibilityElement(new UnknownExtensibilityElement());
    FPort port = new FPortImpl("foobar", qName, addressInformation);

    assertNotNull("FPort object must not be null.", port);
    assertEquals("Port name must match initial value.", "foobar", port.getPortName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FExtensibilityElement addressInformation = new FExtensibilityElement(new UnknownExtensibilityElement());
    FPort port = FPort.factory.create("foobar", qName, addressInformation);

    assertNotNull("FPort object must not be null.", port);
    assertEquals("Port name must match initial value.", "foobar", port.getPortName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    QName initialQName = new QName("namespaceURI", "localPart");
    QName newQName = new QName("namespaceURI", "newLocalPart");
    FExtensibilityElement initialAddressInformation = new FExtensibilityElement(new UnknownExtensibilityElement());
    FExtensibilityElement newAddressInformation = new FExtensibilityElement(new HTTPAddressImpl());
    FPort port = FPort.factory.create("foo", initialQName, initialAddressInformation);

    // Test port name
    port.setPortName("bar");
    assertEquals("Port name must match new value.", "bar", port.getPortName());

    // Test binding reference
    port.setBindingReference(newQName);
    assertEquals("Binding reference must match new value.", "newLocalPart", port.getBindingReference().getLocalPart());

    // Test address information
    port.setAddressInformation(newAddressInformation);
    assertEquals("Address information must match new value.", "HTTPAddressImpl",
            port.getAddressInformation().getImplementationName());
  }
}
