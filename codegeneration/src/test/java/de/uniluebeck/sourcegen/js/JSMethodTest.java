/** 01.08.2012 14:32 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Unit test for JSMethod and JSMethodFactory class.
 *
 * @author seidel
 */
public class JSMethodTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation() throws Exception
  {
    JSMethod method = new JSMethodImpl("foo");
    assertNotNull("JSMethod object must not be null.", method);
    assertEquals("Method name must match initial value.", "foo", method.getName());

    method = new JSMethodImpl("bar", true);
    assertNotNull("JSMethod object must not be null.", method);
    assertEquals("Method name must match initial value.", "bar", method.getName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory() throws Exception
  {
    JSMethod method = JSMethod.factory.create("foo");
    assertNotNull("JSMethod object must not be null.", method);
    assertEquals("Method name must match initial value.", "foo", method.getName());

    method = JSMethod.factory.create("bar", true);
    assertNotNull("JSMethod object must not be null.", method);
    assertEquals("Method name must match initial value.", "bar", method.getName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter() throws Exception
  {
    JSMethod method = JSMethod.factory.create("foo", true, "alpha", "beta");

    // Test prototype flag
    assertTrue("Prototype flag must match initial value.", method.getIsPrototypeFunction());
    assertEquals("Alias function must return same value for prototype flag.",
            method.getIsPrototypeFunction(), method.isPrototypeFunction());
    method.setIsPrototypeFunction(false);
    assertFalse("Prototype flag must match new value.", method.getIsPrototypeFunction());
    assertEquals("Alias function must still return same value for prototype flag.",
            method.getIsPrototypeFunction(), method.isPrototypeFunction());

    // Test name
    assertEquals("Method name must match initial value.", "foo", method.getName());
    method.setName("bar");
    assertEquals("Method name must match new value.", "bar", method.getName());

    // Test arguments
    assertTrue("Argument list must contain initial values.",
            method.getArguments().contains("alpha") &&
            method.getArguments().contains("beta"));
    method.setArguments("gamma", "delta");
    assertTrue("Argument list must contain new values.",
            method.getArguments().contains("gamma") &&
            method.getArguments().contains("delta"));
    assertTrue("Argument list must not contain initial values anymore.",
            !method.getArguments().contains("alpha") &&
            !method.getArguments().contains("beta"));

    // Test body
    assertEquals("Method body must be empty.", "", method.getBody().toString());
    method.getBody().appendCode("nop();");
    assertEquals("Method body must match new value.", "nop();", method.getBody().toString());

    // Test comment
    assertFalse("Method comment must be empty.", method.toString().startsWith("/**"));
    method.setComment(new JSCommentImpl("Some comment."));
    assertTrue("Method comment must contain new text.", method.toString().contains("Some comment."));
  }

  /**
   * Test handling of method arguments.
   */
  @Test(timeout = 1000)
  public void testArgumentHandling() throws Exception
  {
    JSMethod method = JSMethod.factory.create("foo", "alpha", "beta");

    // Test add()
    assertTrue("Argument list must contain initial values.",
            method.getArguments().contains("alpha") &&
            method.getArguments().contains("beta"));
    method.add("gamma", "delta");
    assertTrue("Argument list must contain new values.",
            method.getArguments().contains("gamma") &&
            method.getArguments().contains("delta"));
    assertTrue("Argument list must still contain initial values.",
            method.getArguments().contains("alpha") &&
            method.getArguments().contains("beta"));

    // Test for expected exception in constructor
    Exception exception = null;
    try
    {
      new JSMethodImpl("bar", "omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Constructor of JSMethod must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in factory
    exception = null;
    try
    {
      JSMethod.factory.create("foobar", "omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Factory mechanism of JSMethod must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in setArguments()
    exception = null;
    try
    {
      method.setArguments("omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSMethod must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in add()
    exception = null;
    try
    {
      method.add("omega");
      method.add("omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSMethod must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality() throws Exception
  {
    JSMethod firstMethod = JSMethod.factory.create("foo", true, "alpha", "beta");
    JSMethod secondMethod = JSMethod.factory.create("bar", false, "gamma", "delta");

    // Unequality
    assertFalse("Methods with different prototype flags, names and arguments must not be equal.",
            firstMethod.equals(secondMethod));

    secondMethod.setName("foo");
    assertFalse("Methods with different prototype flags and arguments must not be equal.",
            firstMethod.equals(secondMethod));

    firstMethod.setName("foobar");
    secondMethod.setArguments("alpha", "beta");
    assertFalse("Methods with different prototype flags and names must not be equal.",
            firstMethod.equals(secondMethod));

    secondMethod.setIsPrototypeFunction(true);
    secondMethod.setArguments("gamma", "delta");
    assertFalse("Methods with different names and arguments must not be equal.",
            firstMethod.equals(secondMethod));

    // Equality
    secondMethod.setName("foobar");
    secondMethod.setArguments("alpha", "beta");
    assertTrue("Methods must be equal.", firstMethod.equals(secondMethod));

    assertTrue("Methods must be equal to itself.", firstMethod.equals(firstMethod));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput() throws Exception
  {
    JSMethod method = JSMethod.factory.create("foobar", true, "alpha", "beta", "gamma", "delta");

    String functionBody =
            "var a = beta;\n" +
            "\n" +
            "return gamma;";
    method.getBody().setCode(functionBody);

    method.setComment(new JSCommentImpl("This is a test method."));

    assertTrue("Method output must contain header comment.", method.toString().contains("This is a test method."));
    assertTrue("Method output must contain 'prototype' keyword.", method.toString().contains("prototype"));
    assertTrue("Method output must contain method name", method.toString().contains("foobar"));
    assertTrue("Method output must contain argument list.", method.toString().contains("alpha, beta, gamma, delta"));
    assertTrue("Method output must contain body.", method.toString().contains("var a = beta;"));

    System.out.println(method);
  }
}
