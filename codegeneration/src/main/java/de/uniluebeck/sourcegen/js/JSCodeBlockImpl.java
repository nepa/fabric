/** 02.08.2012 15:07 */
package de.uniluebeck.sourcegen.js;

/**
 * This class represents a common block of JavaScript code
 * in a source file. All lines of code will be written to
 * a StringBuffer and cannot be accessed individually
 * afterwards. However, to output the code with pretty
 * indention, lines are later processed one by one.
 *
 * @author seidel
 */
public class JSCodeBlockImpl extends JSElementImpl implements JSCodeBlock
{
  /** Buffer for code of the code block */
  private StringBuffer code;

  /**
   * Parameterless constructor will create an empty code block.
   */
  public JSCodeBlockImpl()
  {
    this.code = new StringBuffer();
  }

  /**
   * Parameterized constructor will create a code block
   * with the given lines of JavaScript source code.
   *
   * @param codeLines Lines of JavaScript source code
   */
  public JSCodeBlockImpl(final String... codeLines)
  {
    this.code = new StringBuffer();

    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }
  }

  /**
   * Set JavaScript source code of the code block.
   *
   * @param code JavaScript source code
   *
   * @return JSCodeBlock object
   */
  @Override
  public JSCodeBlock setCode(final String code)
  {
    this.clearCode();
    this.appendCode(code);

    return this;
  }

  /**
   * Get JavaScript source code of the code block.
   *
   * @return JSCodeBlock object
   */
  @Override
  public String getCode()
  {
    return this.code.toString();
  }

  /**
   * Prepend new lines of code at beginning of existing
   * JavaScript source code.
   *
   * @param codeLines Lines of JavaScript source code
   *
   * @return JSCodeBlock object
   */
  @Override
  public JSCodeBlock prependCode(final String... codeLines)
  {
    for (String line: codeLines)
    {
      this.code.insert(0, line + "\n");
    }

    return this;
  }

  /**
   * Append new lines of code at the end of existing
   * JavaScript source code.
   *
   * @param codeLines Lines of JavaScript source code
   *
   * @return JSCodeBlock object
   */
  @Override
  public JSCodeBlock appendCode(final String... codeLines)
  {
    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }

    return this;
  }

  /**
   * Clear buffer that holds JavaScript source code of
   * the code block.
   *
   * @return JSCodeBlock object
   */
  @Override
  public JSCodeBlock clearCode()
  {
    if (this.code.length() > 0)
    {
      this.code.delete(0, this.code.length());
    }

    return this;
  }

  /**
   * Compare current JavaScript code block with another object
   * of the same type. Equality comparison is based on the code
   * in the code block.
   *
   * @param object JSCodeBlock object for comparison
   *
   * @return True if both code blocks are equal, false otherwise
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
      JSCodeBlockImpl otherCodeBlock = (JSCodeBlockImpl)object;

      if (this.getCode().equals(otherCodeBlock.getCode()))
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
    int hash = 5;

    hash = 89 * hash + (this.code != null ? this.code.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript source code of the code block
   * with pretty indention.
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
