/** 02.08.2012 14:56 */
package de.uniluebeck.sourcegen.js;

/**
 * Interface for a common block of JavaScript code in a
 * source file. This file defines all method signatures
 * for JSCodeBlockImpl and contains a factory mechanism
 * to create such objects.
 *
 * @author seidel
 */
public interface JSCodeBlock extends JSElement
{
  /*****************************************************************
   * JSCodeBlockFactory inner class
   *****************************************************************/

  public static final class JSCodeBlockFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSCodeBlockFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSCodeBlockFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSCodeBlockFactory object
     */
    public static synchronized JSCodeBlockFactory getInstance()
    {
      if (null == JSCodeBlockFactory.instance)
      {
        JSCodeBlockFactory.instance = new JSCodeBlockFactory();
      }

      return JSCodeBlockFactory.instance;
    }

    /**
     * Create a new JSCodeBlock object with empty code.
     *
     * @return JSCodeBlockImpl object
     */
    public JSCodeBlockImpl create()
    {
      return new JSCodeBlockImpl();
    }

    /**
     * Create a new JSCodeBlock object with given lines of
     * JavaScript source code.
     *
     * @param codeLines Lines of JavaScript source code
     *
     * @return JSCodeBlockImpl object
     */
    public JSCodeBlockImpl create(final String... codeLines)
    {
      return new JSCodeBlockImpl(codeLines);
    }
  }

  /*****************************************************************
   * JSCodeBlock outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSCodeBlockFactory factory = JSCodeBlockFactory.getInstance();

  public JSCodeBlock setCode(final String code);
  public String getCode();

  public JSCodeBlock prependCode(final String... codeLines);
  public JSCodeBlock appendCode(final String... codeLines);

  public JSCodeBlock clearCode();
}
