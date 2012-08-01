/** 01.08.2012 22:28 */
package de.uniluebeck.sourcegen.js;

/**
 * Interface for a member variable within a JavaScript class.
 * This file defines all method signatures for JSFieldImpl
 * and contains a factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface JSField extends JSElement
{
  /*****************************************************************
   * JSFieldFactory inner class
   *****************************************************************/

  public static final class JSFieldFactory
  {
    /** Factory instance for Singleton pattern */
    private static JSFieldFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private JSFieldFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return JSFieldFactory object
     */
    public static synchronized JSFieldFactory getInstance()
    {
      if (null == JSFieldFactory.instance)
      {
        JSFieldFactory.instance = new JSFieldFactory();
      }

      return JSFieldFactory.instance;
    }

    /**
     * Create a new JSField object with the given name and
     * initial value.
     *
     * @param name Name of the JavaScript field
     * @param initValue Initial value of the field
     *
     * @return JSFieldImpl object
     */
    public JSFieldImpl create(final String name, final String initValue)
    {
      return new JSFieldImpl(name, initValue);
    }
  }

  /*****************************************************************
   * JSField outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final JSFieldFactory factory = JSFieldFactory.getInstance();

  public JSField setName(final String name);
  public String getName();

  public JSField setInitValue(final String initValue);
  public String getInitValue();

  public JSField setComment(final JSComment comment);
}
