/** 02.08.2012 15:10 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for JSCodeBlock and JSCodeBlockFactory class.
 *
 * @author seidel
 */
public class JSCodeBlockTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    JSCodeBlock codeBlock = new JSCodeBlockImpl();
    assertNotNull("JSCodeBlock object must not be null.", codeBlock);
    assertEquals("Source code must be empty.", "", codeBlock.toString());

    codeBlock = new JSCodeBlockImpl("foobar");
    assertNotNull("JSCodeBlock object must not be null.", codeBlock);
    assertEquals("Source code must match initial value.", "foobar", codeBlock.toString());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    JSCodeBlock codeBlock = JSCodeBlock.factory.create();
    assertNotNull("JSCodeBlock object must not be null.", codeBlock);
    assertEquals("Source code must be empty.", "", codeBlock.toString());

    codeBlock = JSCodeBlock.factory.create("foobar");
    assertNotNull("JSCodeBlock object must not be null.", codeBlock);
    assertEquals("Source code must match initial value.", "foobar", codeBlock.toString());
  }

  /**
   * Test code handling.
   */
  @Test(timeout = 1000)
  public void testCodeHandling()
  {
    JSCodeBlock codeBlock = JSCodeBlock.factory.create("Alpha", "Beta", "Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", codeBlock.toString());
    System.out.println(codeBlock);

    codeBlock.appendCode("Omega");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta\nOmega", codeBlock.toString());
    System.out.println(codeBlock);

    codeBlock.setCode("Foobar");
    assertEquals("Output must equal expected form.", "Foobar", codeBlock.toString());
    System.out.println(codeBlock);

    codeBlock.clearCode();
    assertEquals("Output must equal expected form.", "", codeBlock.toString());
    System.out.println(codeBlock);

    codeBlock.setCode("Beta");
    codeBlock.prependCode("Alpha");
    codeBlock.appendCode("Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", codeBlock.toString());
    System.out.println(codeBlock);
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    JSCodeBlock firstCodeBlock = JSCodeBlock.factory.create("nop();");
    JSCodeBlock secondCodeBlock = JSCodeBlock.factory.create("foobar();");

    // Unequality
    assertFalse("Code blocks with different code must not be equal.", firstCodeBlock.equals(secondCodeBlock));

    // Equality
    secondCodeBlock.setCode("nop();");
    assertTrue("Code blocks must be equal.", firstCodeBlock.equals(secondCodeBlock));

    assertTrue("Code block must be equal to itself.", firstCodeBlock.equals(firstCodeBlock));
  }
}
