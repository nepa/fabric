/** 01.08.2012 22:57 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Interface for a JavaScript member function (i.e. a method in a
 * class). This file defines all method signatures for JSMethodImpl
 * and contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface JSMethod extends JSComplexType
{
  /*****************************************************************
   * JSMethodFactory inner class
   *****************************************************************/

  public static final class JSMethodFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSMethodFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSMethodFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSMethodFactory object
     */
    public static synchronized JSMethodFactory getInstance()
    {
      if (null == JSMethodFactory.instance)
      {
        JSMethodFactory.instance = new JSMethodFactory();
      }

      return JSMethodFactory.instance;
    }

    /**
     * Create a new JSMethod object with the given name and
     * arguments.
     *
     * @param name Name of the JavaScript method
     * @param arguments List of method arguments
     *
     * @return JSMethodImpl object
     *
     * @throws JSDuplicateException Duplicate argument detected
     */
    public JSMethodImpl create(final String name, final String... arguments) throws JSDuplicateException
    {
      return new JSMethodImpl(name, arguments);
    }

    /**
     * Create a new JSMethod object with the given name and
     * arguments. The second parameter can be used to define
     * the method as a prototype function. The name will be
     * prefixed with the 'prototype' keyword on output then.
     *
     * @param name Name of the JavaScript method
     * @param isPrototypeFunction Flag to define the method
     * as a prototype function
     * @param arguments List of method arguments
     *
     * @return JSMethodImpl object
     *
     * @throws JSDuplicateException Duplicate argument detected
     */
    public JSMethodImpl create(final String name, final boolean isPrototypeFunction, final String... arguments) throws JSDuplicateException
    {
      return new JSMethodImpl(name, isPrototypeFunction, arguments);
    }
  }

  /*****************************************************************
   * JSMethod outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSMethodFactory factory = JSMethodFactory.getInstance();

  public JSMethod setIsPrototypeFunction(final boolean isPrototypeFunction);
  public boolean getIsPrototypeFunction();
  public boolean isPrototypeFunction();

  public JSMethod setName(final String name);
  @Override
  public String getName();

  public JSMethod setArguments(final String... arguments) throws JSDuplicateException;
  public ArrayList<String> getArguments();

  public JSFunctionBody getBody();

  public JSMethod setComment(final JSComment comment);

  public JSMethod add(final String... arguments) throws JSDuplicateException;
}
