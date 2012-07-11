/** 10.07.2012 02:12 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.namespace.QName;

/**
 * Unit test for FMessagePart and FMessagePartFactory class.
 *
 * @author seidel
 */
public class FMessagePartTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    QName qName = new QName("namespaceURI", "localPart");

    // Test first constructor
    FMessagePart part = new FMessagePartImpl("foo", qName, true);
    assertNotNull("FMessagePart object must not be null.", part);
    assertEquals("Part name must match initial value.", "foo", part.getPartName());

    // Test second constructor
    part = new FMessagePartImpl("bar", qName, null);
    assertNotNull("FMessagePart object must not be null.", part);
    assertEquals("Part name must match initial value.", "bar", part.getPartName());

    // Test for expected exception (elementName and typeName are mutually exclusive)
    Exception exception = null;
    try
    {
      part = new FMessagePartImpl("foobar", qName, qName);
    }
    catch (Exception e)
    {
      exception = e;
      System.out.println(String.format("Expected exception was thrown: '%s'", e.getMessage()));
    }
    assertTrue("FMessagePart must throw 'IllegalStateException'.", exception instanceof IllegalStateException);
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    QName qName = new QName("namespaceURI", "localPart");

    // Test first create()
    FMessagePart part = FMessagePart.factory.create("foo", qName, true);
    assertNotNull("FMessagePart object must not be null.", part);
    assertEquals("Part name must match initial value.", "foo", part.getPartName());

    // Test second create()
    part = FMessagePart.factory.create("bar", qName, null);
    assertNotNull("FMessagePart object must not be null.", part);
    assertEquals("Part name must match initial value.", "bar", part.getPartName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    QName initialQName = new QName("namespaceURI", "localPart");
    QName newQName = new QName("namespaceURI", "newLocalPart");
    FMessagePart part = FMessagePart.factory.create("foo", initialQName, null);

    part.setPartName("bar");
    assertEquals("Part name must match new value.", "bar", part.getPartName());

    part.setElementName(newQName);
    assertEquals("QName value of 'element' attribute must match new value.",
            "newLocalPart", part.getElementName().getLocalPart());

    part.setTypeName(newQName);
    assertEquals("QName value of 'type' attribute must match new value.",
            "newLocalPart", part.getTypeName().getLocalPart());
  }

  /**
   * Test methods to check 'element'/'type' attribute.
   */
  @Test(timeout = 1000)
  public void testFlags()
  {
    QName qName = new QName("namespaceURI", "localPart");

    FMessagePart part = FMessagePart.factory.create("foo", qName, true);
    assertTrue("Part must only have 'element' attribute set.", part.hasElementAttribute() && !part.hasTypeAttribute());

    part = new FMessagePartImpl("bar", qName, false);
    assertTrue("Part must only have 'type' attribute set.", !part.hasElementAttribute() && part.hasTypeAttribute());
  }
}
