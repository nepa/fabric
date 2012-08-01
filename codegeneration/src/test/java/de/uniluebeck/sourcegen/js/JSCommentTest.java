/** 30.07.2012 19:52 */
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
  public void testCreation() throws Exception
  {
    JSComment comment = new JSCommentImpl("A test comment.");
    assertNotNull("JSComment object must not be null.", comment);
    assertEquals("Text of comment must match initial value.", "A test comment.", comment.getDescription());
  }
}
