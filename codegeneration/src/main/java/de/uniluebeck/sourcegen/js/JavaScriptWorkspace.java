/** 02.11.2012 23:18 */
package de.uniluebeck.sourcegen.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Iterator;

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
   * Get a JavaScript source file with the desired path and name.
   * If such a file already exists in the workspace, the existing
   * instance is returned. Otherwise a new empty source file will
   * be created, added to workspace and returned.
   *
   * @param path Path of JavaScript source file
   * @param fileName File name of JavaScript source file
   *
   * @return JavaScript source file with desired name
   */
  public JSSourceFile getJSSourceFile(final String path, final String fileName)
  {
    JSSourceFile result = null;

    // Check existing source files
    for (SourceFile file: this.sourceFiles)
    {
      if (file instanceof JSSourceFile)
      {
        JSSourceFile sourceFile = (JSSourceFile)file;

        // Source file does already exist
        if (path.equals(sourceFile.getPath()) && fileName.equals(sourceFile.getFileName()))
        {
          LOGGER.info(String.format("JavaScript source file '%s' already exists in path '%s'. " +
                  "Will return existing instance.", fileName, path));

          result = sourceFile;
          break;  // Early exit
        }
      }
    }

    // Create a new source file
    if (null == result)
    {
      result = new JSSourceFileImpl(path, fileName);
      this.sourceFiles.add(result);

      LOGGER.info(String.format("New JavaScript source file '%s' added to workspace.", fileName));
    }

    return result;
  }

  /**
   * Get a JavaScript source file with the desired name from the
   * base folder of Fabric's workspace. If such a file already
   * exists in the workspace, the existing instance is returned.
   * Otherwise a new empty source file will be created, added
   * to workspace and returned.
   *
   * @param fileName File name of JavaScript source file
   *
   * @return JavaScript source file with desired name
   */
  public JSSourceFile getJSSourceFile(final String fileName)
  {
    return this.getJSSourceFile("", fileName);
  }

  /**
   * Delete a source file from the JavaScript workspace. The method
   * will return 'true' on success or 'false' if no file with the
   * given name was found.
   *
   * @param path Path of JavaScript source file
   * @param fileName File name of JavaScript source file
   *
   * @return True if file was deleted successfully, false otherwise
   */
  public boolean deleteJSSourceFile(final String path, final String fileName)
  {
    boolean success = false;

    // Iterate all source files
    SourceFile sourceFile = null;
    Iterator<SourceFile> iterator = this.sourceFiles.iterator();
    while (iterator.hasNext())
    {
      sourceFile = iterator.next();

      // Search JavaScript source files
      if (sourceFile instanceof JSSourceFile)
      {
        JSSourceFile jssf = (JSSourceFile)sourceFile;

        if (path.equals(jssf.getPath()) && fileName.equals(jssf.getFileName()))
        {
          iterator.remove();
          success = true;

          LOGGER.info(String.format("Removed JavaScript source file '%s' from workspace.",
                  jssf.getFileName()));
        }
      }
    }

    if (!success)
    {
      LOGGER.info(String.format("Could not remove source file '%s' from workspace. " +
              "File does not exist.", fileName));
    }

    return success;
  }

  /**
   * Delete a source file from base folder of the JavaScript
   * workspace. The method will return 'true' on success or
   * 'false' if no file with the given name was found.
   *
   * @param fileName File name of JavaScript source file
   *
   * @return True if file was deleted successfully, false otherwise
   */
  public boolean deleteJSSourceFile(final String fileName)
  {
    return this.deleteJSSourceFile("", fileName);
  }
}
