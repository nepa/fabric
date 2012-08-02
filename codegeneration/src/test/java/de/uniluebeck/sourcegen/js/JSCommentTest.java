/** 02.08.2012 20:32 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for JSComment class.
 *
 * @author seidel
 */
public class JSCommentTest
{
  /**
   * Test object creation.
   */
  @Test(timeout = 1000)
  public void testCreation()
  {
    JSComment comment = new JSCommentImpl("A test comment.");
    assertNotNull("JSComment object must not be null.", comment);
    assertEquals("Text of comment must match initial value.", "A test comment.", comment.getDescription());
  }

  /**
   * Test object equality.
   */
  @Test(timeout = 1000)
  public void testEquality()
  {
    JSComment firstComment = new JSCommentImpl("This is a comment.");
    JSComment secondComment = new JSCommentImpl("This is another comment.");

    // Unequality
    assertFalse("Comments with different text must not be equal.", firstComment.equals(secondComment));

    // Equality
    secondComment.setDescription("This is a comment.");
    assertTrue("Comments must be equal.", firstComment.equals(secondComment));

    assertTrue("Comment must be equal to itself.", firstComment.equals(firstComment));
  }
}
