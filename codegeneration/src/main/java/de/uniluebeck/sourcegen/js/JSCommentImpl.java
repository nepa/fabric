/** 02.08.2012 20:31 */
package de.uniluebeck.sourcegen.js;

/**
 * This class represents a comment within JavaScript
 * source code. Objects of this type can be assigned
 * to various code elements (e.g. functions).
 *
 * @author seidel
 */
public class JSCommentImpl extends JSElementImpl implements JSComment
{
  /** Actual text of the comment */
  private String description;

  /**
   * Parameterized constructor creates a new comment
   * object with the given text.
   *
   * @param description Text of the comment
   */
  public JSCommentImpl(final String description)
  {
    this.description = description;
  }

  /**
   * Set actual text of the comment.
   *
   * @param description Text of the comment
   */
  @Override
  public JSComment setDescription(final String description)
  {
    this.description = description;

    return this;
  }

  /**
   * Get actual text of the comment.
   *
   * @return Text of the comment
   */
  @Override
  public String getDescription()
  {
    return this.description;
  }

  /**
   * Compare current JavaScript comment with another object of
   * the same type. Equality comparison is based on the text
   * of the comment.
   *
   * @param object JSComment object for comparison
   *
   * @return True if both comments are equal, false otherwise
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
      JSCommentImpl otherComment = (JSCommentImpl)object;

      if (this.description.equals(otherComment.getDescription()))
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

    hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);

    return hash;
  }

  /**
   * Print comment for an element of JavaScript code.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    buffer.append("/**\n");
    buffer.append(" * ").append(this.description).append("\n");
    buffer.append(" */\n");
  }
}
