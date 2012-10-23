/** 23.10.2012 18:38 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for JSField and JSFieldFactory class.
 *
 * @author seidel
 */
public class JSFieldTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    JSField field = new JSFieldImpl("foo", "barObject");
    assertNotNull("JSField object must not be null.", field);
    assertEquals("Field name must match initial value.", "foo", field.getName());
    assertEquals("Field value must match initial value.", "barObject", field.getInitValue());
  }

  /**
   * Test object creation with factory.
   */
  @Test(timeout = 1000)
  public void testFactory()
  {
    JSField field = JSField.factory.create("foo", "barObject");
    assertNotNull("JSField object must not be null.", field);
    assertEquals("Field name must match initial value.", "foo", field.getName());
    assertEquals("Field value must match initial value.", "barObject", field.getInitValue());
  }

  /**
   * Test setter and getter methods.
   */
  public void testSetterGetter()
  {
    JSField field = JSField.factory.create("foo", "fooObject");

    // Test name
    assertEquals("Field name must match initial value.", "foo", field.getName());
    field.setName("bar");
    assertEquals("Field name must match new value.", "bar", field.getName());

    // Test initial value
    assertEquals("Field value must match initial value.", "fooObject", field.getInitValue());
    field.setInitValue("barObject");
    assertEquals("Field value must match new value.", "barObject", field.getInitValue());
  }

  /**
   * Test comment handling.
   */
  @Test(timeout = 1000)
  public void testCommentHandling()
  {
    JSField field = JSField.factory.create("foo", "fooObject");

    assertFalse("Field must not contain a comment.", field.toString().startsWith("/**"));
    field.setComment(new JSCommentImpl("A field comment."));
    assertTrue("Field must contain a comment.", field.toString().startsWith("/**"));
    assertTrue("Field comment must match initual value.", field.toString().contains("A field comment."));
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    JSField firstField = JSField.factory.create("foo", "alpha");
    JSField secondField = JSField.factory.create("bar", "beta");

    // Unequality
    assertFalse("Fields with different names and initial values must not be equal.", firstField.equals(secondField));

    secondField.setName("foo");
    assertFalse("Fields with different initial values must not be equal.", firstField.equals(secondField));

    secondField.setInitValue(null);
    assertFalse("Fields with different initial values must not be equal.", firstField.equals(secondField));

    firstField.setName("foobar");
    secondField.setInitValue("alpha");
    assertFalse("Fields with different names must not be equal.", firstField.equals(secondField));

    // Equality
    secondField.setName("foobar");
    assertTrue("Fields must be equal.", firstField.equals(secondField));

    firstField.setInitValue(null);
    secondField.setInitValue(null);
    assertTrue("Fields must be equal.", firstField.equals(secondField));

    assertTrue("Field must be equal to itself.", firstField.equals(secondField));
  }

  /**
   * Test object output.
   */
  @Test(timeout = 1000)
  public void testOutput()
  {
    JSField field = JSField.factory.create("foo", "barObject");
    System.out.println(field);
    assertTrue("Field output must contain field name.", field.toString().contains("foo"));
    assertTrue("Field output must contain initial value.", field.toString().contains("barObject"));

    field = new JSFieldImpl("bar", "\"Initial value.\"");
    field.setComment(new JSCommentImpl("Field with string value."));
    System.out.println(field);
    assertTrue("Field output must contain comment.", field.toString().contains("Field with string value."));
  }
}
