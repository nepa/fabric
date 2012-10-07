/**
 * Copyright (c) 2010-2012, Institute of Telematics (Dennis Pfisterer, Marco Wegner, Dennis Boldt,
 * Sascha Seidel, Joss Widderich, et al.), University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 *   - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.uniluebeck.sourcegen.c;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Properties;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Unit test for CWorkspace class.
 *
 * @author seidel
 */
public class CWorkspaceTest
{
  /**
   * Test deletion of C++ header files.
   */
  @Test(timeout = 1000)
  public void testCppHeaderFileDeletion() throws Exception
  {
    // Add file to workspace
    CWorkspace cWorkspace = (new Workspace(new Properties())).getC();
    cWorkspace.getCppHeaderFile("ExistingFile");

    // Test successful removal
    assertTrue("C++ header file must be removed from workspace.",
            cWorkspace.deleteCppHeaderFile("ExistingFile"));

    // Test removal of non-existing file
    assertFalse("C++ header file must not be removed from workspace.",
            cWorkspace.deleteCppHeaderFile("OtherFile"));
  }

  /**
   * Test deletion of C++ source files.
   */
  @Test(timeout = 1000)
  public void testCppSourceFileDeletion() throws Exception
  {
    // Add file to workspace
    CWorkspace cWorkspace = (new Workspace(new Properties())).getC();
    cWorkspace.getCppSourceFile("ExistingFile");

    // Test successful removal
    assertTrue("C++ source file must be removed from workspace.",
            cWorkspace.deleteCppSourceFile("ExistingFile"));

    // Test removal of non-existing file
    assertFalse("C++ source file must not be removed from workspace.",
            cWorkspace.deleteCppSourceFile("OtherFile"));
  }

  /**
   * Test deletion of C header files.
   */
  @Test(timeout = 1000)
  public void testCHeaderFileDeletion() throws Exception
  {
    // Add file to workspace
    CWorkspace cWorkspace = (new Workspace(new Properties())).getC();
    cWorkspace.getCHeaderFile("ExistingFile");

    // Test successful removal
    assertTrue("C header file must be removed from workspace.",
            cWorkspace.deleteCHeaderFile("ExistingFile"));

    // Test removal of non-existing file
    assertFalse("C header file must not be removed from workspace.",
            cWorkspace.deleteCHeaderFile("OtherFile"));
  }

  /**
   * Test deletion of C source files.
   */
  @Test(timeout = 1000)
  public void testCSourceFileDeletion() throws Exception
  {
    // Add file to workspace
    CWorkspace cWorkspace = (new Workspace(new Properties())).getC();
    cWorkspace.getCSourceFile("ExistingFile");

    // Test successful removal
    assertTrue("C source file must be removed from workspace.",
            cWorkspace.deleteCSourceFile("ExistingFile"));

    // Test removal of non-existing file
    assertFalse("C source file must not be removed from workspace.",
            cWorkspace.deleteCSourceFile("OtherFile"));
  }
}
