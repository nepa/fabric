/** 03.11.2012 21:48 */
package de.uniluebeck.sourcegen.plaintext;

/**
 * Interface for content of a plain text file. This file defines
 * all method signatures for PlainTextContentImpl and contains a
 * factory mechanism to create such objects.
 *
 * @author seidel
 */
public interface PlainTextContent extends PlainTextElement
{
  /*****************************************************************
   * PlainTextContentFactory inner class
   *****************************************************************/

  public static final class PlainTextContentFactory
  {
    /** Factory instance for Singleton pattern */
    private static PlainTextContentFactory instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private PlainTextContentFactory()
    {
      // Empty implementation
    }

    /**
     * Create a new factory instance, if it does not
     * yet exist, and return the object.
     *
     * @return PlainTextContentFactory object
     */
    public static synchronized PlainTextContentFactory getInstance()
    {
      if (null == PlainTextContentFactory.instance)
      {
        PlainTextContentFactory.instance = new PlainTextContentFactory();
      }

      return PlainTextContentFactory.instance;
    }

    /**
     * Create a new PlainTextContent object with empty code.
     *
     * @return PlainTextContentImpl object
     */
    public PlainTextContentImpl create()
    {
      return new PlainTextContentImpl();
    }

    /**
     * Create a new PlainTextContent object with given lines of code.
     *
     * @param codeLines Lines of code for initialization
     *
     * @return PlainTextContentImpl object
     */
    public PlainTextContentImpl create(final String... codeLines)
    {
      return new PlainTextContentImpl(codeLines);
    }
  }

  /*****************************************************************
   * PlainTextContent outer interface
   *****************************************************************/

  /** Factory instance for object creation */
  public static final PlainTextContentFactory factory = PlainTextContentFactory.getInstance();

  public PlainTextContent setCode(final String code);
  public String getCode();

  public PlainTextContent prependCode(final String... codeLines);
  public PlainTextContent appendCode(final String... codeLines);

  public PlainTextContent clearCode();
}
