/** 02.08.2012 00:05 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Unit test for JSSourceFile class.
 *
 * @author seidel
 */
public class JSSourceFileTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    assertNotNull("JSSourceFile object must not be null.", sourceFile);
    assertEquals("Name of source file must match initial value.", "foobar", sourceFile.getFileName());
  }

  /**
   * Test type object handling.
   */
  @Test(timeout = 1000)
  public void testTypeObjectHandling() throws Exception
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    JSFunction function = JSFunction.factory.create("foobar", "alpba", "beta", "gamma", "delta");

    assertFalse("Source file must not contain function object.", sourceFile.contains(function));
    sourceFile.add(function);
    assertTrue("Source file must contain function object.", sourceFile.contains(function));

    // Test for expected exception
    Exception exception = null;
    try
    {
      sourceFile.add(function);
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSSourceFile must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test comment handling.
   */
  @Test(timeout = 1000)
  public void testCommentHandling()
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");

    assertFalse("Source file must not contain header comment.", sourceFile.toString().startsWith("/**"));
    sourceFile.setComment(new JSCommentImpl("A JavaScript test file."));
    assertTrue("Source file must contain header comment.", sourceFile.toString().startsWith("/**"));
    assertTrue("Source file comment must match initial value.", sourceFile.toString().contains("A JavaScript test file."));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput() throws Exception
  {
    // Create source file
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    sourceFile.setComment(new JSCommentImpl("A JavaScript test file."));
    
    // Create global variable
    JSField field = JSField.factory.create("foo", "\"This is a global string.\"");
    field.setComment(new JSCommentImpl("A global variable."));
    sourceFile.add(field);

    // Create class
    JSClass jsc = JSClassTest.createClass(true);
    sourceFile.add(jsc);

    // Create global function
    JSFunction function = JSFunction.factory.create("foobar", "alpba", "beta", "gamma", "delta");
    function.setComment(new JSCommentImpl("A test function."));

    String functionBody =
            "var a = beta;\n" +
            "\n" +
            "return gamma;";
    function.getBody().setCode(functionBody);

    sourceFile.add(function);

    assertTrue("Source file must contain header comment.", sourceFile.toString().contains("A JavaScript test file."));
    assertTrue("Source file must contain 'foobar' function.", sourceFile.toString().contains("foobar"));

    System.out.println(sourceFile);
  }

  // TODO: Implement further tests
}
