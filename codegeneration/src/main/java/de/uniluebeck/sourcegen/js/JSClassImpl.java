/** 17.03.2013 01:45 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;
import java.util.LinkedList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * This class represents class-like elements in JavaScript source
 * code. A JavaScript class consists of a header comment, a name,
 * a list of class arguments as well as a set of fields and a set
 * of member functions (i.e. methods).
 *
 * Officially, the JavaScript specification (ECMA-262 Edition 5.1)
 * does not contain classes, but it is possible to define objects
 * with a special function-syntax in JavaScript.
 *
 * For example, a class 'Apple' can be written as:
 *
 *   function Apple(price)
 *   {
 *     this.color = "red";
 *     this.taste = "sweet";
 *     this.price = price;
 *
 *     this.prototype.setPrice = function (price) {
 *       this.price = price;
 *     };
 *   }
 *
 * @author seidel
 */
public class JSClassImpl extends JSComplexTypeImpl implements JSClass
{
  /** Name of the JavaScript class */
  private String name;

  /** Name of parent class for inheritance */
  private String parent;

  /** List of class arguments */
  private ArrayList<String> arguments;

  /** List of class attributes (i.e. fields) */
  private LinkedList<JSField> fields;

  /** List of member functions (i.e. methods) */
  private LinkedList<JSMethod> methods;

  /** Header comment for JavaScript class */
  private JSComment comment;

  /**
   * Parameterized constructor creates a new JavaScript class
   * with the given name, no parent class and empty argument
   * list.
   *
   * To add further content to the class, use one of the add()
   * methods or the corresponding setter.
   *
   * @param name Name of the JavaScript class
   */
  public JSClassImpl(final String name)
  {
    this.name = name;
    this.parent = null;
    this.arguments = new ArrayList<String>();
    this.fields = new LinkedList<JSField>();
    this.methods = new LinkedList<JSMethod>();
    this.comment = null;
  }

  /**
   * Parameterized constructor creates a new JavaScript class
   * with the given name and argument list. A parent class is
   * not set.
   *
   * To add further content to the class, use one of the add()
   * methods or the corresponding setter.
   *
   * @param name Name of the JavaScript class
   * @param arguments List of class arguments
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  public JSClassImpl(final String name, final String... arguments) throws JSDuplicateException
  {
    this(name);

    // Add arguments
    for (String argument: arguments)
    {
      this.addArgument(argument);
    }
  }

  /**
   * Set name of the JavaScript class.
   *
   * @param name Name of the class
   *
   * @return JSClass object
   */
  @Override
  public JSClass setName(final String name)
  {
    this.name = name;

    return this;
  }

  /**
   * Get name of the JavaScript class.
   *
   * @return Name of the class
   */
  @Override
  public String getName()
  {
    return this.name;
  }

  /**
   * Set name of the parent class for inheritance.
   *
   * @param parent Name of parent class
   *
   * @return JSClass object
   */
  @Override
  public JSClass setParent(final String parent)
  {
    this.parent = parent;

    return this;
  }

  /**
   * Get name of the parent class for inheritance. Return
   * value will be 'null' if no parent class was set.
   *
   * @return Name of parent class or 'null'
   */
  @Override
  public String getParent()
  {
    return this.parent;
  }

  /**
   * Set list of class arguments. Calling this method will
   * erase all parameters that have been set before! If you
   * simply want to add new arguments without clearing prior
   * ones, use the add() method.
   *
   * @param arguments List of class arguments
   *
   * @return JSClass object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSClass setArguments(final String... arguments) throws JSDuplicateException
  {
    this.arguments.clear();

    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Get list of class arguments.
   *
   * @return List of class arguments
   */
  @Override
  public ArrayList<String> getArguments()
  {
    return this.arguments;
  }

  /**
   * Add a new class parameter to the internal list. This
   * method checks, if an argument already exists and throws
   * an exception on duplication.
   *
   * @param argument Class argument to add
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  private void addArgument(final String argument) throws JSDuplicateException
  {
    // Check if argument exists already
    if (this.arguments.contains(argument))
    {
      throw new JSDuplicateException(String.format(
              "Duplicate argument '%s' for class '%s'.", argument, this.name));
    }
    else
    {
      this.arguments.add(argument);
    }
  }

  /**
   * Add further parameters to the list of existing
   * class arguments.
   *
   * @param arguments List of class arguments
   *
   * @return JSClass object
   *
   * @throws JSDuplicateException Duplicate argument detected
   */
  @Override
  public JSClass add(final String... arguments) throws JSDuplicateException
  {
    for (String argument: arguments)
    {
      this.addArgument(argument);
    }

    return this;
  }

  /**
   * Add further member variables (i.e. fields) to the class.
   *
   * @param fields List of member variables
   *
   * @return JSClass object
   *
   * @throws JSDuplicateException Duplicate field detected
   */
  @Override
  public JSClass add(final JSField... fields) throws JSDuplicateException
  {
    for (JSField field: fields)
    {
      if (this.fields.contains(field))
      {
        throw new JSDuplicateException(String.format(
                "Duplicate field '%s' for class '%s'.", field.getName(), this.name));
      }
      else
      {
        this.fields.add(field);
      }
    }

    return this;
  }

  /**
   * Add further member function (i.e. method) to the class.
   *
   * @param methods List of member functions
   *
   * @return JSClass object
   *
   * @throws JSDuplicateException Duplicate method detected
   */
  @Override
  public JSClass add(final JSMethod... methods) throws JSDuplicateException
  {
    for (JSMethod method: methods)
    {
      if (this.methods.contains(method))
      {
        throw new JSDuplicateException(String.format(
                "Duplicate method '%s' for class '%s'.", method.getName(), this.name));
      }
      else
      {
        this.methods.add(method);
      }
    }

    return this;
  }

  /**
   * Get list with all member variables (i.e. fields) of the class.
   *
   * @return List of member variables
   */
  @Override
  public LinkedList<JSField> getFields()
  {
    return this.fields;
  }

  /**
   * Get list with all member functions (i.e. methods) of the class.
   *
   * @return List of member functions
   */
  @Override
  public LinkedList<JSMethod> getMethods()
  {
    return this.methods;
  }

  /**
   * Check if a JSField object already exists in the class.
   *
   * @param field JSField object to check
   *
   * @return True if object is already contained, false otherwise
   */
  @Override
  public boolean contains(final JSField field)
  {
    return this.fields.contains(field);
  }

  /**
   * Check if a JSMethod object already exists in the class.
   *
   * @param method JSMethod object to check
   *
   * @return True if object is already contained, false otherwise
   */
  @Override
  public boolean contains(final JSMethod method)
  {
    return this.methods.contains(method);
  }

  /**
   * Set comment for the JavaScript class. The comment
   * will be printed above of the class header.
   *
   * @param comment Comment for JavaScript class
   *
   * @return JSClass object
   */
  @Override
  public JSClass setComment(final JSComment comment)
  {
    this.comment = comment;

    return this;
  }

  /**
   * Set comment for the JavaScript class. The comment
   * will be printed above of the class header.
   *
   * @param comment Comment for JavaScript class
   *
   * @return JSClass object
   */
  @Override
  public JSClass setComment(final String comment)
  {
    return this.setComment(new JSCommentImpl(comment));
  }

  /**
   * Compare current JavaScript class with another object of
   * the same type. Equality comparison is based on the class
   * name, its parent class and its argument list as well as
   * its member fields and methods.
   *
   * @param object JSClass object for comparison
   *
   * @return True if both classes are equal, false otherwise
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
      JSClassImpl otherClass = (JSClassImpl)object;

      // Parent name may be 'null'
      boolean equalParents =
              (null == this.parent && null == otherClass.getParent()) ||
              (null != this.parent && null != otherClass.getParent() && this.parent.equals(otherClass.getParent()));

      if (this.name.equals(otherClass.getName()) &&
          equalParents &&
          this.arguments.size() == otherClass.getArguments().size() && // Early exit for better performance
          this.arguments.equals(otherClass.getArguments()) &&
          this.fields.size() == otherClass.getFields().size() && // Early exit for better performance
          this.fields.equals(otherClass.getFields()) &&
          this.methods.size() == otherClass.getMethods().size() && // Early exit for better performance
          this.methods.equals(otherClass.getMethods()))
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

    hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 67 * hash + (this.parent != null ? this.parent.hashCode() : 0);
    hash = 67 * hash + (this.arguments != null ? this.arguments.hashCode() : 0);
    hash = 67 * hash + (this.fields != null ? this.fields.hashCode() : 0);
    hash = 67 * hash + (this.methods != null ? this.methods.hashCode() : 0);

    return hash;
  }

  /**
   * Print JavaScript class with its header comment, name,
   * argument list, member variables (i.e. fields) and
   * member functions (i.e. methods).
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

    // Enherit from parent class if necessary
    if (null != this.parent)
    {
      buffer.append(String.format("%s.prototype = new %s();", this.name, this.parent));
      buffer.append("\n\n");
    }

    // Output class header
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

    // Print all fields
    for (JSField field: this.fields)
    {
      field.toString(buffer, tabCount + 1, true);

      if (field != this.fields.getLast())
      {
        buffer.append("\n\n");
      }
      else
      {
        buffer.append("\n");
      }
    }

    // Separate fields and methods
    if (!this.fields.isEmpty() && !this.methods.isEmpty())
    {
      buffer.append("\n");
    }

    // Print all methods
    for (JSMethod method: this.methods)
    {
      method.toString(buffer, tabCount + 1);

      if (method != this.methods.getLast())
      {
        buffer.append("\n\n");
      }
      else
      {
        buffer.append("\n");
      }
    }

    buffer.append("}");
  }
}
