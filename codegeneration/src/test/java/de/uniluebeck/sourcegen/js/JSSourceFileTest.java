/** 03.08.2012 01:14 */
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
   * Test field handling.
   */
  @Test(timeout = 1000)
  public void testFieldHandling() throws Exception
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    JSField field = JSField.factory.create("foo", "\"bar\"");

    assertTrue("Source file must not contain any fields.", sourceFile.getFields().isEmpty());
    assertFalse("Source file must not contain field object.", sourceFile.contains(field));
    sourceFile.add(field);
    assertEquals("Source file must contain one field.", 1, sourceFile.getFields().size());
    assertTrue("Source file must contain field object.", sourceFile.contains(field));

    // Test for expected exception
    Exception exception = null;
    try
    {
      sourceFile.add(field);
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSSourceFile must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test class handling.
   */
  @Test(timeout = 1000)
  public void testClassHandling() throws Exception
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    JSClass jsc = JSClass.factory.create("Foobar", "foo", "bar");

    assertTrue("Source file must not contain any classes.", sourceFile.getClasses().isEmpty());
    assertFalse("Source file must not contain class object.", sourceFile.contains(jsc));
    sourceFile.add(jsc);
    assertEquals("Source file must contain one class.", 1, sourceFile.getClasses().size());
    assertTrue("Source file must contain class object.", sourceFile.contains(jsc));

    // Test for expected exception
    Exception exception = null;
    try
    {
      sourceFile.add(jsc);
    }
    catch (Exception e)
    {
      exception = e;
      assertTrue("JSSourceFile must throw 'JSDuplicateException'.", exception instanceof JSDuplicateException);
    }
  }

  /**
   * Test function handling.
   */
  @Test(timeout = 1000)
  public void testFunctionHandling() throws Exception
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");
    JSFunction function = JSFunction.factory.create("foobar", "foo", "bar");

    assertTrue("Source file must not contain any functions.", sourceFile.getFunctions().isEmpty());
    assertFalse("Source file must not contain function object.", sourceFile.contains(function));
    sourceFile.add(function);
    assertEquals("Source file must contain one function.", 1, sourceFile.getFunctions().size());
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
   * Test code block handling.
   */
  @Test(timeout = 1000)
  public void testCodeBlockHandling()
  {
    JSSourceFile sourceFile = new JSSourceFileImpl("foobar");

    // Test before fields
    String code = "// Code block before fields";
    sourceFile.getCodeBeforeFields().setCode(code);
    assertEquals("Code block before fields must match initial value.",
            code, sourceFile.getCodeBeforeFields().toString());

    // Test before classes
    code = "// Code block before classes";
    sourceFile.getCodeBeforeClasses().setCode(code);
    assertEquals("Code block before classes must match initial value.",
            code, sourceFile.getCodeBeforeClasses().toString());

    // Test before functions
    code = "// Code block before functions";
    sourceFile.getCodeBeforeFunctions().setCode(code);
    assertEquals("Code block before functions must match initial value.",
            code, sourceFile.getCodeBeforeFunctions().toString());

    // Test after functions
    code = "// Code block after functions";
    sourceFile.getCodeAfterFunctions().setCode(code);
    assertEquals("Code block after functions must match initial value.",
            code, sourceFile.getCodeAfterFunctions().toString());
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
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    JSSourceFile firstFile = new JSSourceFileImpl("foo");
    JSSourceFile secondFile = new JSSourceFileImpl("foo");

    // Equality
    assertTrue("Source files must be equal.", firstFile.equals(secondFile));
    assertTrue("Source file must be equal to itselt.", firstFile.equals(firstFile));

    // Unequality
    secondFile.setFileName("bar");
    assertFalse("Source files with different names must not be equal.", firstFile.equals(secondFile));
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

    // Create another global variable
    JSField anotherField = JSField.factory.create("bar", "\"This is another global string.\"");
    anotherField.setComment(new JSCommentImpl("Another global variable."));
    sourceFile.add(anotherField);

    // Create class
    JSClass jsc = JSClassTest.createClass(true);
    sourceFile.add(jsc);

    // Create global function
    JSFunction function = JSFunction.factory.create("foobar", "alpha", "beta", "gamma", "delta");
    function.setComment(new JSCommentImpl("A test function."));

    String functionBody =
            "var a = beta;\n" +
            "\n" +
            "return gamma;";
    function.getBody().setCode(functionBody);

    sourceFile.add(function);

    // Create code blocks
    sourceFile.getCodeBeforeFields().setCode("// Code block before fields");
    sourceFile.getCodeBeforeClasses().setCode("// Code block before classes");
    sourceFile.getCodeBeforeFunctions().setCode("// Code block before functions");
    sourceFile.getCodeAfterFunctions().setCode("// Code block after functions");

    String code = sourceFile.toString();
    assertTrue("Source file must contain header comment.", code.contains("A JavaScript test file."));
    assertTrue("Source file must contain 'foo' field.", code.contains("var foo"));
    assertTrue("Source file must contain 'bar' field.", code.contains("var bar"));
    assertTrue("Source file must contain 'Apple' class.", code.contains("function Apple"));
    assertTrue("Source file must contain 'foobar' function.", code.contains("foobar"));

    assertTrue("Source file must contain code block before fields.", code.contains("Code block before fields"));
    assertTrue("Source file must contain code block before classes.", code.contains("Code block before classes"));
    assertTrue("Source file must contain code block before functions.", code.contains("Code block before functions"));
    assertTrue("Source file must contain code block after functions.", code.contains("Code block after functions"));

    System.out.println(sourceFile);
  }
}
