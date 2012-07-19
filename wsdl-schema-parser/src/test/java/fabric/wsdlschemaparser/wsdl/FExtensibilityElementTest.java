/** 19.07.2012 11:37 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.namespace.QName;
import com.ibm.wsdl.extensions.http.HTTPAddressImpl;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;

/**
 * Unit test for FExtensibilityElement class.
 *
 * @author seidel
 */
public class FExtensibilityElementTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FExtensibilityElement element = new FExtensibilityElement(new UnknownExtensibilityElement());

    assertNotNull("FExtensibilityElement object must not be null.", element);
    assertEquals("Extensibility element must be wrapped.", "UnknownExtensibilityElement", element.getImplementationName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FExtensibilityElement element = new FExtensibilityElement(new UnknownExtensibilityElement());
    ExtensibilityElement newWrappedElement = new HTTPAddressImpl();

    // Test wrapped element
    element.setExtensibilityElement(newWrappedElement);
    assertEquals("New extensibility element must be wrapped.", newWrappedElement, element.getExtensibilityElement());

    // Test element type
    element.setElementType(new QName("namespaceURI", "newLocalPart"));
    assertEquals("Element type must match new value.", "newLocalPart", element.getElementType().getLocalPart());

    // Test 'required' flag
    element.setRequired(true);
    assertTrue("Element must be required.", element.getRequired());
    assertTrue("Element must be required.", element.isRequired()); // Test alias as well

    element.setRequired(false);
    assertTrue("Element must not be required.", !element.getRequired());
    assertTrue("Element must not be required.", !element.isRequired()); // Test alias as well
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    ExtensibilityElement element = new UnknownExtensibilityElement();
    FExtensibilityElement firstWrapper = new FExtensibilityElement(element);
    FExtensibilityElement secondWrapper = new FExtensibilityElement(null);

    // Unequality
    assertFalse("Wrapper objects with different content must not be equal.", firstWrapper.equals(secondWrapper));

    // Equality
    secondWrapper.setExtensibilityElement(element);
    assertTrue("Wrapper objects must be equal.", firstWrapper.equals(secondWrapper));
  }
}
