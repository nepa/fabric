/** 17.03.2013 01:51 */
package de.uniluebeck.sourcegen.js;

import java.util.ArrayList;

import de.uniluebeck.sourcegen.exceptions.JSDuplicateException;

/**
 * Interface for a JavaScript function. This file defines all
 * method signatures for JSFunctionImpl and contains a factory
 * mechanism to create such objects.
 *
 * @author seidel
 */
public interface JSFunction extends JSComplexType
{
  /*****************************************************************
   * JSFunctionFactory inner class
   *****************************************************************/

  public static final class JSFunctionFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSFunctionFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSFunctionFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSFunctionFactory object
     */
    public static synchronized JSFunctionFactory getInstance()
    {
      if (null == JSFunctionFactory.instance)
      {
        JSFunctionFactory.instance = new JSFunctionFactory();
      }

      return JSFunctionFactory.instance;
    }

    /**
     * Create a new JSFunction object with the given name and
     * arguments.
     *
     * @param name Name of the JavaScript function
     * @param arguments List of function arguments
     *
     * @return JSFunctionImpl object
     *
     * @throws JSDuplicateException Duplicate argument detected
     */
    public JSFunctionImpl create(final String name, final String... arguments) throws JSDuplicateException
    {
      return new JSFunctionImpl(name, arguments);
    }
  }

  /*****************************************************************
   * JSFunction outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSFunctionFactory factory = JSFunctionFactory.getInstance();

  public JSFunction setName(final String name);
  @Override
  public String getName();

  public JSFunction setArguments(final String... arguments) throws JSDuplicateException;
  public ArrayList<String> getArguments();

  public JSFunctionBody getBody();

  public JSFunction setComment(final JSComment comment);
  public JSFunction setComment(final String comment);

  public JSFunction add(final String... arguments) throws JSDuplicateException;
}
