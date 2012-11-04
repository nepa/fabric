/** 04.11.2012 03:04 */
package de.uniluebeck.sourcegen.plaintext;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Unit test for PlainTextWorkspace class.
 *
 * @author seidel
 */
public class PlainTextWorkspaceTest
{
  /**
   * Test deletion of plain text files.
   */
  @Test(timeout = 1000)
  public void testDeletion() throws Exception
  {
    // Add file to workspace
    Workspace workspace = new Workspace(new Properties());
    workspace.getPlainText().getPlainTextFile("ExistingFile", "txt");

    // Test successful removal
    assertTrue("Plain text file must be removed from workspace.",
            workspace.getPlainText().deletePlainTextFile("ExistingFile", "txt"));

    // Test removal of non-existing file
    assertFalse("Plain text file must not be removed from workspace.",
            workspace.getPlainText().deletePlainTextFile("OtherFile", "txt"));
  }
}
