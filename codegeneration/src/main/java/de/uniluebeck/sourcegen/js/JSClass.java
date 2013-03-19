/** 17.03.2013 01:42 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;
import java.util.LinkedList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Interface for a class-like element in JavaScript source code.
 * This file defines all method signatures for JSClassImpl and
 * contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface JSClass extends JSComplexType
{
  /*****************************************************************
   * JSClassFactory inner class
   *****************************************************************/

  public static final class JSClassFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSClassFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSClassFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSClassFactory object
     */
    public static synchronized JSClassFactory getInstance()
    {
      if (null == JSClassFactory.instance)
      {
        JSClassFactory.instance = new JSClassFactory();
      }

      return JSClassFactory.instance;
    }

    /**
     * Create a new JSClass object with the given class name.
     *
     * @param name Name of the JavaScript class
     *
     * @return JSClassImpl object
     */
    public JSClassImpl create(final String name)
    {
      return new JSClassImpl(name);
    }

    /**
     * Create a new JSClass object with the given class name
     * and argument.
     *
     * @param name Name of the JavaScript class
     * @param arguments List of class arguments
     *
     * @return JSClassImpl object
     *
     * @throws JSDuplicateException Duplicate argument detected
     */
    public JSClassImpl create(final String name, final String... arguments) throws JSDuplicateException
    {
      return new JSClassImpl(name, arguments);
    }
  }

  /*****************************************************************
   * JSClass outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSClassFactory factory = JSClassFactory.getInstance();

  public JSClass setName(final String name);
  @Override
  public String getName();

  public JSClass setParent(final String parent);
  public String getParent();

  public JSClass setArguments(final String... arguments) throws JSDuplicateException;
  public ArrayList<String> getArguments();

  public JSClass add(final String... arguments) throws JSDuplicateException;
  public JSClass add(final JSField... fields) throws JSDuplicateException;
  public JSClass add(final JSMethod... methods) throws JSDuplicateException;

  public LinkedList<JSField> getFields();
  public LinkedList<JSMethod> getMethods();

  public boolean contains(final JSField field);
  public boolean contains(final JSMethod method);

  public JSClass setComment(final JSComment comment);
  public JSClass setComment(final String comment);
}
