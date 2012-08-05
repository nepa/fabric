/** 03.08.2012 01:14 */
package de.uniluebeck.sourcegen.js;

import java.util.LinkedList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Implementation of the JSSourceFile interface. Objects
 * of this class represent virtual JavaScript source
 * files in Fabric's workspace.
 *
 * @author seidel
 */
public class JSSourceFileImpl extends JSComplexTypeImpl implements JSSourceFile
{
  /** Name of the JavaScript source file */
  private String fileName;

  /** Code block before fields */
  private JSCodeBlock codeBeforeFields;

  /** List of variables in the global scope */
  private LinkedList<JSField> fields;

  /** Code block before classes */
  private JSCodeBlock codeBeforeClasses;

  /** List of classes in the source file */
  private LinkedList<JSClass> classes;

  /** Code block before functions */
  private JSCodeBlock codeBeforeFunctions;

  /** List of global functions in the source file */
  private LinkedList<JSFunction> functions;

  /** Code block after functions */
  private JSCodeBlock codeAfterFuntions;

  /** Comment to print at beginning of file */
  private JSComment comment;

  /**
   * Parameterized constructor creates new JavaScript
   * source file with the given file name.
   *
   * @param fileName Desired name for source file
   */
  public JSSourceFileImpl(final String fileName)
  {
    this.fileName = fileName;
    this.codeBeforeFields = JSCodeBlock.factory.create();
    this.fields = new LinkedList<JSField>();
    this.codeBeforeClasses = JSCodeBlock.factory.create();
    this.classes = new LinkedList<JSClass>();
    this.codeBeforeFunctions = JSCodeBlock.factory.create();
    this.functions = new LinkedList<JSFunction>();
    this.codeAfterFuntions = JSCodeBlock.factory.create();
    this.comment = null;
  }

  /**
   * Set name of the JavaScript source file.
   *
   * @param fileName Desired name for source file
   *
   * @return JSSourceFile object
   */
  @Override
  public JSSourceFile setFileName(final String fileName)
  {
    this.fileName = fileName;

    return this;
  }

  /**
   * Get name of the JavaScript source file.
   *
   * @return Name of source file
   */
  @Override
  public String getName()
  {
    return this.fileName;
  }

  /**
   * Get name of the JavaScript source file.
   *
   * @return Name of source file
   */
  @Override
  public String getFileName()
  {
    return this.getName();
  }

  /**
   * Add new variables to global scope of the source file.
   *
   * @param fields List of global variables
   *
   * @return JSSourceFile object
   *
   * @throws JSDuplicateException Duplicate variable detected
   */
  @Override
  public JSSourceFile add(final JSField... fields) throws JSDuplicateException
  {
    for (JSField field: fields)
    {
      if (this.fields.contains(field))
      {
        throw new JSDuplicateException(String.format(
                "Duplicate global variable '%s' in source file '%s'.", field.getName(), this.fileName));
      }
      else
      {
        this.fields.add(field);
      }
    }

    return this;
  }

  /**
   * Add new classes to the source file.
   *
   * @param classes List of classes
   *
   * @return JSSourceFile object
   *
   * @throws JSDuplicateException Duplicate variable detected
   */
  @Override
  public JSSourceFile add(final JSClass... classes) throws JSDuplicateException
  {
    for (JSClass jsc: classes)
    {
      if (this.classes.contains(jsc))
      {
        throw new JSDuplicateException(String.format(
                "Duplicate class '%s' in source file '%s'.", jsc.getName(), this.fileName));
      }
      else
      {
        this.classes.add(jsc);
      }
    }

    return this;
  }

  /**
   * Add new function to global scope of the source file.
   *
   * @param functions List of global functions
   *
   * @return JSSourceFile object
   *
   * @throws JSDuplicateException Duplicate variable detected
   */
  @Override
  public JSSourceFile add(final JSFunction... functions) throws JSDuplicateException
  {
    for (JSFunction function: functions)
    {
      if (this.functions.contains(function))
      {
        throw new JSDuplicateException(String.format(
                "Duplicate global function '%s' in source file '%s'.", function.getName(), this.fileName));
      }
      else
      {
        this.functions.add(function);
      }
    }

    return this;
  }

  /**
   * Get list with variables in global scope of the source file.
   *
   * @return List of global variables
   */
  @Override
  public LinkedList<JSField> getFields()
  {
    return this.fields;
  }

  /**
   * Get list of classes that are defined in the source file.
   *
   * @return List of classes
   */
  @Override
  public LinkedList<JSClass> getClasses()
  {
    return this.classes;
  }

  /**
   * Get list of global functions in the source file.
   *
   * @return List of global functions
   */
  @Override
  public LinkedList<JSFunction> getFunctions()
  {
    return this.functions;
  }

  /**
   * Check if a JSField object already exists in the source file.
   *
   * @param field JSField object to check
   *
   * @return True if object is already contained, false otherwise
   */
  @Override
  public boolean contains(final JSField fieldObject)
  {
    return this.fields.contains(fieldObject);
  }

  /**
   * Check if a JSClass object already exists in the source file.
   *
   * @param field JSClass object to check
   *
   * @return True if object is already contained, false otherwise
   */
  @Override
  public boolean contains(final JSClass classObject)
  {
    return this.classes.contains(classObject);
  }

  /**
   * Check if a JSFunction object already exists in the source file.
   *
   * @param field JSFunction object to check
   *
   * @return True if object is already contained, false otherwise
   */
  @Override
  public boolean contains(final JSFunction functionObject)
  {
    return this.functions.contains(functionObject);
  }

  /**
   * Get block of JavaScript code before global variables
   * (i.e. fields) of the source file.
   *
   * @return Code block before fields
   */
  @Override
  public JSCodeBlock getCodeBeforeFields()
  {
    return this.codeBeforeFields;
  }

  /**
   * Get block of JavaScript code after global variables
   * (i.e. fields) of the source file. This method is an
   * alias for getCodeBeforeClasses() and is simply provided
   * for convenience.
   *
   * @return Code block after fields
   */
  @Override
  public JSCodeBlock getCodeAfterFields()
  {
    return this.getCodeBeforeClasses();
  }

  /**
   * Get block of JavaScript code before classes of
   * the source file.
   *
   * @return Code block before classes
   */
  @Override
  public JSCodeBlock getCodeBeforeClasses()
  {
    return this.codeBeforeClasses;
  }

  /**
   * Get block of JavaScript code after classes of
   * the source file. This method is an alias for
   * getCodeBeforeFunctions() and is simply provided
   * for convenience.
   *
   * @return Code block after classes
   */
  @Override
  public JSCodeBlock getCodeAfterClasses()
  {
    return this.getCodeBeforeFunctions();
  }

  /**
   * Get block of JavaScript code before global functions
   * of the source file.
   *
   * @return Code block before functions
   */
  @Override
  public JSCodeBlock getCodeBeforeFunctions()
  {
    return this.codeBeforeFunctions;
  }

  /**
   * Get block of JavaScript code after global functions
   * of the source file.
   *
   * @return Code block after functions
   */
  @Override
  public JSCodeBlock getCodeAfterFunctions()
  {
    return this.codeAfterFuntions;
  }

  /**
   * Set a comment that will be printed right at the
   * beginning of the JavaScript source file.
   *
   * @param comment Comment to be added
   *
   * @return JavaScript source file with comment
   */
  @Override
  public JSSourceFile setComment(final JSComment comment)
  {
    this.comment = comment;

    return this;
  }

  /**
   * Compare current JavaScript source file with another object
   * of the same type. Equality comparison is based on the file
   * name as well as all fields, classes and functions that are
   * contained in the file. Code blocks before and after fields,
   * classes and functions are also used in equality test.
   *
   * @param object JSSourceFile object for comparison
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
      JSSourceFileImpl otherSourceFile = (JSSourceFileImpl)object;

      if (this.fileName.equals(otherSourceFile.getFileName()) &&
          this.codeBeforeFields.equals(otherSourceFile.getCodeBeforeFields()) &&
          this.codeBeforeClasses.equals(otherSourceFile.getCodeBeforeClasses()) &&
          this.codeBeforeFunctions.equals(otherSourceFile.getCodeBeforeFunctions()) &&
          this.codeAfterFuntions.equals(otherSourceFile.getCodeAfterFunctions()) &&
          this.fields.size() == otherSourceFile.getFields().size() && // Early exit for better performance
          this.fields.equals(otherSourceFile.getFields()) &&
          this.classes.size() == otherSourceFile.getClasses().size() && // Early exit for better performance
          this.classes.equals(otherSourceFile.getClasses()) &&
          this.functions.size() == otherSourceFile.getFunctions().size() && // Early exit for better performance
          this.functions.equals(otherSourceFile.getFunctions()))
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

    hash = 23 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
    hash = 23 * hash + (this.codeBeforeFields != null ? this.codeBeforeFields.hashCode() : 0);
    hash = 23 * hash + (this.fields != null ? this.fields.hashCode() : 0);
    hash = 23 * hash + (this.codeBeforeClasses != null ? this.codeBeforeClasses.hashCode() : 0);
    hash = 23 * hash + (this.classes != null ? this.classes.hashCode() : 0);
    hash = 23 * hash + (this.codeBeforeFunctions != null ? this.codeBeforeFunctions.hashCode() : 0);
    hash = 23 * hash + (this.functions != null ? this.functions.hashCode() : 0);
    hash = 23 * hash + (this.codeAfterFuntions != null ? this.codeAfterFuntions.hashCode() : 0);

    return hash;
  }

  /**
   * Create a printable form of the JavaScript source file.
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
      buffer.append("\n");
    }

    // Print code block before fields
    if (!this.codeBeforeFields.getCode().isEmpty())
    {
      this.codeBeforeFields.toString(buffer, tabCount);
      buffer.append("\n\n");
    }

    // Print all global variables
    if (!this.fields.isEmpty())
    {
      this.toString(buffer, tabCount, this.fields, "", "\n\n");
      buffer.append("\n\n");
    }

    // Print code block before classes
    if (!this.codeBeforeClasses.getCode().isEmpty())
    {
      this.codeBeforeClasses.toString(buffer, tabCount);
      buffer.append("\n\n");
    }

    // Print all classes
    if (!this.classes.isEmpty())
    {
      this.toString(buffer, tabCount, this.classes, "", "\n\n");
      buffer.append("\n\n");
    }

    // Print code block before functions
    if (!this.codeBeforeFunctions.getCode().isEmpty())
    {
      this.codeBeforeFunctions.toString(buffer, tabCount);
      buffer.append("\n\n");
    }

    // Print all global functions
    if (!this.functions.isEmpty())
    {
      this.toString(buffer, tabCount, this.functions, "", "\n\n");
      buffer.append("\n\n");
    }

    // Print code block after functions
    if (!this.codeAfterFuntions.getCode().isEmpty())
    {
      this.codeAfterFuntions.toString(buffer, tabCount);
    }

    // Remove trailing line breaks (if any)
    String code = buffer.toString();
    String trimmedCode = code.trim();
    if (buffer.length() > 0 && !code.equals(trimmedCode))
    {
      // Clear buffer and use trimmed code instead
      buffer.delete(0, buffer.length());
      buffer.append(trimmedCode);
    }
  }
}
