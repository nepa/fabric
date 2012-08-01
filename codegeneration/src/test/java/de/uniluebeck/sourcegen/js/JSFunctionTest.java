/** 30.07.2012 17:58 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Unit test for JSFunction and JSFunctionFactory class.
 *
 * @author seidel
 */
public class JSFunctionTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation() throws Exception
  {
    JSFunction function = new JSFunctionImpl("foobar");
    assertNotNull("JSFunction object must not be null.", function);
    assertEquals("Function name must match initial value.", "foobar", function.getName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory() throws Exception
  {
    JSFunction function = JSFunction.factory.create("foobar");
    assertNotNull("JSFunction object must not be null.", function);
    assertEquals("Function name must match initial value.", "foobar", function.getName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter() throws Exception
  {
    JSFunction function = JSFunction.factory.create("foo", "alpha", "beta");

    // Test name
    assertEquals("Function name must match initial value.", "foo", function.getName());
    function.setName("bar");
    assertEquals("Function name must match new value.", "bar", function.getName());

    // Test arguments
    assertTrue("Argument list must contain initial values.",
            function.getArguments().contains("alpha") &&
            function.getArguments().contains("beta"));
    function.setArguments("gamma", "delta");
    assertTrue("Argument list must contain new values.",
            function.getArguments().contains("gamma") &&
            function.getArguments().contains("delta"));
    assertTrue("Argument list must not contain initial values anymore.",
            !function.getArguments().contains("alpha") &&
            !function.getArguments().contains("beta"));

    // Test body
    assertEquals("Function body must be empty.", "", function.getBody().toString());
    function.getBody().appendCode("nop();");
    assertEquals("Function body must match new value.", "nop();", function.getBody().toString());

    // Test comment
    assertFalse("Function comment must be empty.", function.toString().startsWith("/**"));
    function.setComment(new JSCommentImpl("Some comment."));
    assertTrue("Function comment must contain new text.", function.toString().contains("Some comment."));
  }

  /**
   * Test handling of function arguments.
   */
  @Test(timeout = 1000)
  public void testArgumentHandling() throws Exception
  {
    JSFunction function = JSFunction.factory.create("foo", "alpha", "beta");

    // Test add()
    assertTrue("Argument list must contain initial values.",
            function.getArguments().contains("alpha") &&
            function.getArguments().contains("beta"));
    function.add("gamma", "delta");
    assertTrue("Argument list must contain new values.",
            function.getArguments().contains("gamma") &&
            function.getArguments().contains("delta"));
    assertTrue("Argument list must still contain initial values.",
            function.getArguments().contains("alpha") &&
            function.getArguments().contains("beta"));

    // Test for expected exception in constructor
    Exception exception = null;
    try
    {
      new JSFunctionImpl("bar", "omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Constructor of JSFunction must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in factory
    exception = null;
    try
    {
      JSFunction.factory.create("foobar", "omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Factory mechanism of JSFunction must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in setArguments()
    exception = null;
    try
    {
      function.setArguments("omega", "omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSFunction must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in add()
    exception = null;
    try
    {
      function.add("omega");
      function.add("omega");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSFunction must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality() throws Exception
  {
    JSFunction firstFunction = JSFunction.factory.create("foo", "alpha", "beta");
    JSFunction secondFunction = JSFunction.factory.create("bar", "gamma", "delta");

    // Unequality
    assertFalse("Functions with different names and arguments must not be equal.", firstFunction.equals(secondFunction));

    secondFunction.setName("foo");
    assertFalse("Functions with different arguments must not be equal.", firstFunction.equals(secondFunction));

    firstFunction.setName("foobar");
    secondFunction.setArguments("alpha", "beta");
    assertFalse("Functions with different names must not be equal.", firstFunction.equals(secondFunction));

    // Equality
    secondFunction.setName("foobar");
    assertTrue("Functions must be equal.", firstFunction.equals(secondFunction));

    assertTrue("Function must be equal to itself.", firstFunction.equals(firstFunction));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput() throws Exception
  {
    JSFunction function = JSFunction.factory.create("foobar", "alpha", "beta", "gamma", "delta");

    String functionBody =
            "var a = beta;\n" +
            "\n" +
            "return gamma;";
    function.getBody().setCode(functionBody);

    function.setComment(new JSCommentImpl("This is a test function."));

    assertTrue("Function output must contain header comment.", function.toString().contains("This is a test function."));
    assertTrue("Function output must contain function name", function.toString().contains("foobar"));
    assertTrue("Function output must contain argument list.", function.toString().contains("alpha, beta, gamma, delta"));
    assertTrue("Function output must contain body.", function.toString().contains("var a = beta;"));

    System.out.println(function);
  }
}
