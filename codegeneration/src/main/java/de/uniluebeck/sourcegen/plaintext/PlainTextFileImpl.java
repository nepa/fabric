/** 03.11.2012 20:19 */
package de.uniluebeck.sourcegen.plaintext;

/**
 * Implementation of the PlainTextFile interface. Objects
 * of this class represent virtual plain text files in
 * Fabric's workspace.
 *
 * @author seidel
 */
public class PlainTextFileImpl extends PlainTextComplexTypeImpl implements PlainTextFile
{
  /** Path of the plain text file */
  private String path;

  /** Name of the plain text file */
  private String fileName;

  /** Extension of the plain text file */
  private String extension;

  /** Content of the plain text file */
  private PlainTextContent content;

  /**
   * Parameterized constructor creates new plain text
   * file with the given path, name and extension.
   *
   * @param path Desired path for plain text file
   * @param fileName Desired name for plain text file
   * @param extension Desired extension for plain text file
   */
  public PlainTextFileImpl(final String path, final String fileName, final String extension)
  {
    this.path = path;
    this.fileName = fileName;
    this.extension = extension;
    this.content = PlainTextContent.factory.create();
  }

  /**
   * Parameterized constructor creates new plain text
   * file with the given name and extension in root
   * folder of Fabric's workspace.
   *
   * @param fileName Desired name for plain text file
   * @param extension Desired extension for plain text file
   */
  public PlainTextFileImpl(final String fileName, final String extension)
  {
    this("", fileName, extension);
  }

  /**
   * Set path of the plain text file.
   *
   * @param path Desired path for plain text file
   *
   * @return PlainTextFile object
   */
  @Override
  public PlainTextFile setPath(final String path)
  {
    this.path = path;

    return this;
  }

  /**
   * Get path of the plain text file.
   *
   * @return Path of plain text file
   */
  @Override
  public String getPath()
  {
    return this.path;
  }

  /**
   * Set name of the plain text file.
   *
   * @param fileName Desired name for plain text file
   *
   * @return PlainTextFile object
   */
  @Override
  public PlainTextFile setFileName(final String fileName)
  {
    this.fileName = fileName;

    return this;
  }

  /**
   * Get name of the plain text file.
   *
   * @return Name of plain text file
   */
  @Override
  public String getName()
  {
    return this.fileName;
  }

  /**
   * Get name of the plain text file.
   *
   * @return Name of plain text file
   */
  @Override
  public String getFileName()
  {
    return this.getName();
  }

  /**
   * Set extension of the plain text file.
   *
   * @param extension Desired extension for plain text file
   *
   * @return PlainTextFile object
   */
  @Override
  public PlainTextFile setExtension(final String extension)
  {
    this.extension = extension;

    return this;
  }

  /**
   * Get extension of the plain text file.
   *
   * @return Extension of plain text file
   */
  @Override
  public String getExtension()
  {
    return this.extension;
  }

  /**
   * Get content of plain text file.
   *
   * @return Content of plain text file
   */
  @Override
  public PlainTextContent getContent()
  {
    return this.content;
  }

  /**
   * Compare current plain text file to another object of the
   * same type. Equality comparison is based on the path, file
   * name and extension as well as the file's content.
   *
   * @param object PlainTextFile object for comparison
   *
   * @return True if both source files are equal, false otherwise
   */
  @Override
  public boolean equals(Object object)
  {
    // Other object is null
    if (null == object)
    {
      return false;
    }

    // Catch self-comparison
    if (this == object)
    {
      return true;
    }

    // Objects are of the same class
    if (this.getClass() == object.getClass())
    {
      // Safe cast to desired type
      PlainTextFileImpl otherTextFile = (PlainTextFileImpl)object;

      if (this.path.equals(otherTextFile.getPath()) &&
          this.fileName.equals(otherTextFile.getFileName()) &&
          this.extension.equals(otherTextFile.getExtension()) &&
          this.content.equals(otherTextFile.getContent()))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Generate hash code for object comparison based on
   * some attributes of the current class.
   *
   * @return Hash code for current object
   */
  @Override
  public int hashCode()
  {
    int hash = 3;

    hash = 13 * hash + (this.path != null ? this.path.hashCode() : 0);
    hash = 13 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
    hash = 13 * hash + (this.extension != null ? this.extension.hashCode() : 0);
    hash = 13 * hash + (this.content != null ? this.content.hashCode() : 0);

    return hash;
  }

  /**
   * Create a printable form of the plain text file.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    // Print file content
    if (!this.content.getCode().isEmpty())
    {
      this.content.toString(buffer, tabCount);
    }
  }
}
