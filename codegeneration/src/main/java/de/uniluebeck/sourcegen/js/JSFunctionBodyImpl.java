/** 04.11.2012 00:55 */
package de.uniluebeck.sourcegen.js;

/**
 * This class represents the body of a JavaScript function.
 * All lines of code will be written to a StringBuffer and
 * cannot be accessed individually afterwards. However, to
 * output the code with pretty indention, lines are later
 * processed one by one.
 *
 * @author seidel
 */
public class JSFunctionBodyImpl extends JSElementImpl implements JSFunctionBody
{
  /** Buffer for code of the function body */
  private StringBuffer code;

  /**
   * Parameterless constructor will create an empty function body.
   */
  public JSFunctionBodyImpl()
  {
    this.code = new StringBuffer();
  }

  /**
   * Parameterized constructor will create a function body
   * with the given lines of JavaScript source code.
   *
   * @param codeLines Lines of JavaScript source code
   */
  public JSFunctionBodyImpl(final String... codeLines)
  {
    this.code = new StringBuffer();

    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }
  }

  /**
   * Set JavaScript source code of the function body.
   *
   * @param code JavaScript source code
   *
   * @return JSFunctionBody object
   */
  @Override
  public JSFunctionBody setCode(final String code)
  {
    this.clearCode();
    this.appendCode(code);
    
    return this;
  }

  /**
   * Get JavaScript source code of the function body.
   *
   * @return JavaScript source code
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
   * @return JSFunctionBody object
   */
  @Override
  public JSFunctionBody prependCode(final String... codeLines)
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
   * @return JSFunctionBody object
   */
  @Override
  public JSFunctionBody appendCode(final String... codeLines)
  {
    for (String line: codeLines)
    {
      this.code.append(line).append("\n");
    }

    return this;
  }

  /**
   * Clear buffer that holds JavaScript source code of
   * the function body.
   *
   * @return JSFunctionBody object
   */
  @Override
  public JSFunctionBody clearCode()
  {
    if (this.code.length() > 0)
    {
      this.code.delete(0, this.code.length());
    }

    return this;
  }

  /**
   * Compare current JavaScript function body with another
   * object of the same type. Equality comparison is based
   * on the code in the function body.
   *
   * @param object JSFunctionBody object for comparison
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
      JSFunctionBodyImpl otherFunctionBody = (JSFunctionBodyImpl)object;

      if (this.getCode().equals(otherFunctionBody.getCode()))
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

    hash = 67 * hash + (this.code != null ? this.code.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript source code of the function body
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
