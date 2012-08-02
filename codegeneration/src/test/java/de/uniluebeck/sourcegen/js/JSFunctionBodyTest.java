/** 02.08.2012 20:19 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for JSFunctionBody and JSFunctionBodyFactory class.
 *
 * @author seidel
 */
public class JSFunctionBodyTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    JSFunctionBody functionBody = new JSFunctionBodyImpl();
    assertNotNull("JSFunctionBody object must not be null.", functionBody);
    assertEquals("Source code must be empty.", "", functionBody.toString());

    functionBody = new JSFunctionBodyImpl("foobar");
    assertNotNull("JSFunctionBody object must not be null.", functionBody);
    assertEquals("Source code must match initial value.", "foobar", functionBody.toString());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    JSFunctionBody functionBody = JSFunctionBody.factory.create();
    assertNotNull("JSFunctionBody object must not be null.", functionBody);
    assertEquals("Source code must be empty.", "", functionBody.toString());

    functionBody = JSFunctionBody.factory.create("foobar");
    assertNotNull("JSFunctionBody object must not be null.", functionBody);
    assertEquals("Source code must match initial value.", "foobar", functionBody.toString());
  }

  /**
   * Test code handling.
   */
  @Test(timeout = 1000)
  public void testCodeHandling()
  {
    JSFunctionBody functionBody = JSFunctionBody.factory.create("Alpha", "Beta", "Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", functionBody.toString());
    System.out.println(functionBody);

    functionBody.appendCode("Omega");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta\nOmega", functionBody.toString());
    System.out.println(functionBody);

    functionBody.setCode("Foobar");
    assertEquals("Output must equal expected form.", "Foobar", functionBody.toString());
    System.out.println(functionBody);

    functionBody.clearCode();
    assertEquals("Output must equal expected form.", "", functionBody.toString());
    System.out.println(functionBody);

    functionBody.setCode("Beta");
    functionBody.prependCode("Alpha");
    functionBody.appendCode("Gamma", "Delta");
    assertEquals("Output must equal expected form.", "Alpha\nBeta\nGamma\nDelta", functionBody.toString());
    System.out.println(functionBody);
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    JSFunctionBody firstFunctionBody = JSFunctionBody.factory.create("nop();");
    JSFunctionBody secondFunctionBody = JSFunctionBody.factory.create("foobar();");

    // Unequality
    assertFalse("Function bodies with different code must not be equal.", firstFunctionBody.equals(secondFunctionBody));

    // Equality
    secondFunctionBody.setCode("nop();");
    assertTrue("Function bodies must be equal.", firstFunctionBody.equals(secondFunctionBody));

    assertTrue("Function body must be equal to itself.", firstFunctionBody.equals(firstFunctionBody));
  }
}
