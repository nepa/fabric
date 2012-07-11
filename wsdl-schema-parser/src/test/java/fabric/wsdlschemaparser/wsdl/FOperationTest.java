/** 10.07.2012 12:57 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 * Unit test for FOperation and FOperationFactory class.
 *
 * @author seidel
 */
public class FOperationTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FOperation operation = null;
    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);
    FOperationFaultMessage fault = new FOperationFaultMessage("fault", qName);
    HashSet<FOperationFaultMessage> faults = new HashSet<FOperationFaultMessage>();
    faults.add(fault);

    // Test first constructor
    operation = new FOperationImpl("foo", FOperationType.ONE_WAY, input, output);

    assertNotNull("FOperation object must not be null.", operation);
    assertEquals("Operation name must match initial value.", "foo", operation.getOperationName());

    // Test second constructor
    operation = new FOperationImpl("bar", FOperationType.ONE_WAY, input, output, faults);

    assertNotNull("FOperation object must not be null.", operation);
    assertEquals("Operation name must match initial value.", "bar", operation.getOperationName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    FOperation operation = null;
    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);
    FOperationFaultMessage fault = new FOperationFaultMessage("fault", qName);
    HashSet<FOperationFaultMessage> faults = new HashSet<FOperationFaultMessage>();
    faults.add(fault);

    // Test first create()
    operation = FOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    assertNotNull("FOperation object must not be null.", operation);
    assertEquals("Operation name must match initial value.", "foo", operation.getOperationName());

    // Test second create()
    operation = FOperation.factory.create("bar", FOperationType.ONE_WAY, input, output, faults);

    assertNotNull("FOperation object must not be null.", operation);
    assertEquals("Operation name must match initial value.", "bar", operation.getOperationName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);
    FOperation operation = FOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    // Test operation name
    operation.setOperationName("bar");
    assertEquals("Operation name must match new value.", "bar", operation.getOperationName());

    // Test operation type
    operation.setOperationType(FOperationType.NOTIFICATION);
    assertEquals("Operation type must match new value.", FOperationType.NOTIFICATION, operation.getOperationType());

    // Test operation input
    operation.setInputMessage(new FOperationInputMessage("newInput", qName));
    assertEquals("Operation input message name must match new value.", "newInput", operation.getInputMessage().getOperationMessageName());

    // Test operation output
    operation.setOutputMessage(new FOperationOutputMessage("newOutput", qName));
    assertEquals("Operation output message name must match new value.", "newOutput", operation.getOutputMessage().getOperationMessageName());
  }

  /**
   * Test methods for handling of fault messages.
   */
  @Test(timeout = 1000)
  public void testFaultHandling()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FOperationInputMessage input = new FOperationInputMessage("input", qName);
    FOperationOutputMessage output = new FOperationOutputMessage("output", qName);
    FOperation operation = FOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    FOperationFaultMessage firstFault = new FOperationFaultMessage("firstFault", qName);
    FOperationFaultMessage secondFault = new FOperationFaultMessage("secondFault", qName);

    // Test addFaultMessage()
    assertEquals("Fault message count must be zero.", 0, operation.getFaultMessages().size());
    operation.addFaultMessage(firstFault);
    assertEquals("Fault message count must be one.", 1, operation.getFaultMessages().size());
    operation.addFaultMessage(secondFault);
    assertEquals("Fault message count must be two.", 2, operation.getFaultMessages().size());

    // Reset operation object
    operation = FOperation.factory.create("bar", FOperationType.ONE_WAY, input, output);

    // Test addFaultMessages()
    HashSet<FOperationFaultMessage> faults = new HashSet<FOperationFaultMessage>();
    faults.add(firstFault);
    faults.add(secondFault);
    assertEquals("Fault message count must be zero.", 0, operation.getFaultMessages().size());
    operation.addFaultMessages(faults);
    assertEquals("Fault message count must be two.", 2, operation.getFaultMessages().size());

    // Reset operation object
    operation = FOperation.factory.create("foobar", FOperationType.ONE_WAY, input, output);

    // Test setFaultMessages()
    assertEquals("Fault message count must be zero.", 0, operation.getFaultMessages().size());
    operation.setFaultMessages(faults);
    assertEquals("Fault message count must be two.", 2, operation.getFaultMessages().size());

    // Test getFaultMessages()
    Iterator iterator = operation.getFaultMessages().iterator();
    while (iterator.hasNext())
    {
      FOperationFaultMessage fault = (FOperationFaultMessage)iterator.next();

      assertTrue("Operation must contain fault message that was added previously.", faults.contains(fault));
    }

    // Test getFaultMessage()
    assertNotNull("Operation must contain a fault message named 'firstFault'.", operation.getFaultMessage("firstFault"));
    assertNull("Operation must not contait a fault message named 'thirdFault'.", operation.getFaultMessage("thirdFault"));
  }
}
