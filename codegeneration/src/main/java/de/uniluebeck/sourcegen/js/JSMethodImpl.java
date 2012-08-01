/** 01.08.2012 23:00 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * This class represents a member function (i.e. method) in
 * JavaScript classes. A method consists of a header comment,
 * a name, a list of method arguments and a body with the
 * actual source code. Furthermore, a flag can be set to mark
 * the method as a prototype function.
 *
 * JSMethod objects are used as member functions within classes.
 * If you want to define a function in the global scope of a
 * source file, use JSFunction instead.
 *
 * @author seidel
 */
public class JSMethodImpl extends JSComplexTypeImpl implements JSMethod
{
  /** Flag to mark method as a prototype function */
  private boolean isPrototypeFunction;

  /** Name of the method */
  private String name;

  /** List of method arguments */
  private ArrayList<String> arguments;

  /** Function body with actual source code */
  private JSFunctionBody body;

  /** Header comment of function */
  private JSComment comment;

  /**
   * Parameterized constructor will create a new method
   * with the given name and list of arguments.
   *
   * @param name Name of the method
   * @param arguments List of method arguments
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  public JSMethodImpl(final String name, final String... arguments) throws JSDuplicateException
  {
    this(name, false, arguments);
  }

  /**
   * Parameterized constructor will create a new method
   * with the given name and list of arguments. The second
   * parameter can be used to mark the method as a prototype
   * function. The name will be prefixed with the 'prototype'
   * keyword on output then.
   *
   * @param name Name of the method
   * @param isPrototypeFunction Flag to define the method
   * as a prototype function
   * @param arguments List of method arguments
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  public JSMethodImpl(final String name, final boolean isPrototypeFunction, final String... arguments) throws JSDuplicateException
  {
    this.isPrototypeFunction = isPrototypeFunction;
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
   * Set flag that marks the method as a prototype function.
   *
   * @param isPrototypeFunction Flag to declare method as a
   * prototype function
   *
   * @return JSMethod object
   */
  @Override
  public JSMethod setIsPrototypeFunction(final boolean isPrototypeFunction)
  {
    this.isPrototypeFunction = isPrototypeFunction;

    return this;
  }

  /**
   * Get flag that marks the method as a prototype function.
   *
   * @return True if method is a prototype function, false
   * otherwise
   */
  @Override
  public boolean getIsPrototypeFunction()
  {
    return this.isPrototypeFunction;
  }

  /**
   * This method is an alias for getIsPrototypeFunction()
   * and is solely provided for convenience.
   *
   * @return True if method is a prototype function, false
   * otherwise
   */
  @Override
  public boolean isPrototypeFunction()
  {
    return this.getIsPrototypeFunction();
  }

  /**
   * Set name of the JavaScript member function.
   *
   * @param name Name of the method
   *
   * @return JSMethod object
   */
  @Override
  public JSMethod setName(final String name)
  {
    this.name = name;

    return this;
  }

  /**
   * Get name of the JavaScript member function.
   *
   * @return Name of the method
   */
  @Override
  public String getName()
  {
    return this.name;
  }

  /**
   * Set list of method arguments. Calling this method will
   * erase all parameters that have been set before! If you
   * simply want to add new arguments without clearing prior
   * ones, use the add() method.
   *
   * @param arguments List of method arguments
   *
   * @return JSMethod object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSMethod setArguments(final String... arguments) throws JSDuplicateException
  {
    this.arguments.clear();

    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Get list of method arguments.
   *
   * @return List of method arguments
   */
  @Override
  public ArrayList<String> getArguments()
  {
    return this.arguments;
  }

  /**
   * Add a new method parameter to the internal list. This
   * method checks, if an argument already exists and throws
   * an exception on duplication.
   *
   * @param argument Method argument to add
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  private void addArgument(final String argument) throws JSDuplicateException
  {
    // Check if argument exists already
    if (this.arguments.contains(argument))
    {
      throw new JSDuplicateException(String.format(
              "Duplicate argument '%s' for method '%s'.", argument, this.name));
    }
    else
    {
      this.arguments.add(argument);
    }
  }

  /**
   * Get body of method that contains the actual
   * JavaScript source code.
   *
   * @return Body of JavaScript method
   */
  @Override
  public JSFunctionBody getBody()
  {
    return this.body;
  }

  /**
   * Set comment for the JavaScript method. The comment
   * will be printed above of the method header.
   *
   * @param comment Comment for JavaScript method
   *
   * @return JSMethod object
   */
  @Override
  public JSMethod setComment(final JSComment comment)
  {
    this.comment = comment;

    return this;
  }

  /**
   * Add further parameters to the list of existing
   * method arguments.
   *
   * @param arguments List of method arguments
   *
   * @return JSMethod object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSMethod add(final String... arguments) throws JSDuplicateException
  {
    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Compare current JavaScript method with another object of the
   * same type. Equality comparison is based on the prototype flag,
   * the method name and its argument list only. Function body and
   * comment are NOT part of the comparison.
   *
   * @param object JSMethod object for comparison
   *
   * @return True if both methods are equal, false otherwise
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
      JSMethodImpl otherMethod = (JSMethodImpl)object;
      
      if (this.isPrototypeFunction == otherMethod.isPrototypeFunction() &&
          this.name.equals(otherMethod.getName()) &&
          this.arguments.size() == otherMethod.getArguments().size() && // Early exit for better performance
          this.arguments.equals(otherMethod.getArguments()))
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

    hash = 29 * hash + (this.isPrototypeFunction ? 1 : 0);
    hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 29 * hash + (this.arguments != null ? this.arguments.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript method with its header comment,
   * name, argument list and source code body.
   *
   * @param buffer Buffer for code write-out
   * @param tabCount Depth of code indention
   */
  @Override
  public void toString(StringBuffer buffer, int tabCount)
  {
    // Write comment if necessary
    if (null != this.comment)
    {
      this.comment.toString(buffer, tabCount);
    }

    // Indent code
    this.indent(buffer, tabCount);

    // Output function header
    buffer.append(String.format("this%s.%s = function ",
            (this.isPrototypeFunction ? ".prototype" : ""), this.name));

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
    buffer.append("(").append(signature).append(") {\n");

    // Append function body
    JSFunctionBodyImpl functionBody = (JSFunctionBodyImpl)this.getBody();
    functionBody.toString(buffer, tabCount + 1);
    buffer.append("\n");

    this.indent(buffer, tabCount);
    buffer.append("};");
  }
}
