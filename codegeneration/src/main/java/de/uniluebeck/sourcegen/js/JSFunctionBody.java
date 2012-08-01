/** 30.07.2012 22:29 */
package de.uniluebeck.sourcegen.js;

/**
 * Interface for the body of a JavaScript function. This file
 * defines all method signatures for JSFunctionBodyImpl and
 * contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface JSFunctionBody extends JSElement
{
  /*****************************************************************
   * JSFunctionBodyFactory inner class
   *****************************************************************/

  public static final class JSFunctionBodyFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSFunctionBodyFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSFunctionBodyFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSFunctionBodyFactory object
     */
    public static synchronized JSFunctionBodyFactory getInstance()
    {
      if (null == JSFunctionBodyFactory.instance)
      {
        JSFunctionBodyFactory.instance = new JSFunctionBodyFactory();
      }

      return JSFunctionBodyFactory.instance;
    }

    /**
     * Create a new JSFunctionBody object with empty code.
     *
     * @return JSFunctionBodyImpl object
     */
    public JSFunctionBodyImpl create()
    {
      return new JSFunctionBodyImpl();
    }

    /**
     * Create a new JSFunctionBody object with given lines of
     * JavaScript source code.
     *
     * @param codeLines Lines of JavaScript source code
     *
     * @return JSFunctionBodyImpl object
     */
    public JSFunctionBodyImpl create(final String... codeLines)
    {
      return new JSFunctionBodyImpl(codeLines);
    }
  }

  /*****************************************************************
   * JSFunctionBody outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSFunctionBodyFactory factory = JSFunctionBodyFactory.getInstance();

  public JSFunctionBody setCode(final String code);
  public String getCode();

  public JSFunctionBody prependCode(final String... codeLines);
  public JSFunctionBody appendCode(final String... codeLines);

  public JSFunctionBody clearCode();
}
