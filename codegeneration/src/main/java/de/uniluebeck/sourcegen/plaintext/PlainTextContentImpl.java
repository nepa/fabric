/** 04.11.2012 01:02 */
package de.uniluebeck.sourcegen.plaintext;

/**
 * This class represents the content of a plain text file.
 * All lines of code will be written to a StringBuffer and
 * cannot be accessed individually afterwards. However, to
 * output the code with pretty indention, lines are later
 * processed one by one.
 *
 * @author seidel
 */
public class PlainTextContentImpl extends PlainTextElementImpl implements PlainTextContent
{
  /** Buffer for content of the text file */
  private StringBuffer code;

  /**
   * Parameterless constructor will create an object
   * with empty file content.
   */
  public PlainTextContentImpl()
  {
    this.code = new StringBuffer();
  }

  /**
   * Parameterized constructor will create an object
   * with the given lines of code as its content.
   *
   * @param codeLines Lines of code for initialization
   */
  public PlainTextContentImpl(final String... codeLines)
  {
    this.code = new StringBuffer();

    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }
  }

  /**
   * Set content of the plain text file.
   *
   * @param code Content for plain text file
   *
   * @return PlainTextContent object
   */
  @Override
  public PlainTextContent setCode(final String code)
  {
    this.clearCode();
    this.appendCode(code);

    return this;
  }

  /**
   * Get content of the plain text file.
   *
   * @return Content of plain text file
   */
  @Override
  public String getCode()
  {
    return this.code.toString();
  }

  /**
   * Prepend new lines at beginning of existing file content.
   *
   * @param codeLines Lines of code
   *
   * @return PlainTextContent object
   */
  @Override
  public PlainTextContent prependCode(final String... codeLines)
  {
    for (String line: codeLines)
    {
      this.code.insert(0, line + "\n");
    }

    return this;
  }

  /**
   * Append new lines at the end of existing file content.
   *
   * @param codeLines Lines of code
   *
   * @return PlainTextContent object
   */
  @Override
  public PlainTextContent appendCode(final String... codeLines)
  {
    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }

    return this;
  }

  /**
   * Clear buffer that holds content of the plain text file.
   *
   * @return PlainTextContent object
   */
  @Override
  public PlainTextContent clearCode()
  {
    if (this.code.length() > 0)
    {
      this.code.delete(0, this.code.length());
    }

    return this;
  }

  /**
   * Compare current plain text file content with another
   * object of the same type. Equality comparison is based
   * on the plain text content.
   *
   * @param object PlainTextContent object for comparison
   *
   * @return True if both function bodies are equal, false
   * otherwise
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
      PlainTextContentImpl otherTextContent = (PlainTextContentImpl)object;

      if (this.getCode().equals(otherTextContent.getCode()))
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
    int hash = 7;

    hash = 41 * hash + (this.code != null ? this.code.hashCode() : 0);

    return hash;
  }

  /**
   * Print content of plain text file with pretty indention.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    int startOfLine = 0;
    int endOfLine = 0;
    int codeLength = this.code.length();

    // Process entire source code
    while (startOfLine < codeLength)
    {
      // Find end of current line
      endOfLine = this.code.indexOf("\n", startOfLine);
      endOfLine = (endOfLine == -1) ? codeLength : endOfLine;

      // Indent line
      this.indent(buffer, tabCount);

      // Copy current line to output buffer
      buffer.append(this.code, startOfLine, endOfLine);
      buffer.append(endOfLine != codeLength - 1 ? "\n" : ""); // No break after last line

      // Start of next line
      startOfLine = endOfLine + 1;
    }
  }
}
