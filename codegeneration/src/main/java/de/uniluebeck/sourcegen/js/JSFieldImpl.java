/** 23.10.2012 18:34 */
package de.uniluebeck.sourcegen.js;

/**
 * This class represents a field in a JavaScript class.
 * A field consists of a name and an initial value.
 *
 * @author seidel
 */
public class JSFieldImpl extends JSElementImpl implements JSField
{
  /** Name of the field */
  private String name;

  /** Initial value of the field */
  private String initValue;

  /** Comment for the field */
  private JSComment comment;

  /**
   * Parameterized constructor creates a new JavaScript
   * field with the given name and initial value.
   *
   * @param name Name of the JavaScript field
   * @param initValue Initial value of the field
   */
  public JSFieldImpl(final String name, final String initValue)
  {
    this.name = name;
    this.initValue = initValue;
    this.comment = null;
  }

  /**
   * Set name of the JavaScript field.
   *
   * @param name Name of the field
   *
   * @return JSField object
   */
  @Override
  public JSField setName(final String name)
  {
    this.name = name;

    return this;
  }

  /**
   * Get name of the JavaScript field.
   *
   * @return Name of the field
   */
  @Override
  public String getName()
  {
    return this.name;
  }

  /**
   * Set initial value of the JavaScript field.
   *
   * @param initValue Initial value of the field
   *
   * @return JSField object
   */
  @Override
  public JSField setInitValue(final String initValue)
  {
    this.initValue = initValue;

    return this;
  }

  /**
   * Get initial value of the JavaScript field.
   *
   * @return Initial value of the field
   */
  @Override
  public String getInitValue()
  {
    return this.initValue;
  }

  /**
   * Set comment for the JavaScript field.
   *
   * @param comment Comment for JavaScript field
   *
   * @return JSField object
   */
  @Override
  public JSField setComment(final JSComment comment)
  {
    this.comment = comment;

    return this;
  }

  /**
   * Compare current JavaScript field with another object
   * of the same type. Equality comparison is based on the
   * field's name and its initial value.
   *
   * @param object JSField object for comparison
   *
   * @return True if both fields are equal, false otherwise
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
      JSFieldImpl otherField = (JSFieldImpl)object;

      // Initial values may be 'null'
      boolean equalInitValues =
              (null == this.initValue && null == otherField.getInitValue()) ||
              (null != this.initValue && null != otherField.getInitValue() && this.initValue.equals(otherField.getInitValue()));

      if (this.name.equals(otherField.getName()) &&
          equalInitValues)
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

    hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 53 * hash + (this.initValue != null ? this.initValue.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript field with its name and initial value. This
   * method will print the field in the global context of a source
   * file, that is "var name = initialValue;". If you want to print
   * the field as a class member, use the other toString() method
   * instead.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    this.toString(buffer, tabCount, false);
  }

  /**
   * Print JavaScript field with its name and initial value.
   * This method will print the field in class context, that
   * is "this.name = initialValue;". If you want to print the
   * field in the global context of a source file, use the
   * other toString() method instead.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   * @param inClassContext Flag to print field in class context
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount, boolean inClassContext)
  {
    // Write comment if necessary
    if (null != this.comment)
    {
      this.comment.toString(buffer, tabCount);
    }

    this.indent(buffer, tabCount);

    // Print field with initial value
    if (inClassContext)
    {
      buffer.append(String.format("this.%s = %s;", this.name, (null == this.initValue ? "null" : this.initValue)));
    }
    else
    {
      buffer.append(String.format("var %s = %s;", this.name, (null == this.initValue ? "null" : this.initValue)));
    }
  }
}
