/** 19.07.2012 11:46 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * Unit test for FBindingOperation and FBindingOperationFactory class.
 *
 * @author seidel
 */
public class FBindingOperationTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FBindingOperation bindingOperation = null;
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperationFaultMessage fault = new FBindingOperationFaultMessage("fault");
    HashSet<FBindingOperationFaultMessage> faults = new HashSet<FBindingOperationFaultMessage>();
    faults.add(fault);

    // Test first constructor
    bindingOperation = new FBindingOperationImpl("foo", FOperationType.ONE_WAY, input, output);

    assertNotNull("FBindingOperation object must not be null.", bindingOperation);
    assertEquals("Binding operation name must match initial value.", "foo", bindingOperation.getBindingOperationName());

    // Test second constructor
    bindingOperation = new FBindingOperationImpl("bar", FOperationType.ONE_WAY, input, output, faults);

    assertNotNull("FBindingOperation object must not be null.", bindingOperation);
    assertEquals("Binding operation name must match initial value.", "bar", bindingOperation.getBindingOperationName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    FBindingOperation bindingOperation = null;
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperationFaultMessage fault = new FBindingOperationFaultMessage("fault");
    HashSet<FBindingOperationFaultMessage> faults = new HashSet<FBindingOperationFaultMessage>();
    faults.add(fault);

    // Test first create()
    bindingOperation = FBindingOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    assertNotNull("FBindingOperation object must not be null.", bindingOperation);
    assertEquals("Binding operation name must match initial value.", "foo", bindingOperation.getBindingOperationName());

    // Test second create
    bindingOperation = FBindingOperation.factory.create("bar", FOperationType.ONE_WAY, input, output, faults);

    assertNotNull("FBindingOperation object must not be null.", bindingOperation);
    assertEquals("Binding operation name must match initial value.", "bar", bindingOperation.getBindingOperationName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperation bindingOperation = FBindingOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    // Test operation name
    bindingOperation.setBindingOperationName("bar");
    assertEquals("Binding operation name must match new value.", "bar", bindingOperation.getBindingOperationName());

    // Test operation input
    bindingOperation.setInputMessage(new FBindingOperationInputMessage("newInput"));
    assertEquals("Binding operation input message name must match new value.", "newInput",
            bindingOperation.getInputMessage().getBindingOperationMessageName());

    // Test operation output
    bindingOperation.setOutputMessage(new FBindingOperationOutputMessage("newOutput"));
    assertEquals("Binding operation output message name must match new value.", "newOutput",
            bindingOperation.getOutputMessage().getBindingOperationMessageName());
  }

  /**
   * Test methods for handling of per-operation information.
   */
  @Test(timeout = 1000)
  public void testPerMessageInformationHandling()
  {
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperation bindingOperation = FBindingOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    FExtensibilityElement firstElement = new FExtensibilityElement(new UnknownExtensibilityElement());
    FExtensibilityElement secondElement = new FExtensibilityElement(new UnknownExtensibilityElement());

    // Test addPerOperationInformation()
    assertEquals("Per-operation information count must be zero.", 0, bindingOperation.perOperationInformationCount());
    bindingOperation.addPerOperationInformation(firstElement);
    assertEquals("Per-operation information count must be one.", 1, bindingOperation.perOperationInformationCount());
    bindingOperation.addPerOperationInformation(secondElement);
    assertEquals("Per-operation information count must be two.", 2, bindingOperation.perOperationInformationCount());

    // Reset binding operation object
    bindingOperation = FBindingOperation.factory.create("bar", FOperationType.ONE_WAY, input, output);

    // Test addPerOperationInformations()
    HashSet<FExtensibilityElement> elements = new HashSet<FExtensibilityElement>();
    elements.add(firstElement);
    elements.add(secondElement);
    assertEquals("Per-operation information count must be zero.", 0, bindingOperation.perOperationInformationCount());
    bindingOperation.addPerOperationInformations(elements);
    assertEquals("Per-operation information count must be two.", 2, bindingOperation.perOperationInformationCount());

    // Reset binding operation object
    bindingOperation = FBindingOperation.factory.create("foobar", FOperationType.ONE_WAY, input, output);

    // Test setPerOperationInformations()
    assertEquals("Per-operation information count must be zero.", 0, bindingOperation.perOperationInformationCount());
    bindingOperation.setPerOperationInformations(elements);
    assertEquals("Per-operation information count must be two.", 2, bindingOperation.perOperationInformationCount());

    // Test getPerOperationInformations()
    Iterator iterator = bindingOperation.getPerOperationInformations().iterator();
    while (iterator.hasNext())
    {
      FExtensibilityElement element = (FExtensibilityElement)iterator.next();

      assertTrue("Binding operation must contain per-operation information that was added previously.", elements.contains(element));
    }
  }

  /**
   * Test methods for handling of fault messages.
   */
  @Test(timeout = 1000)
  public void testFaultHandling()
  {
    FBindingOperationInputMessage input = new FBindingOperationInputMessage("input");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperation bindingOperation = FBindingOperation.factory.create("foo", FOperationType.ONE_WAY, input, output);

    FBindingOperationFaultMessage firstFault = new FBindingOperationFaultMessage("firstFault");
    FBindingOperationFaultMessage secondFault = new FBindingOperationFaultMessage("secondFault");

    // Test addFaultMessage()
    assertEquals("Fault message count must be zero.", 0, bindingOperation.faultMessageCount());
    bindingOperation.addFaultMessage(firstFault);
    assertEquals("Fault message count must be one.", 1, bindingOperation.faultMessageCount());
    bindingOperation.addFaultMessage(secondFault);
    assertEquals("Fault message count must be two.", 2, bindingOperation.faultMessageCount());

    // Reset binding operation object
    bindingOperation = FBindingOperation.factory.create("bar", FOperationType.ONE_WAY, input, output);

    // Test addFaultMessages()
    HashSet<FBindingOperationFaultMessage> faults = new HashSet<FBindingOperationFaultMessage>();
    faults.add(firstFault);
    faults.add(secondFault);
    assertEquals("Fault message count must be zero.", 0, bindingOperation.faultMessageCount());
    bindingOperation.addFaultMessages(faults);
    assertEquals("Fault message count must be two.", 2, bindingOperation.faultMessageCount());

    // Reset binding operation object
    bindingOperation = FBindingOperation.factory.create("foobar", FOperationType.ONE_WAY, input, output);

    // Test setFaultMessages()
    assertEquals("Fault message count must be zero.", 0, bindingOperation.faultMessageCount());
    bindingOperation.setFaultMessages(faults);
    assertEquals("Fault message count must be two.", 2, bindingOperation.faultMessageCount());

    // Test getFaultMessages()
    Iterator iterator = bindingOperation.getFaultMessages().iterator();
    while (iterator.hasNext())
    {
      FBindingOperationFaultMessage fault = (FBindingOperationFaultMessage)iterator.next();

      assertTrue("Binding operation must contain fault message that was added previously.", faults.contains(fault));
    }

    // Test getFaultMessage()
    assertNotNull("Binding operation must contain a fault message named 'firstFault'.",
            bindingOperation.getFaultMessage("firstFault"));
    assertNull("Binding operation must not contait a fault message named 'thirdFault'.",
            bindingOperation.getFaultMessage("thirdFault"));
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    FBindingOperationInputMessage firstInput = new FBindingOperationInputMessage("firstInput");
    FBindingOperationInputMessage secondInput = new FBindingOperationInputMessage("secondInput");
    FBindingOperationOutputMessage output = new FBindingOperationOutputMessage("output");
    FBindingOperation firstOperation = FBindingOperation.factory.create("foo", FOperationType.REQUEST_RESPONSE, firstInput, output);
    FBindingOperation secondOperation = FBindingOperation.factory.create("bar", FOperationType.REQUEST_RESPONSE, secondInput, output);

    // Notice: Operation type is solely used for implicit message naming in
    // FBindingOperation, so we cannot reset it after calling the constructor.

    // Unequality
    assertFalse("Operation objects with different names and messages must not be equal.", firstOperation.equals(secondOperation));

    secondOperation.setInputMessage(firstInput);
    assertFalse("Operation objects with different names must not be equal.", firstOperation.equals(secondOperation));

    // Equality
    secondOperation.setBindingOperationName("foo");
    assertTrue("Operation objects must be equal.", firstOperation.equals(secondOperation));
  }
}
