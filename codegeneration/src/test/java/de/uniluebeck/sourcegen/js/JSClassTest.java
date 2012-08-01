/** 01.08.2012 15:23 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Unit test for JSClass and JSClassFactory class.
 *
 * @author seidel
 */
public class JSClassTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(false);
    assertNotNull("JSClass object must not be null.", jsc);
    assertEquals("Class name must match initial value.", "Apple", jsc.getName());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);
    assertNotNull("JSClass object must not be null.", jsc);
    assertEquals("Class name must match initial value.", "Apple", jsc.getName());
  }

  /**
   * Test setter and getter methods.
   */
  @Test(timeout = 1000)
  public void testSetterGetter() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);

    // Test name
    assertEquals("Class name must match initial value.", "Apple", jsc.getName());
    jsc.setName("Cucumber");
    assertEquals("Class name must match new value.", "Cucumber", jsc.getName());

    // Test parent
    assertEquals("Parent class name must match initial value.", "Fruit", jsc.getParent());
    jsc.setParent("Vegetable");
    assertEquals("Parent class name must match new value.", "Vegetable", jsc.getParent());

    // Test arguments
    assertTrue("Argument list must contain initial value.",
            jsc.getArguments().contains("price"));
    jsc.setArguments("weight", "size");
    assertTrue("Argument list must contain new values.",
            jsc.getArguments().contains("weight")
            && jsc.getArguments().contains("size"));
    assertTrue("Argument list must not contain initial value anymore.",
            !jsc.getArguments().contains("price"));

    // Test comment
    assertTrue("Class must contain a header comment.", jsc.toString().startsWith("/**"));
    assertTrue("Class comment must match initial value.", jsc.toString().contains("The 'Apple' class."));
    jsc.setComment(null);
    assertFalse("Class comment must be empty.", jsc.toString().startsWith("/**"));
  }

  /**
   * Test handling of class arguments.
   */
  @Test(timeout = 1000)
  public void testArgumentHandling() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);

    // Test add()
    assertTrue("Argument list must contain initial value.",
            jsc.getArguments().contains("price"));
    jsc.add("weight", "size");
    assertTrue("Argument list must contain new values.",
            jsc.getArguments().contains("weight")
            && jsc.getArguments().contains("size"));
    assertTrue("Argument list must still contain initial value.",
            jsc.getArguments().contains("price"));

    // Test for expected exception in constructor
    Exception exception = null;
    try
    {
      new JSClassImpl("Apple", "price", "price");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Constructor of JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in factory
    exception = null;
    try
    {
      JSClass.factory.create("Apple", "price", "price");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("Factory mechanism of JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in setArguments()
    exception = null;
    try
    {
      jsc.setArguments("price", "price");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }

    // Test for expected exception in add()
    exception = null;
    try
    {
      jsc.add("price");
      jsc.add("price");
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test handling of member attributes (i.e. fields).
   */
  @Test(timeout = 1000)
  public void testFieldHandling() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);

    // Test add()
    assertTrue("Class must contain initial fields.",
            jsc.contains(JSField.factory.create("color", "\"red\"")) &&
            jsc.contains(JSField.factory.create("taste", "\"sweet\"")) &&
            jsc.contains(JSField.factory.create("price", "price")));
    jsc.add(JSField.factory.create("weight", "2 kg"), JSField.factory.create("size", "20 cm"));
    assertTrue("Class must contain new fields.",
            jsc.contains(JSField.factory.create("weight", "2 kg")) &&
            jsc.contains(JSField.factory.create("size", "20 cm")));
    assertTrue("Class must still contain initial fields.",
            jsc.contains(JSField.factory.create("color", "\"red\"")) &&
            jsc.contains(JSField.factory.create("taste", "\"sweet\"")) &&
            jsc.contains(JSField.factory.create("price", "price")));

    // Test for expected exception in add()
    Exception exception = null;
    try
    {
      jsc.add(JSField.factory.create("foo", "bar"));
      jsc.add(JSField.factory.create("foo", "bar"));
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test handling of member functions (i.e. methods).
   */
  @Test(timeout = 1000)
  public void testFuntionHandling() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);

    // Test add()
    assertTrue("Class must contain initial method.",
            jsc.contains(JSMethod.factory.create("setPrice", true, "price")));
    jsc.add(JSMethod.factory.create("setWeight", true, "weight"));
    assertTrue("Class must contain new method.",
            jsc.contains(JSMethod.factory.create("setWeight", true, "weight")));
    assertTrue("Class must still contain initial method.",
            jsc.contains(JSMethod.factory.create("setPrice", true, "price")));

    // Test for expected exception in add()
    Exception exception = null;
    try
    {
      jsc.add(JSMethod.factory.create("setFoo", true, "foo"));
      jsc.add(JSMethod.factory.create("setFoo", true, "foo"));
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSClass must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality() throws Exception
  {
    JSClass firstClass = JSClassTest.createClass(true);
    JSClass secondClass = JSClassTest.createClass(true);

    // Equality
    assertTrue("Classes must be equal.", firstClass.equals(secondClass));
    assertTrue("Class must be equal to itself.", firstClass.equals(firstClass));

    // Unequality
    secondClass.setName("Cucumber");
    assertFalse("Classes with different names must not be equal.", firstClass.equals(secondClass));

    secondClass.setName("Apple");
    secondClass.setParent("Vegetable");
    assertFalse("Classes with different parent classes must not be equal.", firstClass.equals(secondClass));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput() throws Exception
  {
    JSClass jsc = JSClassTest.createClass(true);

    assertTrue("Class output must contain header comment.", jsc.toString().contains("The 'Apple' class."));
    assertTrue("Class output must contain parent class name.", jsc.toString().contains("Fruit"));
    assertTrue("Class output must contain class name.", jsc.toString().contains("Apple"));
    assertTrue("Class output must contain argument list.", jsc.toString().contains("price"));
    assertTrue("Class output must contain fields.",
            jsc.toString().contains("this.color = \"red\";") &&
            jsc.toString().contains("this.taste = \"sweet\";") &&
            jsc.toString().contains("this.price = price;"));
    assertTrue("Class output must contain method header and body.",
            jsc.toString().contains("this.prototype.setPrice = function (price)") && // Method header
            jsc.toString().contains("this.price = price;")); // Method body

    System.out.println(jsc);
  }

  /**
   * Private helper method to create a JSClass object.
   *
   * @return JSClass object
   */
  protected static JSClass createClass(final boolean withFactory) throws Exception
  {
    JSClass jsc = null;

    if (withFactory)
    {
      jsc = JSClass.factory.create("Apple", "price");
    }
    else
    {
      jsc = new JSClassImpl("Apple", "price");
    }

    jsc.setParent("Fruit");

    jsc.add(JSField.factory.create("color", "\"red\""));
    jsc.add(JSField.factory.create("taste", "\"sweet\""));
    jsc.add(JSField.factory.create("price", "price"));

    JSMethod method = JSMethod.factory.create("setPrice", true, "price");
    method.getBody().setCode("this.price = price;");
    jsc.add(method);

    jsc.setComment(new JSCommentImpl("The 'Apple' class."));

    return jsc;
  }
}
