/** 10.07.2012 12:56 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * Unit test for FBindingOperationMessage classes.
 *
 * @author seidel
 */
public class FBindingOperationMessageTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FBindingOperationMessage message = null;

    // Test input message
    message = new FBindingOperationInputMessage("input");

    assertNotNull("FBindingOperationInputMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "input", message.getBindingOperationMessageName());

    // Test output message
    message = new FBindingOperationOutputMessage("output");

    assertNotNull("FBindingOperationOutputMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "output", message.getBindingOperationMessageName());

    // Test fault message
    message = new FBindingOperationFaultMessage("fault");

    assertNotNull("FBindingOperationFaultMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "fault", message.getBindingOperationMessageName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FBindingOperationMessage message = new FBindingOperationInputMessage("foo");

    message.setBindingOperationMessageName("bar");
    assertEquals("Message name must match new value.", "bar", message.getBindingOperationMessageName());
  }

  /**
   * Test methods for handling of per-message information.
   */
  @Test(timeout = 1000)
  public void testPerMessageInformationHandling()
  {
    FBindingOperationMessage message = new FBindingOperationInputMessage("foo");
    FExtensibilityElement firstElement = new FExtensibilityElement(new UnknownExtensibilityElement());
    FExtensibilityElement secondElement = new FExtensibilityElement(new UnknownExtensibilityElement());

    // Test addPerMessageInformation()
    assertEquals("Per-message information count must be zero.", 0, message.getPerMessageInformations().size());
    message.addPerMessageInformation(firstElement);
    assertEquals("Per-message information count must be one.", 1, message.getPerMessageInformations().size());
    message.addPerMessageInformation(secondElement);
    assertEquals("Per-message information count must be two.", 2, message.getPerMessageInformations().size());

    // Reset message object
    message = new FBindingOperationInputMessage("bar");

    // Test addPerMessageInformations()
    HashSet<FExtensibilityElement> elements = new HashSet<FExtensibilityElement>();
    elements.add(firstElement);
    elements.add(secondElement);
    assertEquals("Per-message information count must be zero.", 0, message.getPerMessageInformations().size());
    message.addPerMessageInformations(elements);
    assertEquals("Per-message information count must be two.", 2, message.getPerMessageInformations().size());

    // Reset message object
    message = new FBindingOperationInputMessage("bar");

    // Test setPerMessageInformations()
    assertEquals("Per-message information count must be zero.", 0, message.getPerMessageInformations().size());
    message.setPerMessageInformations(elements);
    assertEquals("Per-message information count must be two.", 2, message.getPerMessageInformations().size());

    // Test getPerMessageInformations()
    Iterator iterator = message.getPerMessageInformations().iterator();
    while (iterator.hasNext())
    {
      FExtensibilityElement element = (FExtensibilityElement)iterator.next();

      assertTrue("Message must contain per-message information that was added previously.", elements.contains(element));
    }
  }
}