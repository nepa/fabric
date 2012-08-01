/** 28.07.2012 22:17 */
package de.uniluebeck.sourcegen.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.uniluebeck.sourcegen.SourceFile;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Workspace class for JavaScript source files.
 * 
 * @author seidel
 */
public class JavaScriptWorkspace
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptWorkspace.class);

  /** Source files in workspace */
  private List<SourceFile> sourceFiles;

  /**
   * Parameterized constructor creates new workspace for
   * JavaScript source files.
   *
   * @param workspace Workspace object with existing
   * source files
   */
  public JavaScriptWorkspace(final Workspace workspace)
  {
    this.sourceFiles = workspace.getSourceFiles();
  }

  /**
   * Get a JavaScript source file with the desired name. If
   * such a file already exists in the workspace, the existing
   * instance is returned. Otherwise a new empty source file
   * will be created, added to workspace and returned.
   *
   * @param fileName File name of JavaScript source file
   *
   * @return JavaScript source file with desired name
   */
  public JSSourceFile getJSSourceFile(final String fileName)
  {
    JSSourceFile result = null;

    // Check existing source files
    for (SourceFile file: this.sourceFiles)
    {
      if (file instanceof JSSourceFile)
      {
        JSSourceFile sourceFile = (JSSourceFile)file;

        // Source file does already exist
        if (fileName.equals(sourceFile.getFileName()))
        {
          LOGGER.info(String.format("JavaScript source file '%s' already exists. " +
                  "Will return existing instance.", fileName));

          result = sourceFile;
          break;  // Early exit
        }
      }
    }

    // Create a new source file
    if (null == result)
    {
      result = new JSSourceFileImpl(fileName);
      this.sourceFiles.add(result);

      LOGGER.info(String.format("New JavaScript source file '%s' added to workspace.", fileName));
    }

    return result;
  }
}
