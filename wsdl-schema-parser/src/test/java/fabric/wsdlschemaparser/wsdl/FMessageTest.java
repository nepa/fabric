/** 19.07.2012 12:03 */
package fabric.wsdlschemaparser.wsdl;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 * Unit test for FMessage and FMessageFactory class.
 *
 * @author seidel
 */
public class FMessageTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    FMessage message = new FMessageImpl("foobar");

    assertNotNull("FMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "foobar", message.getMessageName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    FMessage message = FMessage.factory.create("foobar");

    assertNotNull("FMessage object must not be null.", message);
    assertEquals("Message name must match initial value.", "foobar", message.getMessageName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter()
  {
    FMessage message = FMessage.factory.create("foo");

    message.setMessageName("bar");
    assertEquals("Message name must match new value.", "bar", message.getMessageName());

    message.setParts(new HashSet<FMessagePart>());
    assertNotNull("Set of message parts must not be null.", message.getParts());
  }

  /**
   * Test methods for handling of message parts.
   */
  @Test(timeout = 1000)
  public void testPartHandling()
  {
    FMessage message = FMessage.factory.create("foo");

    FMessagePart firstPart = FMessagePart.factory.create("firstPart", new QName("namespaceURI", "localPart"), true);
    FMessagePart secondPart = FMessagePart.factory.create("secondPart", new QName("namespaceURI", "localPart"), true);

    // Test addPart() and partCount()
    assertEquals("Part count must be zero.", 0, message.partCount());
    message.addPart(firstPart);
    assertEquals("Part count must be one.", 1, message.partCount());
    message.addPart(secondPart);
    assertEquals("Part count must be two.", 2, message.partCount());

    // Reset message object
    message = FMessage.factory.create("bar");

    // Test addParts()
    HashSet<FMessagePart> parts = new HashSet<FMessagePart>();
    parts.add(firstPart);
    parts.add(secondPart);
    assertEquals("Part count must be zero.", 0, message.partCount());
    message.addParts(parts);
    assertEquals("Part count must be two.", 2, message.partCount());

    // Reset message object
    message = FMessage.factory.create("foobar");

    // Test setParts()
    assertEquals("Part count must be zero.", 0, message.partCount());
    message.setParts(parts);
    assertEquals("Part count must be two.", 2, message.partCount());

    // Test getParts()
    Iterator iterator = message.getParts().iterator();
    while (iterator.hasNext())
    {
      FMessagePart part = (FMessagePart)iterator.next();

      assertTrue("Message must contain part that was added previously.", parts.contains(part));
    }
  }

  /**
   * Test methods to check singlepart/multipart flag.
   */
  @Test(timeout = 1000)
  public void testFlags()
  {
    FMessage message = FMessage.factory.create("foo");

    // Test singlepart message (only one part with 'type' attribute set)
    FMessagePart part = FMessagePart.factory.create("part", new QName("namespaceURI", "localPart"), false);
    message.addPart(part);

    assertTrue("Message must be flagged as singlepart.", message.isSinglepart());
    assertTrue("Message must not be flagged as multipart.", !message.isMultipart());

    // Reset message object
    message = FMessage.factory.create("bar");

    // Test multipart message (all parts with 'element' attribite set
    FMessagePart firstPart = FMessagePart.factory.create("firstPart", new QName("namespaceURI", "localPart"), true);
    FMessagePart secondPart = FMessagePart.factory.create("secondPart", new QName("namespaceURI", "localPart"), true);

    message.addPart(firstPart);
    assertTrue("Message must not be flagged as singlepart.", !message.isSinglepart());
    assertTrue("Message must be flagged as multipart.", message.isMultipart());

    message.addPart(secondPart);
    assertTrue("Message must still not be flagged as singlepart.", !message.isSinglepart());
    assertTrue("Message must still be flagged as multipart.", message.isMultipart());
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    FMessage firstMessage = FMessage.factory.create("firstMessage");
    FMessage secondMessage = FMessage.factory.create("secondMessage");

    QName qName = new QName("namespaceURI", "localPart");
    FMessagePart firstPart = FMessagePart.factory.create("firstPart", qName, true);
    FMessagePart secondPart = FMessagePart.factory.create("secondPart", qName, true);

    // Unequality
    firstMessage.addPart(firstPart);
    firstMessage.addPart(secondPart);
    assertFalse("Message objects with different names and parts must not be equal.", firstMessage.equals(secondMessage));

    // Adding in different order should not affect equality!
    secondMessage.addPart(secondPart);
    secondMessage.addPart(firstPart);
    assertFalse("Message objects with different names must not be equal.", firstMessage.equals(secondMessage));

    // Equality
    firstMessage.setMessageName("foobar");
    secondMessage.setMessageName("foobar");
    assertTrue("Message objects must be equal.", firstMessage.equals(secondMessage));
  }
}
