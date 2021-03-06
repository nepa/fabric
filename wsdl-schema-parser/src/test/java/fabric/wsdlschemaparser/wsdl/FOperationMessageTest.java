/** 19.07.2012 11:56 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.namespace.QName;

/**
 * Unit test for FOperationMessage classes.
 *
 * @author seidel
 */
public class FOperationMessageTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FOperationMessage message = null;

    // Test input message
    message = new FOperationInputMessage("input", qName);

    assertNotNull("FOperationInputMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "input", message.getOperationMessageName());

    // Test output message
    message = new FOperationOutputMessage("output", qName);

    assertNotNull("FOperationOutputMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "output", message.getOperationMessageName());

    // Test fault message
    message = new FOperationFaultMessage("fault", qName);

    assertNotNull("FOperationFaultMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "fault", message.getOperationMessageName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    QName initialQName = new QName("namespaceURI", "localPart");
    QName newQName = new QName("namespaceURI", "newLocalPart");
    FOperationMessage message = new FOperationInputMessage("foo", initialQName);

    message.setOperationMessageName("bar");
    assertEquals("Message name must match new value.", "bar", message.getOperationMessageName());

    message.setMessageAttribute(newQName);
    assertEquals("Message attribute must match new value.", "newLocalPart",
            message.getMessageAttribute().getLocalPart());
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    QName qName = new QName("namespaceURI", "localPart");
    FOperationMessage firstInput = new FOperationInputMessage("foo", qName);
    FOperationMessage secondInput = new FOperationInputMessage("bar", qName);
    FOperationMessage output = new FOperationOutputMessage("foo", qName);

    // Unequality
    assertFalse("Message objects with different type must not be equal.", firstInput.equals(output));

    assertFalse("Message objects with different names must not be equal.", firstInput.equals(secondInput));

    // Equality
    firstInput.setOperationMessageName("foobar");
    secondInput.setOperationMessageName("foobar");
    assertTrue("Message objects must be equal.", firstInput.equals(secondInput));
  }
}
