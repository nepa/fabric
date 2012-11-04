/** 04.11.2012 03:38 */
package de.uniluebeck.sourcegen.plaintext;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for PlainTextContent and PlainTextContentFactory class.
 *
 * @author seidel
 */
public class PlainTextContentTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    PlainTextContent textContent = new PlainTextContentImpl();
    assertNotNull("PlainTextContent object must not be null.", textContent);
    assertEquals("Text content must be empty.", "", textContent.toString());

    textContent = new PlainTextContentImpl("foobar");
    assertNotNull("PlainTextContent object must not be null.", textContent);
    assertEquals("Text content must match initial value.", "foobar", textContent.toString());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    PlainTextContent textContent = PlainTextContent.factory.create();
    assertNotNull("PlainTextContent object must not be null.", textContent);
    assertEquals("Text content must be empty.", "", textContent.toString());

    textContent = PlainTextContent.factory.create("foobar");
    assertNotNull("PlainTextContent object must not be null.", textContent);
    assertEquals("Text content must match initial value.", "foobar", textContent.toString());
  }

  /**
   * Test code handling.
   */
  @Test(timeout = 1000)
  public void testCodeHandling()
  {
    PlainTextContent textContent = PlainTextContent.factory.create("Alpha", "Beta", "Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", textContent.toString());
    System.out.println(textContent);

    textContent.appendCode("Omega");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta\nOmega", textContent.toString());
    System.out.println(textContent);

    textContent.setCode("Foobar");
    assertEquals("Output must equal expected form.", "Foobar", textContent.toString());
    System.out.println(textContent);

    textContent.clearCode();
    assertEquals("Output must equal expected form.", "", textContent.toString());
    System.out.println(textContent);

    textContent.setCode("Beta");
    textContent.prependCode("Alpha");
    textContent.appendCode("Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", textContent.toString());
    System.out.println(textContent);
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    PlainTextContent firstTextContent = PlainTextContent.factory.create("nop();");
    PlainTextContent secondTextContent = PlainTextContent.factory.create("foobar();");

    // Unequality
    assertFalse("Objects with different text content must not be equal.", firstTextContent.equals(secondTextContent));

    // Equality
    secondTextContent.setCode("nop();");
    assertTrue("Text content of objects must be equal.", firstTextContent.equals(secondTextContent));

    assertTrue("Object with text content must be equal to itself.", firstTextContent.equals(firstTextContent));
  }
}
