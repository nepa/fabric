/** 03.11.2012 03:26 */
package de.uniluebeck.sourcegen.plaintext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Iterator;

import de.uniluebeck.sourcegen.SourceFile;
import de.uniluebeck.sourcegen.Workspace;

/**
 * Workspace class for plain text files.
 *
 * @author seidel
 */
public class PlainTextWorkspace
{
  /** Logger object */
  private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextWorkspace.class);

  /** Source files in workspace */
  private List<SourceFile> sourceFiles;

  /**
   * Parameterized constructor creates new workspace for
   * plain text files.
   *
   * @param workspace Workspace object with existing
   * source files
   */
  public PlainTextWorkspace(final Workspace workspace)
  {
    this.sourceFiles = workspace.getSourceFiles();
  }

  /**
   * Get a plain text file with the desired path, name and extension.
   * If such a file already exists in the workspace, the existing
   * instance is returned. Otherwise a new empty file will be created,
   * added to workspace and returned.
   *
   * @param path Path of plain text file
   * @param fileName Name of plain text file
   * @param extension Extension of plain text file
   *
   * @return Plain text file with desired name
   */
  public PlainTextFile getPlainTextFile(final String path, final String fileName, final String extension)
  {
    PlainTextFile result = null;

    // Check existing source files
    for (SourceFile file: this.sourceFiles)
    {
      if (file instanceof PlainTextFile)
      {
        PlainTextFile textFile = (PlainTextFile)file;

        // Plain text file does already exist
        if (path.equals(textFile.getPath()) && fileName.equals(textFile.getFileName()) && extension.equals(textFile.getExtension()))
        {
          LOGGER.info(String.format("Plain text file '%s.%s' already exists in path '%s'. " +
                  "Will return existing instance.", fileName, extension, path));

          result = textFile;
          break;  // Early exit
        }
      }
    }

    // Create a new text file
    if (null == result)
    {
      result = new PlainTextFileImpl(path, fileName, extension);
      this.sourceFiles.add(result);

      LOGGER.info(String.format("New plain text file '%s.%s' added to workspace.", fileName, extension));
    }

    return result;
  }

  /**
   * Get a plain text file with the desired name and extension from
   * the base folder of Fabric's workspace. If such a file already
   * exists in the workspace, the existing instance is returned.
   * Otherwise a new empty file will be created, added to workspace
   * and returned.
   *
   * @param fileName Name of plain text file
   * @param extension Extension of plain text file
   *
   * @return Plain text file with desired name
   */
  public PlainTextFile getPlainTextFile(final String fileName, final String extension)
  {
    return this.getPlainTextFile("", fileName, extension);
  }

  /**
   * Delete a file from the plain text workspace. The method will
   * return 'true' on success or 'false' if no file with the given
   * name was found.
   *
   * @param path Path of plain text file
   * @param fileName Name of plain text file
   * @param extension Extension of plain text file
   *
   * @return True if file was deleted successfully, false otherwise
   */
  public boolean deletePlainTextFile(final String path, final String fileName, final String extension)
  {
    boolean success = false;

    // Iterate all source files
    SourceFile file = null;
    Iterator<SourceFile> iterator = this.sourceFiles.iterator();
    while (iterator.hasNext())
    {
      file = iterator.next();

      // Search plain text files
      if (file instanceof PlainTextFile)
      {
        PlainTextFile textFile = (PlainTextFile)file;

        if (path.equals(textFile.getPath()) && fileName.equals(textFile.getFileName()) && extension.equals(textFile.getExtension()))
        {
          iterator.remove();
          success = true;

          LOGGER.info(String.format("Removed plain text file '%s.%s' from workspace.",
                  textFile.getFileName(), textFile.getExtension()));
        }
      }
    }

    if (!success)
    {
      LOGGER.info(String.format("Could not remove plain text file '%s.%s' from workspace. " +
              "File does not exist.", fileName, extension));
    }

    return success;
  }

  /**
   * Delete a file from base folder of the plain text workspace.
   * The method will return 'true' on success or 'false' if no
   * file with the given name was found.
   *
   * @param fileName Name of plain text file
   * @param extension Extension of plain text file
   *
   * @return True if file was deleted successfully, false otherwise
   */
  public boolean deletePlainTextFile(final String fileName, final String extension)
  {
    return this.deletePlainTextFile("", fileName, extension);
  }
}
