/** 04.10.2012 00:36 */
package de.uniluebeck.sourcegen.js;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Unit test for JSWorkspace class.
 *
 * @author seidel
 */
public class JSWorkspaceTest
{
  /**
   * Test deletion of JavaScript source files.
   */
  @Test(timeout = 1000)
  public void testDeletion() throws Exception
  {
    // Add file to workspace
    Workspace workspace = new Workspace(new Properties());
    workspace.getJavaScript().getJSSourceFile("ExistingFile");

    // Test successful removal
    assertTrue("Source file must be removed from JavaScript workspace.",
            workspace.getJavaScript().deleteJSSourceFile("ExistingFile"));

    // Test removal of non-existing file
    assertFalse("Source file must not be removed from JavaScript workspace.",
            workspace.getJavaScript().deleteJSSourceFile("OtherFile"));
  }
}
