/** 17.03.2013 03:20 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * This class represents a function in JavaScript source code.
 * A function consists of a header comment, a name, a list of
 * function arguments and a body with the actual source code.
 *
 * JSFunction objects are used in the global scope of a source
 * file. If you want to define a member function (i.e. a method
 * within a class), use JSMethod instead.
 *
 * @author seidel
 */
public class JSFunctionImpl extends JSComplexTypeImpl implements JSFunction
{
  /** Name of the function */
  private String name;

  /** List of function arguments */
  private ArrayList<String> arguments;

  /** Function body with actual source code */
  private JSFunctionBody body;

  /** Header comment of function */
  private JSComment comment;

  /**
   * Parameterized constructor will create a new function
   * with the given name and list of arguments.
   *
   * @param name Name of the function
   * @param arguments List of function arguments
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  public JSFunctionImpl(final String name, final String... arguments) throws JSDuplicateException
  {
    this.name = name;
    this.body = JSFunctionBody.factory.create();
    this.comment = null;

    this.arguments = new ArrayList<String>();
    for (String argument: arguments)
    {
      this.addArgument(argument);
    }
  }

  /**
   * Set name of the JavaScript function.
   *
   * @param name Name of the function
   *
   * @return JSFunction object
   */
  @Override
  public JSFunction setName(final String name)
  {
    this.name = name;

    return this;
  }

  /**
   * Get name of the JavaScript function.
   *
   * @return Name of the function
   */
  @Override
  public String getName()
  {
    return this.name;
  }

  /**
   * Set list of function arguments. Calling this method will
   * erase all parameters that have been set before! If you
   * simply want to add new arguments without clearing prior
   * ones, use the add() method.
   *
   * @param arguments List of function arguments
   *
   * @return JSFunction object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSFunction setArguments(final String... arguments) throws JSDuplicateException
  {
    this.arguments.clear();

    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Get list of function arguments.
   *
   * @return List of function arguments
   */
  @Override
  public ArrayList<String> getArguments()
  {
    return this.arguments;
  }

  /**
   * Add a new function parameter to the internal list. This
   * method checks, if an argument already exists and throws
   * an exception on duplication.
   *
   * @param argument Function argument to add
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  private void addArgument(final String argument) throws JSDuplicateException
  {
    // Check if argument exists already
    if (this.arguments.contains(argument))
    {
      throw new JSDuplicateException(String.format(
              "Duplicate argument '%s' for function '%s'.", argument, this.name));
    }
    else
    {
      this.arguments.add(argument);
    }
  }

  /**
   * Get body of function that contains the actual
   * JavaScript source code.
   *
   * @return Body of JavaScript function
   */
  @Override
  public JSFunctionBody getBody()
  {
    return this.body;
  }

  /**
   * Set comment for the JavaScript function. The comment
   * will be printed above of the function header.
   *
   * @param comment Comment for JavaScript function
   *
   * @return JSFunction object
   */
  @Override
  public JSFunction setComment(final JSComment comment)
  {
    this.comment = comment;

    return this;
  }

  /**
   * Set comment for the JavaScript function. The comment
   * will be printed above of the function header.
   *
   * @param comment Comment for JavaScript function
   *
   * @return JSFunction object
   */
  @Override
  public JSFunction setComment(final String comment)
  {
    return this.setComment(new JSCommentImpl(comment));
  }

  /**
   * Add further parameters to the list of existing
   * function arguments.
   *
   * @param arguments List of function arguments
   *
   * @return JSFunction object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSFunction add(final String... arguments) throws JSDuplicateException
  {
    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Compare current JavaScript function with another object of
   * the same type. Equality comparison is based on the method
   * name and its argument list only. Function body and comment
   * are NOT part of the comparison.
   *
   * @param object JSFunction object for comparison
   *
   * @return True if both functions are equal, false otherwise
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
      JSFunctionImpl otherFunction = (JSFunctionImpl)object;

      if (this.name.equals(otherFunction.getName()) &&
          this.arguments.size() == otherFunction.getArguments().size() && // Early exit for better performance
          this.arguments.equals(otherFunction.getArguments()))
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

    hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 53 * hash + (this.arguments != null ? this.arguments.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript function with its header comment,
   * name, argument list and source code body.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    // Write comment if necessary
    if (null != this.comment && !this.comment.isEmpty())
    {
      this.comment.toString(buffer, tabCount);
    }

    // Indent code
    this.indent(buffer, tabCount);

    // Output function header    
    buffer.append("function ").append(this.name);

    // Prepare argument list
    String signature = "";
    for (int i = 0; i < this.arguments.size(); ++i)
    {
      signature += this.arguments.get(i);

      if (i < this.arguments.size() - 1)
      {
        signature += ", ";
      }
    }

    // Append arguments (if any)
    buffer.append("(").append(signature).append(")\n");
    this.indent(buffer, tabCount);
    buffer.append("{\n");

    // Append function body
    JSFunctionBodyImpl functionBody = (JSFunctionBodyImpl)this.getBody();
    functionBody.toString(buffer, tabCount + 1);
    buffer.append("\n");

    this.indent(buffer, tabCount);
    buffer.append("}");
  }
}
